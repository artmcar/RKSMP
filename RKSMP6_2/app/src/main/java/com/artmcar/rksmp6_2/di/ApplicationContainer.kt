package com.artmcar.rksmp6_2.di

import com.artmcar.rksmp6_2.data.RemoteApi
import com.artmcar.rksmp6_2.data.repository.InfoRepositoryImpl
import com.artmcar.rksmp6_2.domain.repository.InfoRepository
import com.artmcar.rksmp6_2.domain.usecase.GetLaureateDetailUseCase
import com.artmcar.rksmp6_2.domain.usecase.GetLaureatesUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApplicationContainer {
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
        defaultRequest {
            header(HttpHeaders.UserAgent, "Mozilla/5.0 (Android; Mobile)")
        }
    }

    private val api = RemoteApi(client)
    val repository: InfoRepository = InfoRepositoryImpl(api)
    val getLaureatesUseCase = GetLaureatesUseCase(repository)
    val getLaureateDetailUseCase = GetLaureateDetailUseCase(repository)


}