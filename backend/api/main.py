import os
import shutil
import cv2
from mtcnn import MTCNN
import numpy as np
from datetime import datetime
from flask import Blueprint, request, jsonify
from keras.models import load_model
from keras.preprocessing import image
from PIL import Image
import matplotlib.pyplot as plt
from flask import Blueprint, request, jsonify
from werkzeug.security import generate_password_hash, check_password_hash
from flask_jwt_extended import create_access_token, jwt_required, get_jwt_identity
from extensions import db
from models import (
    FaceShape,
    FaceScan,
    HaircutRecommendation,
    UserRecommendationHistory,
    User,
    Haircut,
)
import uuid


api_bp = Blueprint("api", __name__)


# Load model once
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH = os.path.join(BASE_DIR, "best_model_final.h5")
modelcnn = load_model(MODEL_PATH)

# Load OpenCV face detection classifier
face_cascade = cv2.CascadeClassifier(
    cv2.data.haarcascades + "haarcascade_frontalface_default.xml"
)

UPLOAD_FOLDER = "static/uploads/"
ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg", "gif", "tiff", "webp", "jfif"}


def allowed_file(filename):
    return "." in filename and filename.rsplit(".", 1)[1].lower() in ALLOWED_EXTENSIONS

def correct_image_orientation(image_path):
    try:
        img = Image.open(image_path)

        # Cek apakah gambar punya data EXIF
        exif = img._getexif()
        if exif is not None:
            for orientation in ExifTags.TAGS.keys():
                if ExifTags.TAGS[orientation] == "Orientation":
                    break

            exif_orientation = exif.get(orientation)
            if exif_orientation == 3:
                img = img.rotate(180, expand=True)
            elif exif_orientation == 6:
                img = img.rotate(270, expand=True)
            elif exif_orientation == 8:
                img = img.rotate(90, expand=True)

        # Simpan ulang gambar setelah dikoreksi
        img.save(image_path)
        img.close()
    except Exception as e:
        print(f"Failed to correct orientation: {e}")

detector = MTCNN()

def detect_face_and_crop(image_path):
    image_bgr = cv2.imread(image_path)
    if image_bgr is None:
        return None, "Gagal membaca gambar."

    image_rgb = cv2.cvtColor(image_bgr, cv2.COLOR_BGR2RGB)
    results = detector.detect_faces(image_rgb)

    if not results:
        return None, "Tidak ada wajah terdeteksi."

    x, y, w, h = results[0]['box']
    x = max(0, x)
    y = max(0, y)
    cropped_face = image_rgb[y:y+h, x:x+w]
    cropped_bgr = cv2.cvtColor(cropped_face, cv2.COLOR_RGB2BGR)

    return cropped_bgr, None


