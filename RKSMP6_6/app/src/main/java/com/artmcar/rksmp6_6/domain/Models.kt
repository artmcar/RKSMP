package com.artmcar.rksmp6_6.domain

data class Prize(
    val id: Int,
    val awardYear: Int,
    val category: String,
    val fullName: String,
    val motivation: String,
    val detailLink: String?,
    val laureates: List<Laureate>
)

data class Laureate(
    val id: Int,
    val fullName: String,
    val portion: String,
    val motivation: String,
    val portraitUrl: String?
)

data class UserProfile(val id: Int, val username: String, val role: String)
