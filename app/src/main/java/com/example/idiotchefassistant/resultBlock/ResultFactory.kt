package com.example.idiotchefassistant.resultBlock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ResultFactory(private val resultRepository: ResultRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(ResultViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            ResultViewModel(resultRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}