package org.example.di

import org.example.data.database.DatabaseFactory
import org.example.data.database.DbConfig
import org.example.data.repository.FavoriteRepositoryImpl
import org.example.data.repository.PrizeRepositoryImpl
import org.example.data.repository.UserRepositoryImpl
import org.example.domain.AddFavoriteUseCase
import org.example.domain.AuthenticateUseCase
import org.example.domain.GetAllPrizesUseCase
import org.example.domain.GetFavoritesUseCase
import org.example.domain.GetProfileUseCase
import org.example.domain.RemoveFavoriteUseCase
import org.example.security.JwtConfig
import org.example.security.JwtSettings
import org.example.security.PasswordHasher
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

class AppModule(env: ApplicationEnvironment) {
    init {
        val cfg = DbConfig(
            jdbcUrl = env.config.property("db.jdbcUrl").getString(),
            user = env.config.property("db.user").getString(),
            password = env.config.property("db.password").getString(),
            maxPoolSize = env.config.property("db.maxPoolSize").getString().toInt()
        )
        DatabaseFactory.init(cfg)
    }

    private val userRepo = UserRepositoryImpl()
    private val prizeRepo = PrizeRepositoryImpl()
    private val favoriteRepo = FavoriteRepositoryImpl()

    init {
        runBlocking {
            userRepo.ensureSeed()
            prizeRepo.ensureSeed()
        }
    }

    val jwt = JwtConfig(
        JwtSettings(
            secret = env.config.property("jwt.secret").getString(),
            issuer = env.config.property("jwt.issuer").getString(),
            audience = env.config.property("jwt.audience").getString(),
            realm = env.config.property("jwt.realm").getString(),
            expirationMinutes = env.config.property("jwt.expirationMinutes").getString().toLong()
        )
    )

    val authenticate = AuthenticateUseCase(userRepo) { raw, hash -> PasswordHasher.verify(raw, hash) }
    val getAllPrizes = GetAllPrizesUseCase(prizeRepo)
    val getProfile = GetProfileUseCase(userRepo)
    val getFavorites = GetFavoritesUseCase(favoriteRepo)
    val addFavorite = AddFavoriteUseCase(favoriteRepo)
    val removeFavorite = RemoveFavoriteUseCase(favoriteRepo)
}