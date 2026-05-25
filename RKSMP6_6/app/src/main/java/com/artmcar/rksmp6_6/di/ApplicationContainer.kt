package com.artmcar.rksmp6_6.di

import android.content.Context
import com.artmcar.rksmp6_6.data.RemoteApi
import com.artmcar.rksmp6_6.data.TokenStore
import com.artmcar.rksmp6_6.data.repository.AuthRepositoryImpl
import com.artmcar.rksmp6_6.data.repository.FavoriteRepositoryImpl
import com.artmcar.rksmp6_6.data.repository.PrizeRepositoryImpl
import com.artmcar.rksmp6_6.domain.AuthRepository
import com.artmcar.rksmp6_6.domain.FavoriteRepository
import com.artmcar.rksmp6_6.domain.GetFavoritesUseCase
import com.artmcar.rksmp6_6.domain.GetPrizesUseCase
import com.artmcar.rksmp6_6.domain.LoginUseCase
import com.artmcar.rksmp6_6.domain.LogoutUseCase
import com.artmcar.rksmp6_6.domain.PrizeRepository
import com.artmcar.rksmp6_6.domain.ToggleFavoriteUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApplicationContainer(appContext: Context) {
    companion object {
        const val BASE_URL = "http://10.0.2.2:8080"
    }

    val tokenStore = TokenStore(appContext)

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
    }

    private val api = RemoteApi(client, BASE_URL)

    val authRepository: AuthRepository = AuthRepositoryImpl(api, tokenStore)
    val prizeRepository: PrizeRepository = PrizeRepositoryImpl(api)
    val favoriteRepository: FavoriteRepository = FavoriteRepositoryImpl(api, tokenStore)

    val loginUseCase = LoginUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    val getPrizesUseCase = GetPrizesUseCase(prizeRepository)
    val getFavoritesUseCase = GetFavoritesUseCase(favoriteRepository)
    val toggleFavoriteUseCase = ToggleFavoriteUseCase(favoriteRepository)
}