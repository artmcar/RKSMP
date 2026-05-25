package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrize(
    val awardYear: Int,
    val category: String,
    val categoryFullName: String,
    val prizeAmount: Long,
    val dateAwarded: String?,
    val laureates: List<Laureate>
)

@Serializable
data class Laureate(
    val id: String,
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