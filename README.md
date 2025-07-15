# HairWeGo

A full-stack application for hair style recommendations, featuring a Flask backend and an Android (Kotlin/Jetpack Compose) frontend.


## Features

- User authentication and profile management
- Face scan and analysis
- Hairstyle recommendation system
- History of scans and recommendations
- Admin dashboard (Flask-Admin)
- Modern Android UI



## Tech Stack

- **Backend:** Python, Flask, Flask-Admin, Flask-JWT-Extended, SQLAlchemy, PyMySQL
- **Frontend:** Kotlin, Jetpack Compose, Room, Retrofit, Hilt, CameraX, Coil, Lottie




## Backend Setup

1. **Clone the repository** and navigate to the backend folder:

   ```bash
   cd backend
   ```

2. **Create and activate a virtual environment:**

   ```bash
   python -m venv env
   source env/bin/activate  # On Windows: env\Scripts\activate
   ```

3. **Install dependencies:**

   ```bash
   pip install -r requirements.txt
   ```

4. **Run the application:**

   ```bash
   python app.py
   ```

   The backend will start on `http://0.0.0.0:5000`.



