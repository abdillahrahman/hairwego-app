package com.app.hairwego.ui.screen.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.app.hairwego.R
import com.app.hairwego.createCustomTempFile
import com.app.hairwego.ui.screen.preview.PreviewComposeActivity
import com.app.hairwego.ui.theme.HairwegoThemeWrapper

class CameraComposeActivity : ComponentActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    private val openGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                openPreviewActivity(it, false)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewView = PreviewView(this)

        previewView.post {
            startCamera()
        }

        setContent {
            HairwegoThemeWrapper(context = applicationContext) {

                val showTipsDialog = remember { mutableStateOf(false) }

                CameraScreenWithPermission(
                    onClose = { finish() },
                    onTakePicture = { takePhoto() },
                    onOpenGallery = { openGallery() },
                    onFlipCamera = { flipCamera() },
                    onShowTips = { showTipsDialog.value = true },
                    previewView = previewView
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
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to bind camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun flipCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        startCamera()
    }

    private fun takePhoto() {

        val photoFile = createCustomTempFile(this)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    openPreviewActivity(savedUri, true)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.error_capturing),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun openGallery() {
        openGalleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun openPreviewActivity(uri: Uri, isFromCamera: Boolean) {
        val intent = Intent(this, PreviewComposeActivity::class.java)
        intent.putExtra("isFromCamera", isFromCamera)
        intent.putExtra("imageUri", uri.toString())
        startActivity(intent)
    }

}

