package com.example.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val name: String,
    val email: String
)
