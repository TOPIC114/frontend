package com.example.idiotchefassistant.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RecipeFactory (private val recipeRepository: RecipeRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        return if(modelClass.isAssignableFrom(RecipeViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            RecipeViewModel(recipeRepository) as T
        } else{
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}