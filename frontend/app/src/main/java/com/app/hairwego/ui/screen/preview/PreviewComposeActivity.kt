package com.app.hairwego.ui.screen.preview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ahmetocak.shoppingapp.presentation.designsystem.theme.HairwegoAppTheme
import com.app.hairwego.R
import com.app.hairwego.data.Result
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.model.PredictResponse
import com.app.hairwego.helper.ImageClassifierHelper
import com.app.hairwego.ui.screen.camera.CameraComposeActivity
import com.app.hairwego.ui.screen.result.ResultActivityScreen
import com.google.gson.Gson
import kotlin.getOrThrow


class PreviewComposeActivity : ComponentActivity(), ImageClassifierHelper.ClassifierListener {

    private var imageUri: Uri? = null
    private var isFromCamera: Boolean = false
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var tokenManager: TokenManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = this,
            tokenManager = tokenManager
        )

        imageUri = intent.getStringExtra("imageUri")?.let { Uri.parse(it) }
        isFromCamera = intent.getBooleanExtra("isFromCamera", false)

        setContent {
            HairwegoAppTheme {
                imageUri?.let { uri ->
                    PreviewScreen(
                        imageUri = uri,
                        isFromCamera = isFromCamera,
                        onRetake = { handleRetake() },
                        onAnalyzeImage = { analyzeImage(imageUri!!)},
                        onShowTips = { showSnapTipsDialog() }
                    )
                } ?: run {
                    Toast.makeText(this, getString(R.string.image_not_find), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun analyzeImage(uri: Uri) {
        /*if (!isInternetAvailable()) {
            showNoInternetDialog()
            return
        }*/
        try {
            imageClassifierHelper.classifyImage(uri)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.error_analyze_image), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleRetake() {
        if (isFromCamera) {
            val intent = Intent(this, CameraComposeActivity::class.java)
            startActivity(intent)
        } else {
            val pickImage = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivity(Intent.createChooser(pickImage, "Select Picture"))
        }
        finish()
    }

    /*override fun onError(error: String) {
        showNoInternetDialog()
    }*/

    /*private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }*/

    private fun showNoInternetDialog() {
        // Use Compose Dialog or keep using AlertDialog with View.inflate
    }

    private fun showSnapTipsDialog() {
        // Use Compose AlertDialog
    }

    private fun showLowConfidenceDialog() {
        // Use Compose AlertDialog
    }

    override fun onResult(result: Result<PredictResponse>) {
        when (result) {
            is Result.Loading -> {
                // Show loading indicator if needed
            }
            is Result.Success -> {
                val predictResponse = result.data
                val gson = Gson()
                val responseJson = gson.toJson(predictResponse)

                val intent = Intent(this, ResultActivityScreen::class.java).apply {
                    putExtra("predictResponse", responseJson)
                }
                startActivity(intent)
            }
            is Result.Error -> {
                Toast.makeText(this, "Gagal memuat hasil prediksi: ${result.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onError(error: String) {
        TODO("Not yet implemented")
    }
}
