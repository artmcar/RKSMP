package com.artmcar.rksmp5_4.data.repository

import com.artmcar.rksmp5_4.data.local.TodoDao
import com.artmcar.rksmp5_4.data.local.TodoJsonDataSource
import com.artmcar.rksmp5_4.data.mapper.toDomain
import com.artmcar.rksmp5_4.data.mapper.toEntity
import com.artmcar.rksmp5_4.domain.model.TodoItem
import com.artmcar.rksmp5_4.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



class TodoRepositoryImpl(
    private val jsonDataSource: TodoJsonDataSource,
    private val dao: TodoDao
) : TodoRepository {

    override fun getTodos(): Flow<List<TodoItem>> =
        dao.getAllTodos().map { it.map { e -> e.toDomain() } }

    override suspend fun addTask(task: TodoItem) {
        dao.insert(task.toEntity().copy(id = 0))
    }

    override suspend fun toggleTodo(id: Int) {
        val todo = dao.getTodoById(id) ?: return
        dao.update(todo.copy(isCompleted = !todo.isCompleted))
    }

    override suspend fun deleteTodo(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun importFromJsonIfNeeded() {
        if (dao.getCount() == 0) {
            val todos = jsonDataSource.getTodos().map { it.toEntity() }
            dao.insertAll(todos)
        }
    }
}