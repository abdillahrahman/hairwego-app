package com.app.hairwego.data.model

data class RegisterRequest(
    val fullname: String,
    val username: String,
    val email: String,
    val password: String
)