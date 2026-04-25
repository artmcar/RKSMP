package com.artmcar.rksmp5_4.data.mapper

import com.artmcar.rksmp5_4.data.local.TodoEntity
import com.artmcar.rksmp5_4.data.model.TodoItemDto
import com.artmcar.rksmp5_4.domain.model.TodoItem

fun TodoEntity.toDomain() = TodoItem(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

fun TodoItem.toEntity() = TodoEntity(
    id = id,
    title = title,
    description = description ?: "",
    isCompleted = isCompleted
)

fun TodoItemDto.toEntity() = TodoEntity(
    id = 0,
    title = title,
    description = description,
    isCompleted = isCompleted
)