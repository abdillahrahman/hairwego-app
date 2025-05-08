import enum
import uuid

import arrow
from sqlalchemy import cast
from sqlalchemy import sql
from sqlalchemy.ext.hybrid import hybrid_property
from sqlalchemy_utils import ArrowType
from sqlalchemy_utils import ChoiceType
from sqlalchemy_utils import ColorType
from sqlalchemy_utils import CurrencyType
from sqlalchemy_utils import EmailType
from sqlalchemy_utils import IPAddressType
from sqlalchemy_utils import TimezoneType
from sqlalchemy_utils import URLType
from sqlalchemy_utils import UUIDType

from admin import db


class User(db.Model):
    __tablename__ = 'users'

    id = db.Column(UUIDType(binary=False), primary_key=True, default=uuid.uuid4)
    username = db.Column(db.String(50), nullable=False, unique=True)
    email = db.Column(EmailType, nullable=False, unique=True)
    password = db.Column(db.String(255), nullable=False)
    created_at = db.Column(ArrowType, default=arrow.utcnow)

    def __repr__(self):
        return f"<User(username={self.username}, email={self.email})>"

class FaceShape(db.Model):
    __tablename__ = 'face_shape'

    id = db.Column(UUIDType(binary=False), primary_key=True, default=uuid.uuid4)
    shape_name = db.Column(db.String(50), nullable=False, unique=True)
    description = db.Column(db.Text, nullable=True)

    def __repr__(self):
        return f"<FaceShape(shape_name={self.shape_name})>"

class FaceScan(db.Model):
    __tablename__ = 'face_scan'

    id = db.Column(UUIDType(binary=False), primary_key=True, default=uuid.uuid4)
    user_id = db.Column(UUIDType(binary=False), db.ForeignKey('users.id'), nullable=False)
    image_path = db.Column(db.String(255), nullable=False)
    face_shape_id = db.Column(UUIDType(binary=False), db.ForeignKey('face_shape.id'), nullable=False)
    scan_date = db.Column(ArrowType, default=arrow.utcnow)

    user = db.relationship('User', backref=db.backref('face_scans', lazy=True))
    face_shape = db.relationship('FaceShape', backref=db.backref('face_scans', lazy=True))

    def __repr__(self):
        return f"<FaceScan(user_id={self.user_id}, face_shape_id={self.face_shape_id})>"
    
class Haircut(db.Model):
    __tablename__ = 'haircut'

    id = db.Column(UUIDType(binary=False), primary_key=True, default=uuid.uuid4)
    name = db.Column(db.String(100), nullable=False)
    description = db.Column(db.Text, nullable=True)
    image = db.Column(db.String(255), nullable=True)

    def __repr__(self):
        return f"<Haircut(name={self.name})>"
    
haircut_recommendation_assoc = db.Table(
    'haircut_recommendation_assoc',
    db.Column('recommendation_id', UUIDType(binary=False), db.ForeignKey('haircut_recommendation.id'), primary_key=True),
    db.Column('haircut_id', UUIDType(binary=False), db.ForeignKey('haircut.id'), primary_key=True)
)

class HaircutRecommendation(db.Model):
    __tablename__ = 'haircut_recommendation'

    id = db.Column(UUIDType(binary=False), primary_key=True, default=uuid.uuid4)
    face_shape_id = db.Column(UUIDType(binary=False), db.ForeignKey('face_shape.id'), nullable=False)

    face_shape = db.relationship('FaceShape', backref=db.backref('haircut_recommendations', lazy=True))
    haircuts = db.relationship('Haircut', secondary=haircut_recommendation_assoc, backref='recommendation_sources')

    def __repr__(self):
        return f"<HaircutRecommendation(id={self.id}, face_shape_id={self.face_shape_id})>"


class UserRecommendationHistory(db.Model):
    __tablename__ = 'user_recommendation_history'

    id = db.Column(UUIDType(binary=False), primary_key=True, default=uuid.uuid4)
    user_id = db.Column(UUIDType(binary=False), db.ForeignKey('users.id'), nullable=False)
    haircut_recommendation_id = db.Column(UUIDType(binary=False), db.ForeignKey('haircut_recommendation.id'), nullable=False)
    face_scan_id = db.Column(UUIDType(binary=False), db.ForeignKey('face_scan.id'), nullable=False)

    user = db.relationship('User', backref=db.backref('recommendation_histories', lazy=True))
    haircut_recommendation = db.relationship('HaircutRecommendation', backref=db.backref('recommendation_histories', lazy=True))
    face_scan = db.relationship('FaceScan', backref=db.backref('recommendation_histories', lazy=True))

    def __repr__(self):
        return f"<UserRecommendationHistory(user_id={self.user_id}, haircut_recommendation_id={self.haircut_recommendation_id}, face_scan_id={self.face_scan_id})>"



