package com.example.idiotchefassistant.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.idiotchefassistant.AuthTokenManager
import com.example.idiotchefassistant.R
import com.example.idiotchefassistant.UserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm
    val loginResult: LiveData<String> = loginRepository.message
    val user: LiveData<UserResponse> = loginRepository.user

    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        loginRepository.setScope(viewModelScope)
    }

    fun login(username: String, password: String) {
        loginRepository.login(username, password)
        loginRepository.me()
    }

    fun logout(){
        loginRepository.logout()
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.isNotEmpty()) {
            true
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}