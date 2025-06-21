package com.app.hairwego.ui.screen.History

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.app.hairwego.ViewModelFactory
import com.app.hairwego.data.local.FaceScanWithRecommendations
import java.io.File

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val viewModel: HistoryViewModel = viewModel(
        factory = ViewModelFactory(context)
    )

    val historyList by viewModel.history.observeAsState(emptyList())
    val isFetching by remember { viewModel.isFetching }
    val fetchError by remember { viewModel.fetchError }

    when {

        historyList.isEmpty() && !isFetching -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada riwayat scan")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(historyList) { scan ->
                    HistoryCard(scan)
                }
            }
        }
    }
}

@Composable
fun HistoryCard(data: FaceScanWithRecommendations) {
    val scanImageFile = File(data.faceScan.scanImage)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Log.d("HistoryCard", "scanImage path: ${data.faceScan.scanImage}")
            Image(
                painter = rememberAsyncImagePainter(model = scanImageFile),
                contentDescription = "Scan Image",
                modifier = Modifier
                    .size(125.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )

            Column {
                Text("Bentuk Wajah: ${data.faceScan.faceShape}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(50.dp))
                Text("Waktu Scan: ${data.faceScan.scanDate}", fontSize = 15.sp)
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
            scanDate = "2025-06-18 17:17:13"
        ),
        recommendations = emptyList()
    )

    HistoryCard(data = dummy)
}
