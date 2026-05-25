package com.artmcar.rksmp6_6.domain

class LoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(username: String, password: String) = repo.login(username, password)
}

class LogoutUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.logout()
}

class GetPrizesUseCase(private val repo: PrizeRepository) {
    suspend operator fun invoke(): List<Prize> = repo.getPrizes()
}

class GetFavoritesUseCase(private val repo: FavoriteRepository) {
    suspend operator fun invoke(): List<Prize> = repo.getFavorites()
}

class ToggleFavoriteUseCase(private val repo: FavoriteRepository) {
    suspend operator fun invoke(prizeId: Int, add: Boolean) {
        if (add) repo.add(prizeId) else repo.remove(prizeId)
    }
}