@api_bp.route("/predict", methods=["POST"])
@jwt_required()
def predict():
    if "file" not in request.files:
        return jsonify({"message": "No image in the request"}), 400

    files = request.files.getlist("file")
    filename = "temp_image.png"
    errors = {}
    success = False

    # Simpan gambar yang di-upload
    for file in files:
        if file and allowed_file(file.filename):
            file.save(os.path.join(UPLOAD_FOLDER, filename))
            success = True
        else:
            errors["message"] = f"File type of {file.filename} is not allowed"

    if not success:
        return jsonify(errors), 400

    img_path = os.path.join(UPLOAD_FOLDER, filename)

    # Koreksi orientasi pada file upload
    correct_image_orientation(img_path)

    # Simpan hasil orientasi ke path unik dan pastikan portrait
    timestamp = datetime.now().strftime("%d%m%y-%H%M%S")
    saved_path = os.path.join(UPLOAD_FOLDER, f"{timestamp}.png")
    with Image.open(img_path) as im:
        if im.width > im.height:
            im = im.rotate(90, expand=True)
        im.save(saved_path)

    # --- Crop wajah ---
    cropped_face, error_message = detect_face_and_crop(saved_path)
    if cropped_face is None:
        return jsonify({"message": error_message}), 400

    cropped_path = os.path.join(UPLOAD_FOLDER, f"cropped_{timestamp}.png")
    cv2.imwrite(cropped_path, cropped_face)

    # Proses gambar crop untuk prediksi
    img = image.load_img(cropped_path, target_size=(224, 224))
    x = image.img_to_array(img)
    x = x / 127.5 - 1  # Normalisasi
    x = np.expand_dims(x, axis=0)

    # Prediksi menggunakan model CNN
    prediction_array_cnn = modelcnn.predict(x)
    class_names = ["ovale", "round", "square"]
    predicted_class = class_names[np.argmax(prediction_array_cnn)]
    confidence = float(np.max(prediction_array_cnn))

    # Kembalikan hasil prediksi
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    if not user:
        return jsonify({"message": "User tidak ditemukan"}), 404

    # Cari face_shape_id dari tabel FaceShape
    face_shape = FaceShape.query.filter_by(shape_name=predicted_class).first()
    if not face_shape:
        return jsonify({"message": "Face shape tidak ditemukan di database"}), 404

    # Simpan ke face_scan
    new_scan = FaceScan(
        user_id=user.id,
        image_path=cropped_path,
        face_shape_id=face_shape.id,
    )

    db.session.add(new_scan)
    db.session.commit()

    # Ambil rekomendasi berdasarkan face_shape
    recommendations = HaircutRecommendation.query.filter_by(
        face_shape_id=face_shape.id
    ).all()
    if not recommendations:
        return jsonify({"message": "Tidak ada rekomendasi untuk bentuk wajah ini"}), 404

    # Save to UserRecommendationHistory for each recommendation
    for recommendation in recommendations:
        history = UserRecommendationHistory(
            user_id=user.id,
            haircut_recommendation_id=recommendation.id,  # Use the id of each recommendation
            face_scan_id=new_scan.id,
        )
        db.session.add(history)

    db.session.commit()

    # Ambil detail haircut dari rekomendasi
    haircut_list = []
    for recommendation in recommendations:
        for haircut in recommendation.haircuts:
            haircut_list.append(
                {
                    "name": haircut.name,
                    "description": haircut.description,
                    "image": haircut.image,
                }
            )

    return (
        jsonify(
            {
                "prediction": predicted_class,
                "confidence": f"{confidence * 100:.2f}%",
                "image_scan" : saved_path,
                "rekomendasi": haircut_list,
            }
        ),
        200,
    )


@api_bp.route("/history", methods=["GET"])
@jwt_required()
def get_history():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    if not user:
        return jsonify({"message": "User not found"}), 404

    # Query FaceScan and group by scan_date
    face_scans = (
        FaceScan.query.filter_by(user_id=user_id)
        .order_by(FaceScan.scan_date.desc())
        .all()
    )

    history = {}
    for scan in face_scans:
        scan_date = scan.scan_date.format("YYYY-MM-DD HH:mm:ss")  # Format scan_date
        if scan_date not in history:
            history[scan_date] = []

        # Get recommendations for the current scan
        recommendations = UserRecommendationHistory.query.filter_by(
            face_scan_id=scan.id
        ).all()
        recommendation_details = []
        for recommendation in recommendations:
            haircut_recommendation = recommendation.haircut_recommendation
            if haircut_recommendation:
                for haircut in haircut_recommendation.haircuts:
                    recommendation_details.append(
                        {
                            "haircut_name": haircut.name,
                            "description": haircut.description,
                            "image": haircut.image,
                        }
                    )

        history[scan_date].append(
            {
                "face_scan_id": str(scan.id),
                "scan_image": scan.image_path,
                "face_shape": scan.face_shape.shape_name if scan.face_shape else "N/A",
                "recommendations": recommendation_details,
            }
        )

    return jsonify(history), 200


@api_bp.route("/history/<face_scan_id>", methods=["GET"])
@jwt_required()
def history_detail(face_scan_id):
    scan = FaceScan.query.filter_by(id=face_scan_id).first()
    if not scan:
        return jsonify({"message": "Face scan not found"}), 404

    face_shape = scan.face_shape.shape_name if scan.face_shape else None

    histories = UserRecommendationHistory.query.filter_by(face_scan_id=scan.id).all()
    recommendations = []
    for history in histories:
        rec = history.haircut_recommendation
        if rec:
            for haircut in rec.haircuts:
                recommendations.append({
                    "haircut_name": haircut.name,
                    "description": haircut.description,
                    "image": haircut.image
                })

    recommendations = [dict(t) for t in {tuple(d.items()) for d in recommendations}]

    return jsonify({
        "face_shape": face_shape,
        "recommendations": recommendations,
        "scan_image": scan.image_path
    })
