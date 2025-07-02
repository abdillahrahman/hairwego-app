package com.app.hairwego.ui.screen.result

import android.content.Intent
import com.app.hairwego.data.model.PredictResponse
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import com.ahmetocak.shoppingapp.presentation.designsystem.theme.HairwegoAppTheme
import com.app.hairwego.MainActivity
import com.app.hairwego.data.local.HairWeGoDatabase
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.repository.HistoryRepository
import com.app.hairwego.ui.theme.HairwegoThemeWrapper
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ResultActivityScreen : ComponentActivity() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val responseJson = intent.getStringExtra("predictResponse")

        val gson = Gson()
        val predictResponse = gson.fromJson(responseJson, PredictResponse::class.java)

        setContent {
            HairwegoThemeWrapper(context = applicationContext) {
                ResultScreen(
                    faceShape = predictResponse.prediction,
                    predictionConfidence = predictResponse.confidence,
                    faceImage = predictResponse.imageScan,
                    recommendations = predictResponse.rekomendasi
                )
            }
        }

        val tokenManager = TokenManager(applicationContext)
        val dao = HairWeGoDatabase.getDatabase(applicationContext, applicationScope).historyDao()
        val repository = HistoryRepository(applicationContext, tokenManager)

        applicationScope.launch {
            try {
                repository.fetchAndSaveHistory(dao)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
