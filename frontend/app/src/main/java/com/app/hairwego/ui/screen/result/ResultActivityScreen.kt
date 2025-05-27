package com.app.hairwego.ui.screen.result

import com.app.hairwego.data.model.PredictResponse
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ahmetocak.shoppingapp.presentation.designsystem.theme.HairwegoAppTheme
import com.google.gson.Gson

class ResultActivityScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil string JSON dari intent
        val responseJson = intent.getStringExtra("predictResponse")

        // Parse kembali ke model PredictResponse
        val gson = Gson()
        val predictResponse = gson.fromJson(responseJson, PredictResponse::class.java)

        // Tampilkan UI menggunakan Compose
        setContent {
            HairwegoAppTheme {
                ResultScreen(
                    faceShape = predictResponse.prediction,
                    predictionConfidence = predictResponse.confidence,
                    faceImage = predictResponse.imageScan,
                    recommendations = predictResponse.rekomendasi
                )
            }
        }
    }
}
