package com.artmcar.rksmp5_4.domain.usecase

import com.artmcar.rksmp5_4.domain.model.TodoItem
import com.artmcar.rksmp5_4.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class GetTodosUseCase(private val repository: TodoRepository) {
    operator fun invoke(): Flow<List<TodoItem>> {
        return repository.getTodos()
    }
}