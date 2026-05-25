package com.artmcar.rksmp6_1.di

import com.artmcar.rksmp6_1.data.RemoteApi
import com.artmcar.rksmp6_1.data.repository.PhotoRepositoryImpl
import com.artmcar.rksmp6_1.domain.repository.PhotoRepository
import com.artmcar.rksmp6_1.domain.usecase.GetPhotoUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationContainer {
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://picsum.photos/")
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: RemoteApi = retrofit.create(RemoteApi::class.java)

    val photoRepository: PhotoRepository = PhotoRepositoryImpl(api)
    val getPhotoUseCase: GetPhotoUseCase = GetPhotoUseCase(photoRepository)
}