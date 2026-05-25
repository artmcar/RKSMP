package com.artmcar.rksmp6_1.domain.model

data class Photo(
    val id: String,
    val author: String,
    val url: String,
    val height: Int,
    val width: Int,
    val downloadUrl: String
) {
    val thumbnailUrl: String get() = "https://picsum.photos/id/$id/400/400"
}