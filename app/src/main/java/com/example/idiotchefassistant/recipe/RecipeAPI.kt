package com.example.idiotchefassistant.recipe

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class CommentBody(
    val rid: Int,
    val content: String,
    val rate: Int
)

data class MessageBody(
    val message: String
)

interface RecipeAPI {
    @GET("/recipes/content/{rid}")
    fun getRecipeContent(
        @Path("rid") rid: Int
    ): Call<RecipeData>

    @POST("/recipes/comment")
    fun postComment(
        @Body commentBody: CommentBody
    ): Call<MessageBody>
}
