package org.example.domain

class GetAllPrizesUseCase(private val repo: PrizeRepository) {
    suspend operator fun invoke(): List<NobelPrize> = repo.all()
}

class GetPrizeUseCase(private val repo: PrizeRepository) {
    suspend operator fun invoke(year: Int, category: String): NobelPrize? =
        repo.byYearAndCategory(year, category)
}

class GetLaureatesUseCase(private val repo: PrizeRepository) {
    suspend operator fun invoke(year: Int, category: String): List<Laureate> =
        repo.laureatesFor(year, category)
}

class AuthenticateUseCase(
    private val users: UserRepository,
    private val verify: (raw: String, hash: String) -> Boolean
) {
    suspend operator fun invoke(username: String, password: String): AppUser? {
        val user = users.findByUsername(username) ?: return null
        return if (verify(password, user.passwordHash)) user else null
    }
}