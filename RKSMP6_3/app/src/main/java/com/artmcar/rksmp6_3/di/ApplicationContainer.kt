package com.artmcar.rksmp6_3.di

import android.content.Context
import com.artmcar.rksmp6_3.data.local.TokenStore
import com.artmcar.rksmp6_3.data.remote.AuthInterceptor
import com.artmcar.rksmp6_3.data.remote.RemoteApi
import com.artmcar.rksmp6_3.data.repository.AuthRepositoryImpl
import com.artmcar.rksmp6_3.data.repository.UserRepositoryImpl
import com.artmcar.rksmp6_3.domain.AuthRepository
import com.artmcar.rksmp6_3.domain.GetUserByIdUseCase
import com.artmcar.rksmp6_3.domain.GetUsersUseCase
import com.artmcar.rksmp6_3.domain.LoginUseCase
import com.artmcar.rksmp6_3.domain.LogoutUseCase
import com.artmcar.rksmp6_3.domain.UserRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApplicationContainer(appContext: Context) {
    val tokenStore = TokenStore(appContext)

    private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenStore))
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(RemoteApi::class.java)

    val authRepository: AuthRepository = AuthRepositoryImpl(api, tokenStore)
    val userRepository: UserRepository = UserRepositoryImpl(api)

    val loginUseCase = LoginUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    val getUsersUseCase = GetUsersUseCase(userRepository)
    val getUserByIdUseCase = GetUserByIdUseCase(userRepository)
}