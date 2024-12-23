package com.example.idiotchefassistant.search

import com.example.idiotchefassistant.recipe.RecipeItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ResultSearchAPI {
    @GET("/recipes/search/iid")
    fun searchByIid(
        @Query("offset") offset: Int,
        @Query("iids") iids: List<Int>
    ): Call<List<RecipeItem>>

    @GET("/recipes/search/keyword")
    fun searchByKeyword(
        @Query("offset") offset: Int,
        @Query("keyword") keyword: String
    ): Call<List<RecipeItem>>
}