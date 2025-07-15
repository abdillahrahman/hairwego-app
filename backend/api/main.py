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
from flask_jwt_extended import create_access_token, jwt_required, get_jwt_identity, verify_jwt_in_request
from flask_jwt_extended.exceptions import NoAuthorizationError

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
import logging
from functools import wraps


api_bp = Blueprint("api", __name__)

logging.basicConfig(
    filename='access.log',  
    level=logging.INFO,
    format='%(asctime)s %(levelname)s %(message)s'
)

def log_access(route_name):
    def decorator(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
            user_id = None
            try:
                user_id = get_jwt_identity()
            except Exception:
                pass
            logging.info(
                f"Route: {route_name} | User: {user_id} | Method: {request.method} | Path: {request.path} | IP: {request.remote_addr}"
            )
            return f(*args, **kwargs)
        return decorated_function
    return decorator


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

    enlarge_factor = 1.3
    center_x = x + w // 2
    center_y = y + h // 2
    new_w = int(w * enlarge_factor)
    new_h = int(h * enlarge_factor)
    new_x = max(0, center_x - new_w // 2)
    new_y = max(0, center_y - new_h // 2)
    new_x2 = min(image_rgb.shape[1], new_x + new_w)
    new_y2 = min(image_rgb.shape[0], new_y + new_h)

    cropped_face = image_rgb[new_y:new_y2, new_x:new_x2]
    cropped_bgr = cv2.cvtColor(cropped_face, cv2.COLOR_RGB2BGR)

    return cropped_bgr, None

def crop_and_save(image_path):
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

    enlarge_factor = 1.3
    center_x = x + w // 2
    center_y = y + h // 2
    new_w = int(w * enlarge_factor)
    new_h = int(h * enlarge_factor)

    top_offset = int(0.3 * h)
    bottom_offset = int(0.3 * h)
    left_offset = int(0.2 * w)
    right_offset = int(0.2 * w)

    new_y = max(0, center_y - new_h // 2 - top_offset)
    new_x = max(0, center_x - new_w // 2 - left_offset)
    new_x2 = min(image_rgb.shape[1], new_x + new_w + left_offset + right_offset)
    new_y2 = min(image_rgb.shape[0], new_y + new_h + top_offset + bottom_offset)

    cropped_face = image_rgb[new_y:new_y2, new_x:new_x2]
    cropped_bgr = cv2.cvtColor(cropped_face, cv2.COLOR_RGB2BGR)

    return cropped_bgr, None


@api_bp.route("/predict", methods=["POST"])
@log_access("predict")
def predict():
    if "file" not in request.files:
        return jsonify({"message": "No image in the request"}), 400

    files = request.files.getlist("file")
    filename = "temp_image.png"
    errors = {}
    success = False

    for file in files:
        if file and allowed_file(file.filename):
            file.save(os.path.join(UPLOAD_FOLDER, filename))
            success = True
        else:
            errors["message"] = f"File type of {file.filename} is not allowed"

    if not success:
        return jsonify(errors), 400

    img_path = os.path.join(UPLOAD_FOLDER, filename)
    correct_image_orientation(img_path)

    timestamp = datetime.now().strftime("%d%m%y-%H%M%S")
    saved_path = os.path.join(UPLOAD_FOLDER, f"{timestamp}.png")
    with Image.open(img_path) as im:
        if im.width > im.height:
            im = im.rotate(90, expand=True)
        im.save(saved_path)

    cropped_face_extend, error_message = crop_and_save(saved_path)
    if cropped_face_extend is None:
        return jsonify({"message": error_message}), 400
    cropped_path_extend = os.path.join(UPLOAD_FOLDER, f"cropped_extend_{timestamp}.png")
    cv2.imwrite(cropped_path_extend, cropped_face_extend)

    cropped_face, error_message = detect_face_and_crop(saved_path)
    if cropped_face is None:
        return jsonify({"message": error_message}), 400
    cropped_path = os.path.join(UPLOAD_FOLDER, f"cropped_{timestamp}.png")
    cv2.imwrite(cropped_path, cropped_face)

    # --- Prediksi ---
    img = image.load_img(cropped_path, target_size=(224, 224))
    x = image.img_to_array(img)
    x = x / 127.5 - 1
    x = np.expand_dims(x, axis=0)
    prediction_array_cnn = modelcnn.predict(x)

    class_names = ["Ovale", "Round", "Square"]
    predicted_class = class_names[np.argmax(prediction_array_cnn)]
    confidence = float(np.max(prediction_array_cnn))

    # Cek apakah user login
    try:
        verify_jwt_in_request(optional=True)
        user_id = get_jwt_identity()
    except NoAuthorizationError:
        user_id = None

    haircut_list = []

    # Ambil rekomendasi
    face_shape = FaceShape.query.filter_by(shape_name=predicted_class).first()
    if not face_shape:
        return jsonify({"message": "Face shape tidak ditemukan di database"}), 404

    recommendations = HaircutRecommendation.query.filter_by(
        face_shape_id=face_shape.id
    ).all()

    if not recommendations:
        return jsonify({"message": "Tidak ada rekomendasi untuk bentuk wajah ini"}), 404

    # Simpan ke DB hanya jika user login
    if user_id:
        user = User.query.get(user_id)
        if not user:
            return jsonify({"message": "User tidak ditemukan"}), 404

        new_scan = FaceScan(
            user_id=user.id,
            image_path=saved_path,
            image_path_cropped=cropped_path_extend,
            face_shape_id=face_shape.id,
            
        )
        db.session.add(new_scan)
        db.session.commit()

        for recommendation in recommendations:
            history = UserRecommendationHistory(
                user_id=user.id,
                haircut_recommendation_id=recommendation.id,
                face_scan_id=new_scan.id,
            )
            db.session.add(history)

        db.session.commit()

    # Format hasil untuk semua user (login dan guest)
    for recommendation in recommendations:
        for haircut in recommendation.haircuts:
            haircut_list.append(
                {
                    "haircut_name": haircut.haircut_name,  # ubah dari name ke haircut_name
                    "description": haircut.description,
                    "image_path": haircut.image_path,
                }
            )

    return (
        jsonify(
            {
                "prediction": predicted_class,
                "confidence": f"{confidence * 100:.2f}%",
                "image_scan": saved_path,
                "rekomendasi": haircut_list,
                "mode": "user" if user_id else "guest"
            }
        ),
        200,
    )


@api_bp.route("/history", methods=["GET"])
@log_access("get_history")
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
        scan_date = scan.scan_date.to('Asia/Jakarta').format("YYYY-MM-DD HH:mm:ss")
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
                            "haircut_name": haircut.haircut_name, 
                            "description": haircut.description,
                            "image_path": haircut.image_path,
                        }
                    )

        history[scan_date].append(
            {
                "face_scan_id": str(scan.id),
                "scan_date": scan_date,
                "scan_image": scan.image_path,
                "scan_image_cropped": scan.image_path_cropped,
                "face_shape": scan.face_shape.shape_name if scan.face_shape else "N/A",
                "recommendations": recommendation_details,
            }
        )

    return jsonify(history), 200

@api_bp.route("/history/<uuid:face_scan_id>", methods=["DELETE"])
@jwt_required()
def delete_history(face_scan_id):
    user_id = get_jwt_identity()

    face_scan = FaceScan.query.filter_by(id=face_scan_id, user_id=user_id).first()
    if not face_scan:
        return jsonify({"message": "History not found or unauthorized"}), 404

    # Hapus semua rekomendasi terkait
    UserRecommendationHistory.query.filter_by(face_scan_id=face_scan_id).delete()

    # Hapus face_scan
    db.session.delete(face_scan)
    db.session.commit()

    return jsonify({"message": "History deleted successfully"}), 200


@api_bp.route("/profile", methods=["GET"])
@jwt_required()
@log_access("get_profile")
def get_profile():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    
    if not user:
        return jsonify({"message": "User not found"}), 404
    
    scan_count = FaceScan.query.filter_by(user_id=user_id).count()
    
    latest_scan = FaceScan.query.filter_by(user_id=user_id).order_by(FaceScan.scan_date.desc()).first()
    latest_face_shape = latest_scan.face_shape.shape_name if latest_scan else None
    
    return jsonify({
        "fullname": user.full_name,
        "username": user.username,
        "email": user.email,
        "created_at": user.created_at.format("YYYY-MM-DD HH:mm:ss"),
        "total_scans": scan_count,
        "latest_face_shape": latest_face_shape
    }), 200

