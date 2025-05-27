package com.app.hairwego.data.model

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("image_scan")
	val imageScan: String,

	@field:SerializedName("confidence")
	val confidence: String,

	@field:SerializedName("prediction")
	val prediction: String,

	@field:SerializedName("rekomendasi")
	val rekomendasi: List<RekomendasiItem>
)

data class RekomendasiItem(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String
)
