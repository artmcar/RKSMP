package com.artmcar.rksmp6_1.domain.usecase

import com.artmcar.rksmp6_1.domain.model.Photo
import com.artmcar.rksmp6_1.domain.repository.PhotoRepository

class GetPhotoUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(): List<Photo> = repository.getPhotos()
}