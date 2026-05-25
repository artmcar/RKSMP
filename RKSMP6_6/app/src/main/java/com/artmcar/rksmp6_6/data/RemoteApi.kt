package com.artmcar.rksmp6_6.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(val token: String, val expiresAt: Long)

@Serializable
data class PrizeDto(
    val id: Int,
    val awardYear: Int,
    val category: String,
    val fullName: String,
    val motivation: String,
    val detailLink: String? = null,
    val laureates: List<LaureateDto> = emptyList()
)

@Serializable
data class LaureateDto(
    val id: Int,
    val prizeId: Int,
    val fullName: String,
    val portion: String,
    val motivation: String,
    val portraitUrl: String? = null
)

@Serializable
data class ProfileDto(val id: Int, val username: String, val role: String)

class RemoteApi(private val client: HttpClient, private val baseUrl: String) {
    suspend fun login(username: String, password: String): LoginResponse =
        client.post("$baseUrl/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username, password))
        }.body()

    suspend fun getPrizes(): List<PrizeDto> =
        client.get("$baseUrl/prizes").body()

    suspend fun getProfile(token: String): ProfileDto =
        client.get("$baseUrl/users/me") { bearerAuth(token) }.body()

    suspend fun getFavorites(token: String): List<PrizeDto> =
        client.get("$baseUrl/users/me/prizes") { bearerAuth(token) }.body()

    suspend fun addFavorite(token: String, prizeId: Int) {
        client.post("$baseUrl/users/me/prizes/$prizeId") { bearerAuth(token) }
    }

    suspend fun removeFavorite(token: String, prizeId: Int) {
        client.delete("$baseUrl/users/me/prizes/$prizeId") { bearerAuth(token) }
    }
}
