from flask_admin.contrib import sqla
from flask_admin import Admin
from flask import redirect, url_for
from markupsafe import Markup
from wtforms import validators
from flask_admin.theme import Bootstrap4Theme
from flask_admin.form import FileUploadField
from flask import Blueprint, current_app
import os

from admin import db
from models import (
    User,
    FaceShape,
    FaceScan,
    Haircut,
    HaircutRecommendation,
    UserRecommendationHistory,
)


# Customized User model admin
class UserAdmin(sqla.ModelView):
    can_set_page_size = True
    page_size = 20
    column_list = ["id","username", "email", "created_at"]
    form_columns = ["username", "email", "password"]
    column_searchable_list = ["username", "email"]
    form_args = {
        "password": {
            "label": "Password",
            "validators": [validators.DataRequired()],
        }
    }


# Customized FaceShape model admin
class FaceShapeAdmin(sqla.ModelView):
    column_list = ["shape_name", "description"]
    form_columns = ["shape_name", "description"]


# Customized FaceScan model admin
class FaceScanAdmin(sqla.ModelView):
    column_list = ["id", "user", "face_shape", "image_path", "scan_date"]
    form_columns = ["user", "face_shape", "image_path"]
    column_searchable_list = ["user.username", "face_shape.shape_name"]

    # Formatter untuk face_shape
    def _format_face_shape(self, context, model, name):
        return model.face_shape.shape_name if model.face_shape else "N/A"

    # Formatter untuk user
    def _format_user(self, context, model, name):
        return model.user.username if model.user else "N/A"

    # def _format_image(self, context, model, name):
    #     if model.image_path:
    #         image_url = url_for('static', filename=f'uploads/{model.image_path}')
    #         return Markup(f'<img src="{image_url}" style="max-height: 100px;">')
    #     return ""


    column_formatters = {
        "face_shape": _format_face_shape,
        "user": _format_user,
        # "image_path": _format_image,
    }

    def __repr__(self):
        return f"<FaceScanAdmin(id={self.id})>"

# Customized HaircutRecommendation model admin
class HaircutRecommendationAdmin(sqla.ModelView):
    column_list = ["face_shape", "haircuts", "id"]
    form_columns = ["face_shape", "haircuts"]
    column_searchable_list = ["face_shape.shape_name"]

    def _list_haircuts(view, context, model, name):
        return ", ".join([haircut.name for haircut in model.haircuts])
    
    def _format_face_shape(view, context, model, name):
        return model.face_shape.shape_name if model.face_shape else "N/A"

    column_formatters = {
        "haircuts": _list_haircuts,
        "face_shape": _format_face_shape,
    }


# Customized UserRecommendationHistory model admin
class UserRecommendationHistoryAdmin(sqla.ModelView):
    column_list = ["user", "haircut_recommendation", "face_scan"]
    form_columns = ["user", "haircut_recommendation", "face_scan"]
    column_searchable_list = ["user.username", "haircut_recommendation.id"]

    # Format the user and haircut_recommendation columns
    def _format_user(view, context, model, name):
        return model.user.username if model.user else "N/A"

    def _format_haircut_recommendation(view, context, model, name):
        # Display the names of associated haircuts
        return ", ".join([haircut.name for haircut in model.haircut_recommendation.haircuts]) if model.haircut_recommendation else "N/A"

    def _format_face_scan(view, context, model, name):
        # Display only the face_scan ID
        return str(model.face_scan.id) if model.face_scan else "N/A"
    
    column_formatters = {
        "user": _format_user,
        "haircut_recommendation": _format_haircut_recommendation,
        "face_scan": _format_face_scan,
    }


# Customized Haircut model admin
class HaircutAdmin(sqla.ModelView):
    column_list = ["name", "description", "image"]
    form_columns = ["name", "description", "image"]
    column_searchable_list = ["name", "description"]

    # 📂 Folder tujuan upload
    file_path = os.path.join(os.path.dirname(__file__), "..")

    # 📤 Upload field
    form_extra_fields = {
        'image': FileUploadField(
            'Image',
            base_path=file_path,
            relative_path='static/uploads/',  # simpan di DB sebagai static/upload/namafile
            allow_overwrite=False
        )
    }

    # 🖼️ Format kolom gambar
    def _format_image(self, context, model, name):
        if model.image:
            # Remove 'static/uploads/' if present
            filename = model.image.replace('static/uploads/', '')
            image_url = url_for('static', filename=f'uploads/{filename}')
            return Markup(f'<img src="{image_url}" style="max-height: 100px;">')
        return ""

    column_formatters = {
        "image": _format_image
    }

    def __repr__(self):
        return f"<HaircutAdmin(name={self.name})>"

# Create Admin instance
admin_site = Admin(
    name="Admin Panel",
    theme=Bootstrap4Theme(swatch="default"),  # Set the base URL for the admin interface
)

# Add views for each model
admin_site.add_view(FaceShapeAdmin(FaceShape, db.session))
admin_site.add_view(HaircutAdmin(Haircut, db.session))
admin_site.add_view(HaircutRecommendationAdmin(HaircutRecommendation, db.session))
admin_site.add_view(UserAdmin(User, db.session))
admin_site.add_view(FaceScanAdmin(FaceScan, db.session))
admin_site.add_view(
    UserRecommendationHistoryAdmin(UserRecommendationHistory, db.session)
)

admin_bp = Blueprint("admin_bp", __name__)


@admin_bp.route("/")
def index():
    return redirect(
        url_for("admin.index")
    )  
