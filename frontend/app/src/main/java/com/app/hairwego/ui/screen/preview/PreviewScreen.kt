package com.app.hairwego.ui.screen.preview

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.app.hairwego.R

@Composable
fun PreviewScreen(
    imageUri: Uri,
    isFromCamera: Boolean,
    onRetake: () -> Unit,
    onAnalyzeImage: () -> Unit,
    onShowTips: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = stringResource(R.string.image_preview),
            contentScale = if (isFromCamera) ContentScale.Crop else ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight() // Adjust height as needed
        )


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
                        onClick = onRetake,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isFromCamera)
                                stringResource(R.string.retake)
                            else
                                stringResource(R.string.photos)
                        )
                    }
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onAnalyzeImage,
                        modifier = Modifier.size(100.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = stringResource(R.string.analyze_image),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(95.dp)
                        )
                    }
                }

                // Kanan: IconButton (Tips)
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(
                        onClick = onShowTips,
                        modifier = Modifier
                            .padding(start = 20.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tips),
                            contentDescription = stringResource(R.string.tips),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
fun PreviewScreenPreview() {
    PreviewScreen(
        imageUri = Uri.parse("android.resource://com.app.hairwego/drawable/ic_placeholder"), // Mock URI
        isFromCamera = true,
        onRetake = { /* Mock Retake Action */ },
        onAnalyzeImage = { /* Mock Analyze Action */ },
        onShowTips = { /* Mock Show Tips Action */ }
    )
}