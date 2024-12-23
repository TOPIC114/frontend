package com.example.idiotchefassistant
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MyApp:  Application() {
    override fun onCreate() {
        super.onCreate()
        AuthTokenManager.initialize(applicationContext)
    }
    companion object {
        private val _isLogin = MutableLiveData(false)
        val isLogin: LiveData<Boolean> = _isLogin

        fun setLogin(value: Boolean) {
            _isLogin.postValue(value)
        }
    }
}