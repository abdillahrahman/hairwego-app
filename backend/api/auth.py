from flask import Blueprint, request, jsonify
from werkzeug.security import generate_password_hash, check_password_hash
from flask_jwt_extended import create_access_token, create_refresh_token, jwt_required, get_jwt_identity
from extensions import db
from models import User
import uuid

auth_bp = Blueprint("auth", __name__)

@auth_bp.route("/register", methods=["POST"])
def register():
    data = request.json
    username = data.get("username")
    email = data.get("email")
    password = data.get("password")

    if User.query.filter_by(email=email).first():
        return jsonify({"message": "Email sudah terdaftar"}), 409

    new_user = User(
        id=uuid.uuid4(),
        username=username,
        email=email,
        password=generate_password_hash(password),
    )
    db.session.add(new_user)
    db.session.commit()

    return jsonify({"message": "User berhasil terdaftar"}), 201

@auth_bp.route("/login", methods=["POST"])
def login():
    data = request.json
    email = data.get("email")
    password = data.get("password")

    user = User.query.filter_by(email=email).first()
    if user and check_password_hash(user.password, password):
        user_id = str(user.id)
        access_token = create_access_token(identity=user_id)
        refresh_token = create_refresh_token(identity=user_id)
        return jsonify({
            "access_token": access_token,
            "refresh_token": refresh_token
        }), 200

    return jsonify({"message": "Login gagal"}), 401

@auth_bp.route("/refresh-token", methods=["POST"])
@jwt_required(refresh=True)  # HANYA boleh pakai refresh token!
def refresh_token():    
    current_user = get_jwt_identity()
    new_access_token = create_access_token(identity=current_user)
    return jsonify({"access_token": new_access_token}), 200
