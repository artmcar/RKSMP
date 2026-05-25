package org.example.di

import io.ktor.server.application.*
import org.example.data.InMemoryPrizeRepository
import org.example.data.InMemoryUserRepository
import org.example.domain.AuthenticateUseCase
import org.example.domain.GetAllPrizesUseCase
import org.example.domain.GetLaureatesUseCase
import org.example.domain.GetPrizeUseCase
import org.example.security.JwtConfig
import org.example.security.JwtSettings


class ApplicationModule(env: ApplicationEnvironment) {
    private val prizeRepo = InMemoryPrizeRepository()
    private val userRepo = InMemoryUserRepository()

    val jwt = JwtConfig(
        JwtSettings(
            secret = env.config.property("jwt.secret").getString(),
            issuer = env.config.property("jwt.issuer").getString(),
            audience = env.config.property("jwt.audience").getString(),
            realm = env.config.property("jwt.realm").getString(),
            expirationMinutes = env.config.property("jwt.expirationMinutes").getString().toLong()
        )
    )

    val getAllPrizes = GetAllPrizesUseCase(prizeRepo)
    val getPrize = GetPrizeUseCase(prizeRepo)
    val getLaureates = GetLaureatesUseCase(prizeRepo)
    val authenticate = AuthenticateUseCase(userRepo) { raw, hash -> raw == hash }
}