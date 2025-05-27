package com.app.hairwego.ui.screen.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.hairwego.data.model.RekomendasiItem

private const val BASE_URL = "http://192.168.2.229:5000/"

@Composable
fun ResultScreen(
    faceShape: String,
    predictionConfidence: String,
    faceImage: String,
    recommendations: List<RekomendasiItem>
) {
    val fullFaceImageUrl = BASE_URL + faceImage

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
                        model = fullFaceImageUrl,
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
                        text = faceShape,
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

        items(recommendations) { item ->
            HaircutCard(item)
        }
    }
}


@Composable
fun HaircutCard(recommendation: RekomendasiItem) {

    val fullImageUrl = BASE_URL + recommendation.image

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recommendation.name,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AsyncImage(
                model = fullImageUrl,
                contentDescription = recommendation.name,
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
