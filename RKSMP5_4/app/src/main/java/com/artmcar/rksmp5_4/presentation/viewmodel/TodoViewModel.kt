package com.artmcar.rksmp5_4.presentation.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artmcar.rksmp5_4.data.preferences.PreferencesKeys
import com.artmcar.rksmp5_4.domain.model.TodoItem
import com.artmcar.rksmp5_4.domain.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _useCompletedColor = MutableStateFlow(false)
    val useCompletedColor = _useCompletedColor.asStateFlow()

    val todos = repository.getTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.importFromJsonIfNeeded()

            dataStore.data.collect {
                _useCompletedColor.value =
                    it[PreferencesKeys.TASK_COMPLETED_COLOR] ?: false
            }
        }
    }

    fun toggleCompletedColor() = viewModelScope.launch {
        dataStore.edit {
            it[PreferencesKeys.TASK_COMPLETED_COLOR] = !_useCompletedColor.value
        }
    }

    fun addTask(title: String, desc: String?) = viewModelScope.launch {
        repository.addTask(TodoItem(0, title, desc, false))
    }

    fun toggleTodo(id: Int) = viewModelScope.launch {
        repository.toggleTodo(id)
    }

    fun deleteTodo(id: Int) = viewModelScope.launch {
        repository.deleteTodo(id)
    }
}