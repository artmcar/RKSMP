package com.artmcar.rksmp5_4.domain.usecase

import com.artmcar.rksmp5_4.domain.model.TodoItem
import com.artmcar.rksmp5_4.domain.repository.TodoRepository

class AddTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(task: TodoItem) {
        repository.addTask(task)
    }
}