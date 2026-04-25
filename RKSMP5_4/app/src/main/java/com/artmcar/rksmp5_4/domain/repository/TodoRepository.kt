package com.artmcar.rksmp5_4.domain.repository

import com.artmcar.rksmp5_4.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(): Flow<List<TodoItem>>
    suspend fun addTask(task: TodoItem)
    suspend fun toggleTodo(id: Int)
    suspend fun deleteTodo(id: Int)
    suspend fun importFromJsonIfNeeded()
}