package com.artmcar.rksmp6_1.domain.repository

import com.artmcar.rksmp6_1.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(): List<Photo>
    suspend fun getPhotoById(id: String): Photo?
}