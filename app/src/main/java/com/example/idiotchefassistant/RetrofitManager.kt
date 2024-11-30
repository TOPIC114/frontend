package com.example.idiotchefassistant

import DetectAPI
import com.example.idiotchefassistant.ingredientsBlock.IngredientAPI
import com.example.idiotchefassistant.recipeBlock.RecipeAPI
import com.example.idiotchefassistant.searchBlock.ResultSearchAPI
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS) // 設置連接超時時間
    .readTimeout(60, TimeUnit.SECONDS)    // 設置讀取超時時間
    .writeTimeout(60, TimeUnit.SECONDS)   // 設置寫入超時時間
    .build()

var retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://topic114.bntw.dev/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

val detectService: DetectAPI = retrofit.create(DetectAPI::class.java)
val ingredientService: IngredientAPI = retrofit.create(IngredientAPI::class.java)
val resultSearchService: ResultSearchAPI = retrofit.create(ResultSearchAPI::class.java)
val recipeService: RecipeAPI = retrofit.create(RecipeAPI::class.java)
val userService: User = retrofit.create(User::class.java)

val okHttpClientAddToken = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val token = AuthTokenManager.getAuthTokenSync()
        val request = chain.request().newBuilder()
            .addHeader("X-API-Key", "$token") // 這裡放入從 AuthTokenManager 中取得的 Token
            .build()
        chain.proceed(request)
    }
    .connectTimeout(60, TimeUnit.SECONDS) // 設置連接超時時間
    .readTimeout(60, TimeUnit.SECONDS)    // 設置讀取超時時間
    .writeTimeout(60, TimeUnit.SECONDS)   // 設置寫入超時時間
    .build()

var retrofitAddToken: Retrofit = Retrofit.Builder()
    .baseUrl("https://topic114.bntw.dev/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClientAddToken)
    .build()
val userDataService: User = retrofitAddToken.create(User::class.java)