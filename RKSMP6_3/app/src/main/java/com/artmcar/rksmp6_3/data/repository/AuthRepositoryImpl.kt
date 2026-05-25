package com.artmcar.rksmp6_3.data.repository

import com.artmcar.rksmp6_3.data.local.TokenStore
import com.artmcar.rksmp6_3.data.remote.LoginRequestDto
import com.artmcar.rksmp6_3.data.remote.RemoteApi
import com.artmcar.rksmp6_3.data.remote.UserDto
import com.artmcar.rksmp6_3.domain.AuthRepository
import com.artmcar.rksmp6_3.domain.AuthSession
import com.artmcar.rksmp6_3.domain.User
import com.artmcar.rksmp6_3.domain.UserRepository
import kotlinx.coroutines.flow.Flow

internal fun UserDto.toDomain() = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    username = username,
    email = email,
    image = image,
    phone = phone,
    age = age,
    gender = gender,
    birthDate = birthDate
)

class AuthRepositoryImpl(
    private val api: RemoteApi,
    private val store: TokenStore
) : AuthRepository {
    override val tokenFlow: Flow<String?> = store.tokenFlow

    override suspend fun login(username: String, password: String): AuthSession {
        val r = api.login(LoginRequestDto(username, password))
        store.saveToken(r.accessToken)
        val user = User(
            id = r.id,
            firstName = r.firstName,
            lastName = r.lastName,
            username = r.username,
            email = r.email,
            image = r.image
        )
        return AuthSession(r.accessToken, user)
    }

    override suspend fun logout() {
        store.clear()
    }
}

class UserRepositoryImpl(private val api: RemoteApi) : UserRepository {
    override suspend fun getUsers(): List<User> = api.getUsers().users.map { it.toDomain() }
    override suspend fun getUserById(id: Int): User = api.getUserById(id).toDomain()
}