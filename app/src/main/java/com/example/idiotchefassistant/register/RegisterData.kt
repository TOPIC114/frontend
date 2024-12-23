package com.example.idiotchefassistant.register

data class RegisterData (
    val name: String,
    val email: String,
    val password: String,
    val passwordRetype: String
)