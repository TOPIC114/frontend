package com.example.idiotchefassistant.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.idiotchefassistant.R

class RegisterViewModel(private val registerRepository: RegisterRepository) : ViewModel() {
    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm
    private val _registerResult = MutableLiveData<String>()
    val registerResult: LiveData<String> = _registerResult


    fun register(infoData: RegisterData){
        val result = registerRepository.register(infoData).message
        if(result.isNotEmpty()){
            _registerResult.value = result
        }
        else{
            _registerResult.value = "註冊失敗"
        }
    }

    fun registerDataChange(infoData: RegisterData) {
       if(!isEmailValid(infoData.email)){
           _registerForm.value = RegisterFormState(emailError = R.string.invalid_username)
       } else if (!isPasswordValid(infoData.password)) {
           _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
       } else if (!isPasswordRetypeValid(infoData.password, infoData.passwordRetype)) {
           _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password_retype)
       }
    }

    private fun isEmailValid(email: String): Boolean{
        return if (email.contains('@')){
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
        else{
            email.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean{
        return password.length > 5
    }

    private fun isPasswordRetypeValid(password1: String, password2: String): Boolean{
        return password1 == password2
    }
}