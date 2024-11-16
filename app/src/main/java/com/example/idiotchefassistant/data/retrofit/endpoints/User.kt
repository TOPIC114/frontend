package com.example.idiotchefassistant.data.retrofit.endpoints

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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
    val id:Int,
    val username:String,
    val email:String,
    val level:Int
)

data class MessageResponse(
    val message:String
)

data class SearchResponse(
    val id:Int,
    val name:String,
    val description:String,
    @SerializedName("video_link")  val videoLink:String,
    val rtype:Int
)

interface User {
    /*
     *  POST /user/login
     *  It's a login endpoint
     */
    @POST("/user/login")
    fun login(
        @Body loginRequestBody: LoginRequestBody
    ): Call<LoginResponse>
    /*
     *  GET /user/me
     *  To get the token owner's information
     */
    @GET("/user/me")
    fun me(
        @Header("X-API-Key") token:String
    ): Call<UserResponse>

    /*
     *  GET /user/logout
     *  To logout this session.
     */
    @GET("/user/logout")
    fun logout(
        @Header("X-API-Key") token:String
    ): Call<MessageResponse>

    /*
     *  GET /user/logout/all
     *  To logout all sessions.
     */
    @GET("/user/logout/all")
    fun logoutAll(
        @Header("X-API-Key") token:String
    ): Call<MessageResponse>

    /*
     *  POST /user/register
     *  To register a new user
     */
    @POST("/user/register")
    fun register(
        @Body loginRequestBody: RegisterRequestBody
    ): Call<MessageResponse>

    /*
     *  GET /user/searches
     *  To get search history of the token owner
     */
    @GET("/user/searches")
    fun searches(
        @Header("X-API-Key") token:String
    ): Call<SearchResponse>

}