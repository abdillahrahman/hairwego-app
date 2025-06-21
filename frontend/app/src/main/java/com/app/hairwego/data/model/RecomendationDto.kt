package com.app.hairwego.data.model

import com.google.gson.annotations.SerializedName

data class RecommendationDto(
    @SerializedName("description")
    val description: String,

    @SerializedName("haircut_name")
    val haircutName: String,

    @SerializedName("image")
    val image: String
)

