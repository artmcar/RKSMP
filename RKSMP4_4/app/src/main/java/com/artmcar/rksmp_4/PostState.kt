package com.artmcar.rksmp_4

sealed class PostState {
    object Loading: PostState()
    data class Ready(
        val avatar: String,
        val comments: List<Comment>
    ): PostState()
    object Error: PostState()
}
