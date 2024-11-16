package com.example.idiotchefassistant.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RegisterFactory: ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T{
        if(modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(
                registerRepository = RegisterRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}