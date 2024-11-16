package com.example.idiotchefassistant.ingredientsBlock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class IngredientFactory(private val ingredientRepository: IngredientRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(IngredientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
             IngredientViewModel(ingredientRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}