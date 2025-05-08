from flask import Blueprint, request, session
from flask_babel import Babel
from extensions import db  # Jangan inisialisasi lagi di sini

babel = Babel()

def get_locale():
    override = request.args.get("lang")
    if override:
        session["lang"] = override
    return session.get("lang", "en")

def init_admin(app):
    # Initialize Babel for localization
    babel.init_app(app, locale_selector=get_locale)

    # Register blueprint and initialize admin
    from .main import admin_site, admin_bp
    admin_site.init_app(app)
    app.register_blueprint(admin_bp, url_prefix="/admin")

    # Create all tables if they don't exist
    with app.app_context():  # Ensure the application context is active
        from models import User, FaceShape, FaceScan, Haircut, HaircutRecommendation, UserRecommendationHistory
        db.create_all()
