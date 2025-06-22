package com.app.hairwego.ui.screen.HistoryDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.hairwego.ViewModelFactory
import com.app.hairwego.data.local.RecommendationEntity
import java.io.File

@Composable
fun HistoryDetailScreen(navController: NavController, faceScanId: String) {
    val context = LocalContext.current
    val viewModel: HistoryDetailViewModel = viewModel(factory = ViewModelFactory(context))

    LaunchedEffect(faceScanId) {
        viewModel.loadDetail(faceScanId)
    }

    val detail by viewModel.detail.collectAsState()

    detail?.let { data ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column {
                        AsyncImage(
                            model = File(data.faceScan.scanImage),
                            contentDescription = "Detected Face Shape",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = data.faceScan.faceShape,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp, bottom = 10.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Haircut Recommendation",
                    fontSize = 25.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(data.recommendations) { rec ->
                HaircutCard(rec)
            }
        }
    }
}

@Composable
fun HaircutCard(recommendation: RecommendationEntity) {
    val localImageFile = File(recommendation.image)

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recommendation.haircutName,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AsyncImage(
                model = localImageFile,
                contentDescription = recommendation.haircutName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recommendation.description,
                fontSize = 14.sp,
            )
        }
    }
}
