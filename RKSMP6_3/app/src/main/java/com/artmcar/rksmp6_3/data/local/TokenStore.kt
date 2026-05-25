package com.artmcar.rksmp6_3.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

class TokenStore(private val context: Context) {
    private val KEY_TOKEN = stringPreferencesKey("access_token")

    val tokenFlow: Flow<String?> = context.authDataStore.data.map { it[KEY_TOKEN] }

    suspend fun getToken(): String? = tokenFlow.first()

    suspend fun saveToken(token: String) {
        context.authDataStore.edit { it[KEY_TOKEN] = token }
    }

    suspend fun clear() {
        context.authDataStore.edit { it.remove(KEY_TOKEN) }
    }
}