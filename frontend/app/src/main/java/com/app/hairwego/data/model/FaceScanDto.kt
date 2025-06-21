package com.app.hairwego.data.model

import com.google.gson.annotations.SerializedName

data class FaceScanDto(
    @SerializedName("face_scan_id")
    val faceScanId: String,

    @SerializedName("face_shape")
    val faceShape: String,

    @SerializedName("scan_image")
    val scanImage: String,

    @SerializedName("scan_date")
    val scanDate: String,

    @SerializedName("recommendations")
    val recommendations: List<RecommendationDto>
)

