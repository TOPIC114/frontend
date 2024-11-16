package com.example.idiotchefassistant.register

import android.util.Log
import com.example.idiotchefassistant.data.retrofit.endpoints.MessageResponse
import com.example.idiotchefassistant.data.retrofit.endpoints.RegisterRequestBody
import com.example.idiotchefassistant.userService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository {
    var data: RegisterData? = null

    fun register(data: RegisterData): MessageResponse{
        var result = MessageResponse(String())
        val registerRequestBody = RegisterRequestBody(data.name, data.email, data.password)
        userService.register(registerRequestBody).enqueue(
            object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful){
                        result = response.body()!!
                    }
                    else{
                        Log.i("register","Failed:${response.code()} ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Log.e("register", "API call failed: ${t.message}")
                }
            }
        )
        return result
    }
}