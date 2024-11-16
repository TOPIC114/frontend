package com.example.idiotchefassistant.ingredientsBlock

import retrofit2.http.GET
import retrofit2.Call

interface IngredientAPI {
    @GET("/ingredient/list")
    fun getList(): Call<ArrayList<IngredientItem>>
}

data class IngredientItem (
    val id: Int,
    val name: String,
    val mandarin: String
)