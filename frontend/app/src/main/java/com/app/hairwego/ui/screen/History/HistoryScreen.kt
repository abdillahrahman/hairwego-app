package com.app.hairwego.ui.screen.History

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.app.hairwego.ViewModelFactory
import com.app.hairwego.data.local.FaceScanWithRecommendations
import com.app.hairwego.ui.navigation.Screen
import java.io.File

@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: HistoryViewModel = viewModel(factory = ViewModelFactory(context))
    val historyList by viewModel.history.observeAsState(emptyList())
    val isFetching by remember { viewModel.isFetching }
    val fetchError by remember { viewModel.fetchError }

    // TokenManager untuk cek guest
    val tokenManager = remember { com.app.hairwego.data.local.TokenManager(context) }
    val isGuestState = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchHistoryIfNeeded()
        isGuestState.value = tokenManager.isGuest()
    }

    if (isFetching) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        when {
            isGuestState.value -> {
                // Tampilan khusus guest
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Login if you want view your scan history.",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            navController.navigate(Screen.Login.route)
                        }) {
                            Text("Login")
                        }
                    }
                }
            }

            historyList.isEmpty() && !isFetching -> {
                // Jika login tapi tidak ada data
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No scan history available yet.")
                }
            }

            else -> {
                // Tampilan daftar riwayat
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyList) { scan ->
                        HistoryCard(data = scan) { faceScanId ->
                            navController.navigate(Screen.HistoryDetail.createRoute(faceScanId))
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun HistoryCard(
    data: FaceScanWithRecommendations,
    onClick: (String) -> Unit
) {
    val scanImageFile = File(data.faceScan.scanImageCropped)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(data.faceScan.faceScanId) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Log.d("HistoryCard", "scanImage path: ${data.faceScan.scanImageCropped}")
            Image(
                painter = rememberAsyncImagePainter(model = scanImageFile),
                contentDescription = "Scan Image",
                modifier = Modifier
                    .size(125.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )

            Column {
                Text("Face Shape : ${data.faceScan.faceShape}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(50.dp))
                Text("Scan Date: ${data.faceScan.scanDate}", fontSize = 15.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHistoryCard() {
    val dummy = FaceScanWithRecommendations(
        faceScan = com.app.hairwego.data.local.FaceScanEntity(
            faceScanId = "1234",
            faceShape = "Round",
            scanImage = "",
            scanDate = "2025-06-18 17:17:13",
            scanImageCropped = ""
        ),
        recommendations = emptyList()
    )
}
