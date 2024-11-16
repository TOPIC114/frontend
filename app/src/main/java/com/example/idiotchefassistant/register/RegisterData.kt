package com.example.idiotchefassistant.register

import android.provider.ContactsContract.CommonDataKinds.Email

data class RegisterData (
    val name: String,
    val email: String,
    val password: String,
    val passwordRetype: String
)