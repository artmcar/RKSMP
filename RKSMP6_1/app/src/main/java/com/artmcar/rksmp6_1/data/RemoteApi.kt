package com.artmcar.rksmp6_1.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class PhotoDto(
    val id: String,
    val author: String,
    val url: String,
    val height: Int,
    val width: Int,
    @SerializedName("download_url") val downloadUrl: String
)
interface RemoteApi {
    @GET("v2/list")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30
    ): List<PhotoDto>
}