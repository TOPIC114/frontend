package com.example.idiotchefassistant.data.retrofit

import com.example.idiotchefassistant.data.retrofit.endpoints.User
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val retrofitManager = RetrofitManager()

class RetrofitManager internal constructor() {

    val user:User

    init {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS) // 設置連線Timeout
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.9.51:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        this.user = retrofit.create(User::class.java)
    }
}