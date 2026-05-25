package com.artmcar.rksmp6_1.data.repository

import com.artmcar.rksmp6_1.data.PhotoDto
import com.artmcar.rksmp6_1.data.RemoteApi
import com.artmcar.rksmp6_1.domain.model.Photo
import com.artmcar.rksmp6_1.domain.repository.PhotoRepository

class PhotoRepositoryImpl(private val api: RemoteApi) : PhotoRepository {

    @Volatile private var cache: List<Photo> = emptyList()

    override suspend fun getPhotoById(id: String): Photo? =
        cache.firstOrNull {
            it.id == id
        } ?: getPhotos().firstOrNull {
            it.id == id
        }
    override suspend fun getPhotos(): List<Photo> {
        val photos = api.getPhotos().map {
            it.toDomain()
        }
        cache = photos
        return photos
    }
}
private fun PhotoDto.toDomain() = Photo(
    id = id,
    author = author,
    width = width,
    height = height,
    url = url,
    downloadUrl = downloadUrl
)