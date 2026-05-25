package com.artmcar.rksmp6_3.domain

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val tokenFlow: Flow<String?>
    suspend fun login(username: String, password: String): AuthSession
    suspend fun logout()
}

interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun getUserById(id: Int): User
}