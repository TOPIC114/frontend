package com.example.idiotchefassistant

import com.example.idiotchefassistant.recipe.RecipeItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginResponse(
    val token:String
)

data class LoginRequestBody(
    val username:String,
    val password:String
)

data class RegisterRequestBody(
    val username:String,
    val password:String,
    val email: String
)

data class UserResponse(
    val email:String,
    val level:Int,
    val username:String
)

data class MessageResponse(
    val message:String
)

interface User {

    @POST("/user/login")
    fun login(
        @Body loginRequestBody: LoginRequestBody
    ): Call<LoginResponse>

    @GET("/user/me")
    fun me(): Call<UserResponse>

    @GET("/user/logout")
    fun logout(): Call<MessageResponse>

    @GET("/user/recommend")
    fun recommend(): Call<List<RecipeItem>>

    @POST("/user/register")
    fun register(
        @Body registerRequestBody: RegisterRequestBody
    ): Call<MessageResponse>

    @GET("/user/history")
    fun history (): Call<List<RecipeItem>>
}