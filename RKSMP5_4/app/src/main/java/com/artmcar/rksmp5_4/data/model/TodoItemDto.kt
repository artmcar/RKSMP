package com.artmcar.rksmp5_4.data.model

data class TodoItemDto(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)