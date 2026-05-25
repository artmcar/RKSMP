package com.artmcar.rksmp6_6.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.nobelDataStore by preferencesDataStore("nobel_client_prefs")

class TokenStore(private val context: Context) {
    private val KEY = stringPreferencesKey("jwt")
    val tokenFlow: Flow<String?> = context.nobelDataStore.data.map { it[KEY] }
    suspend fun getToken(): String? = tokenFlow.first()
    suspend fun saveToken(token: String) { context.nobelDataStore.edit { it[KEY] = token } }
    suspend fun clear() { context.nobelDataStore.edit { it.remove(KEY) } }
}