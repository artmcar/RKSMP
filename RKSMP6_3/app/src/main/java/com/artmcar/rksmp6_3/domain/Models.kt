package com.artmcar.rksmp6_3.domain

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val image: String?,
    val phone: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val birthDate: String? = null
) {
    val fullName: String get() = "$firstName $lastName"
}

data class AuthSession(val token: String, val user: User)