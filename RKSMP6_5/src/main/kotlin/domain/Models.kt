package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class Prize(
    val id: Int,
    val awardYear: Int,
    val category: String,
    val fullName: String,
    val motivation: String,
    val detailLink: String? = null,
    val laureates: List<Laureate> = emptyList()
)

@Serializable
data class Laureate(
    val id: Int,
    val prizeId: Int,
    val fullName: String,
    val portion: String,
    val motivation: String,
    val portraitUrl: String? = null
)

@Serializable
data class AppUser(
    val id: Int,
    val username: String,
    val passwordHash: String,
    val role: String
)

@Serializable
data class UserProfile(
    val id: Int,
    val username: String,
    val role: String
)