package com.example.idiotchefassistant.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.idiotchefassistant.AuthTokenManager
import com.example.idiotchefassistant.LoginRequestBody
import com.example.idiotchefassistant.LoginResponse
import com.example.idiotchefassistant.MessageResponse
import com.example.idiotchefassistant.MyApp
import com.example.idiotchefassistant.UserResponse
import com.example.idiotchefassistant.userDataService
import com.example.idiotchefassistant.userService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(private val authTokenManager: AuthTokenManager, private val context: Context) {
    private var scope: CoroutineScope? = null
    val message = MutableLiveData<String>()
    val user = MutableLiveData<UserResponse>()

    fun login(username: String, password: String) {
        val body = LoginRequestBody(username, password)
        userService.login(body).enqueue(
            object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            scope?.launch {
                                authTokenManager.saveAuthToken(context, responseBody.token)
                                authTokenManager.authToken.collect { token ->
                                    if (token != null)
                                        Log.i("Login Service", "Login success: $token")
                                    else
                                        Log.i("Login Service", "No token available")
                                }
                            }
                        }
                        message.postValue("登入成功")
                        MyApp.setLogin(true)
                    } else if (response.code() == 401) {
                        val errorMessage = "該名稱與信箱錯誤或密碼錯誤"
                        Log.e("Login Service", errorMessage)
                        message.postValue("登入失敗: $errorMessage")
                    } else {
                        Log.i(
                            "Login Service",
                            "API Failed:${response.code()} ${response.message()}"
                        )
                        message.postValue("登入失敗")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("Login Service", "API call failed: ${t.message}")
                    message.postValue("登入失敗")
                }
            }
        )
    }

    fun logout() {
        userDataService.logout().enqueue(
            object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            scope?.launch {
                                authTokenManager.clearAuthToken(context)
                            }
                        }
                        message.postValue("登出成功")
                        MyApp.setLogin(false)
                    } else {
                        Log.i(
                            "Logout Service",
                            "API Failed:${response.code()} ${response.message()}"
                        )
                        message.postValue("登出失敗")
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Log.e("Logout Service", "API call failed: ${t.message}")
                    message.postValue("登出失敗")
                }
            }
        )
    }

    fun me() {
        userDataService.me().enqueue(
            object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val data = UserResponse(it.email, it.level, it.username)
                            user.postValue(data)
                            Log.i("User Service", "Me: $it")
                        }
                    } else
                        Log.i("User Service", "API Failed:${response.code()} ${response.message()}")
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e("User Service", "API call failed: ${t.message}")
                }
            }
        )
    }

    fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }
}

