package com.example.idiotchefassistant.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchFactory(private val searchRepository: SearchRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        return if(modelClass.isAssignableFrom(SearchViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            SearchViewModel(searchRepository) as T
        } else{
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}