package com.app.hairwego.ui.screen.camera


import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.Manifest
import androidx.compose.foundation.BorderStroke
import com.app.hairwego.R
import com.app.hairwego.R.string.flip_camera
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreenWithPermission(
    onClose: () -> Unit,
    onTakePicture: () -> Unit,
    onOpenGallery: () -> Unit,
    onFlipCamera : () -> Unit,
    previewView: PreviewView
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        CameraActivityScreen(
            onClose = onClose,
            onTakePicture = onTakePicture,
            onOpenGallery = onOpenGallery,
            onFlipCamera = onFlipCamera,
            previewView = previewView
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .wrapContentSize()
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val rationaleText = if (cameraPermissionState.status.shouldShowRationale) {
                "Whoops! Looks like we need your camera to work our magic!\n" +
                        "Don't worry, we just wanna see your pretty face (and maybe some cats).\n\n" +
                        "Grant us permission and let's get this party started!"
            } else {
                "Hi there! We need your camera to work our magic! ✨\n\n" +
                        "Grant us permission and let's get this party started! 🎉"
            }
            Text(text = rationaleText, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Unleash the Camera!")
            }
        }
    }
}

@Composable
fun CameraActivityScreen(
    onClose: () -> Unit,
    onTakePicture: () -> Unit,
    onOpenGallery: () -> Unit,
    onFlipCamera : () -> Unit,
    previewView: PreviewView
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera PreviewView
        AndroidView(
            factory = { context -> previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Close Button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(20.dp)
                .size(50.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(
            onClick = onFlipCamera,
            modifier = Modifier
                .padding(20.dp)
                .size(50.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(R.drawable.flip_camera),
                contentDescription = stringResource(id = flip_camera),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // Bottom panel
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(130.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 30.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Tombol Gallery di kiri
                    OutlinedButton(
                        onClick = onOpenGallery,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(text = stringResource(id = R.string.photos))
                    }
                }

                // Tombol ambil foto di tengah
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onTakePicture,
                        modifier = Modifier.size(100.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_take_image),
                            contentDescription = stringResource(id = R.string.take_image),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Tombol tips di kanan
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(
                        onClick = { /* TODO: tips click */ },
                        modifier = Modifier
                            .padding(start = 20.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tips),
                            contentDescription = stringResource(id = R.string.tips),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
fun CameraScreenPreview() {
    CameraActivityScreen(
        onClose = {},
        onTakePicture = {},
        onOpenGallery = {},
        onFlipCamera = {},
        previewView = PreviewView(LocalContext.current) // Ini tidak valid dalam @Preview
    )
}
