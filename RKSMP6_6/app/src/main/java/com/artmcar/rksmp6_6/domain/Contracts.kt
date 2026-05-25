package com.artmcar.rksmp6_6.domain

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val tokenFlow: Flow<String?>
    suspend fun login(username: String, password: String)
    suspend fun logout()
}

interface PrizeRepository {
    suspend fun getPrizes(): List<Prize>
}

interface FavoriteRepository {
    suspend fun getFavorites(): List<Prize>
    suspend fun add(prizeId: Int)
    suspend fun remove(prizeId: Int)
}