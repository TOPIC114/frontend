package com.example.idiotchefassistant.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.idiotchefassistant.MessageResponse
import com.example.idiotchefassistant.RegisterRequestBody
import com.example.idiotchefassistant.userService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository {
    val message = MutableLiveData<String>()

    fun register(data: RegisterData){
        val registerRequestBody = RegisterRequestBody(data.name, data.password, data.email)
        userService.register(registerRequestBody).enqueue(
            object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        message.postValue("註冊成功")
                        Log.i("Register Service","API Success:${response.code()} ${response.message()}")
                    }
                    else if(response.code() == 409){
                        val errorMessage = response.errorBody()?.string() ?: "該名稱或信箱已註冊"
                        Log.e("Register Service", "Conflict: $errorMessage")
                        message.postValue("註冊失敗: $errorMessage")
                    }
                    else{
                        Log.e("Register Service","API Failed:${response.code()} ${response.message()}")
                        message.postValue("註冊失敗")
                    }
                }
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Log.e("Register Service", "API call failed: ${t.message}")
                }
            }
        )
    }
}