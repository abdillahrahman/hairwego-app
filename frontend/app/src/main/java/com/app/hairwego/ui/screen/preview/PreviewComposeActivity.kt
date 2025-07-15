package com.app.hairwego.ui.screen.preview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.app.hairwego.R
import com.app.hairwego.data.Result
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.model.PredictResponse
import com.app.hairwego.helper.ImageClassifierHelper
import com.app.hairwego.ui.screen.camera.CameraComposeActivity
import com.app.hairwego.ui.screen.result.ResultActivityScreen
import com.app.hairwego.ui.theme.HairwegoThemeWrapper
import com.google.gson.Gson


class PreviewComposeActivity : ComponentActivity(), ImageClassifierHelper.ClassifierListener {

    private var imageUri: Uri? = null
    private var isFromCamera: Boolean = false
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    var isLoading by mutableStateOf(false)
        private set
    var showErrorDialog by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

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
            HairwegoThemeWrapper(context = applicationContext) {
                val showTipsDialog = remember { mutableStateOf(false) }

                imageUri?.let { uri ->
                    PreviewScreen(
                        imageUri = uri,
                        isFromCamera = isFromCamera,
                        isLoading = isLoading,
                        onRetake = { handleRetake() },
                        onAnalyzeImage = { analyzeImage(imageUri!!) },
                        onShowTips = { showTipsDialog.value = true },
                    )

                    if (showTipsDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showTipsDialog.value = false },
                            confirmButton = {
                                TextButton(onClick = { showTipsDialog.value = false }) {
                                    Text("OK", fontSize = 17.sp)
                                }
                            },
                            title = { Text("Snap Tips") },
                            text = {
                                Text(
                                    """
                                    Tips for Taking a Good Face Photo:
                                    
                                    • Make sure only one face is clearly visible in the frame.
                                    • Ensure proper lighting (avoid shadows).
                                    • Do not use blurry or pixelated images.
                                    • Face the camera directly, avoid tilting.
                                    • Remove accessories like sunglasses or masks.
                                    """.trimIndent(),
                                    fontSize = 16.sp
                                )
                            }
                        )
                    }

                    if (showErrorDialog) {
                        AlertDialog(
                            onDismissRequest = { showErrorDialog = false },
                            confirmButton = {
                                TextButton(onClick = { showErrorDialog = false }) {
                                    Text("OK", fontSize = 17.sp)
                                }
                            },
                            title = {
                                Text("An Error Occurred")
                            },
                            text = {
                                Text(
                                    errorMessage,
                                    fontSize = 16.sp
                                )
                            }
                        )
                    }
                } ?: run {
                    Toast.makeText(this, getString(R.string.image_not_find), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun analyzeImage(uri: Uri) {
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


    override fun onResult(result: Result<PredictResponse>) {
        when (result) {
            is Result.Loading -> {
                isLoading = true
            }

            is Result.Success -> {
                isLoading = false
                val predictResponse = result.data
                val gson = Gson()
                val responseJson = gson.toJson(predictResponse)

                val intent = Intent(this, ResultActivityScreen::class.java).apply {
                    putExtra("predictResponse", responseJson)
                }
                startActivity(intent)
            }

            is Result.Error -> {
                isLoading = false
                errorMessage = "${result.message}"
                showErrorDialog = true
            }
        }
    }


    override fun onError(error: String) {
        TODO("Not yet implemented")
    }
}
