package com.app.hairwego.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.hairwego.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.app.hairwego.ui.components.MyButton
import com.app.hairwego.R

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var token by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            token = tokenManager.token.first() // Get token once (suspend function)
        }
    }
    homeScreenContent(
        modifier = Modifier,
        onScanClicked = { }
    )
}

@Composable
fun homeScreenContent(
    modifier: Modifier,
    onScanClicked: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.face_shape_home),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Find Your Face Shape",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                text = "Discover the perfect hairstyle for your face shape with our personalized recommendations.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
            MyButton(modifier = modifier
                .padding(horizontal = 20.dp),
                buttonText = "Start Face Scan", onClick = onScanClicked)

        }
    }

}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen()
}
