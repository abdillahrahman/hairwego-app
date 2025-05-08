import os
from flask import Flask
from jinja2 import StrictUndefined
from flask_cors import CORS

from extensions import db, jwt
from admin import init_admin
from api import init_api

def create_app():
    app = Flask(__name__)
    app.config.from_pyfile("config.py")

    # Init extensions
    db.init_app(app)
    jwt.init_app(app)

    # Enable CORS
    CORS(app)

    # Register Blueprints
    init_admin(app)
    init_api(app)

    app.jinja_env.undefined = StrictUndefined
    return app

if __name__ == "__main__":
    app = create_app()
    app.run(host="0.0.0.0", port=5000, debug=True)
