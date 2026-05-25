package org.example.domain

interface UserRepository {
    suspend fun findByUsername(username: String): AppUser?
    suspend fun findById(id: Int): AppUser?
    suspend fun ensureSeed()
}

interface PrizeRepository {
    suspend fun all(): List<Prize>
    suspend fun findById(id: Int): Prize?
    suspend fun ensureSeed()
}

interface FavoriteRepository {
    suspend fun favoritesOf(userId: Int): List<Prize>
    suspend fun add(userId: Int, prizeId: Int): Boolean
    suspend fun remove(userId: Int, prizeId: Int): Boolean
}