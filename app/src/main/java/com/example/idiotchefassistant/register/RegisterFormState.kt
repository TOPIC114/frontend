package com.example.idiotchefassistant.register

data class RegisterFormState(
    val usernameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val passwordRtypeError: Int? = null,
    val isDataValid: Boolean = false
)