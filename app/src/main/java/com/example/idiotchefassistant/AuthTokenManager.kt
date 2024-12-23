package com.example.idiotchefassistant

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "settings")

object AuthTokenManager {
    private val tokenKey = stringPreferencesKey("auth_token")
    private var _authToken = MutableStateFlow<String?>(null)
    val authToken = _authToken.asStateFlow()

    fun initialize(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            loadInitialToken(context)
        }
    }

    private suspend  fun loadInitialToken(context: Context) {
        context.dataStore.data.map { preferences ->
            preferences[tokenKey]
        }.collect { token ->
            _authToken.value = token
        }
    }

    fun getAuthTokenSync(): String? = _authToken.value

    suspend fun saveAuthToken(context: Context, token: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
        _authToken.value = token
    }

    suspend fun clearAuthToken(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }
        _authToken.value = null
    }
}
