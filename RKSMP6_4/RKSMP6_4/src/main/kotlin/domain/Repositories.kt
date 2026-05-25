package org.example.domain

interface PrizeRepository {
    suspend fun all(): List<NobelPrize>
    suspend fun byYearAndCategory(year: Int, category: String): NobelPrize?
    suspend fun laureatesFor(year: Int, category: String): List<Laureate>
}

interface UserRepository {
    suspend fun findByUsername(username: String): AppUser?
}