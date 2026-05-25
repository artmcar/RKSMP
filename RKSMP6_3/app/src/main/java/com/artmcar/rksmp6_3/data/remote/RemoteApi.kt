package com.artmcar.rksmp6_3.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


data class LoginRequestDto(val username: String, val password: String)

data class LoginResponseDto(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val image: String?,
    val accessToken: String,
    val refreshToken: String? = null
)

data class UsersPageDto(
    val users: List<UserDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val image: String? = null,
    val phone: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val birthDate: String? = null
)

interface RemoteApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @GET("users")
    suspend fun getUsers(): UsersPageDto

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto
}