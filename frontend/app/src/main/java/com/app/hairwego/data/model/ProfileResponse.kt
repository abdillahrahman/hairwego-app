package com.app.hairwego.data.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("latest_face_shape")
	val latestFaceShape: String? = null,

	@field:SerializedName("total_scans")
	val totalScans: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("fullname")
	val fullname: String? = null
)
