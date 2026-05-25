package org.example.domain

class AuthenticateUseCase(
    private val users: UserRepository,
    private val verify: (raw: String, hash: String) -> Boolean
) {
    suspend operator fun invoke(username: String, password: String): AppUser? {
        val user = users.findByUsername(username) ?: return null
        return if (verify(password, user.passwordHash)) user else null
    }
}

class GetAllPrizesUseCase(private val repo: PrizeRepository) {
    suspend operator fun invoke(): List<Prize> = repo.all()
}

class GetProfileUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(username: String): AppUser? = repo.findByUsername(username)
}

class GetFavoritesUseCase(private val repo: FavoriteRepository) {
    suspend operator fun invoke(userId: Int): List<Prize> = repo.favoritesOf(userId)
}

class AddFavoriteUseCase(private val repo: FavoriteRepository) {
    suspend operator fun invoke(userId: Int, prizeId: Int): Boolean = repo.add(userId, prizeId)
}

class RemoveFavoriteUseCase(private val repo: FavoriteRepository) {
    suspend operator fun invoke(userId: Int, prizeId: Int): Boolean = repo.remove(userId, prizeId)
}