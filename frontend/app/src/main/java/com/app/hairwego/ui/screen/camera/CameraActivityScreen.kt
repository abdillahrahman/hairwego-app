package com.app.hairwego.ui.screen.camera


import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.hairwego.R
import com.app.hairwego.R.string.flip_camera
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreenWithPermission(
    onClose: () -> Unit,
    onTakePicture: () -> Unit,
    onOpenGallery: () -> Unit,
    onFlipCamera: () -> Unit,
    onShowTips: () -> Unit,
    previewView: PreviewView
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        CameraActivityScreen(
            onClose = onClose,
            onTakePicture = onTakePicture,
            onOpenGallery = onOpenGallery,
            onFlipCamera = onFlipCamera,
            onShowTips = onShowTips,
            previewView = previewView
        )
    }
}

@Composable
fun CameraActivityScreen(
    onClose: () -> Unit,
    onTakePicture: () -> Unit,
    onOpenGallery: () -> Unit,
    onFlipCamera: () -> Unit,
    onShowTips: () -> Unit,
    previewView: PreviewView
) {
    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            factory = { context -> previewView },
            modifier = Modifier.fillMaxSize()
        )

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

                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(
                        onClick = onShowTips,
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
        onShowTips = {},
        previewView = PreviewView(LocalContext.current)
    )
}
