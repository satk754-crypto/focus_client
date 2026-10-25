package com.example.focuswellbeing.features.auth.domain

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthViewModel : ViewModel() {
    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    fun getAuthToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }
    }

    fun saveSession(context: Context, token: String, email: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[AUTH_TOKEN_KEY] = token
                preferences[USER_EMAIL_KEY] = email
            }
            onComplete()
        }
    }

    fun logout(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences.remove(AUTH_TOKEN_KEY)
                preferences.remove(USER_EMAIL_KEY)
            }
        }
    }
}
