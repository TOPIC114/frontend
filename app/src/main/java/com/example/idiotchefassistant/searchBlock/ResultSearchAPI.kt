package com.example.idiotchefassistant.searchBlock

import com.example.idiotchefassistant.recipeBlock.RecipeItem
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