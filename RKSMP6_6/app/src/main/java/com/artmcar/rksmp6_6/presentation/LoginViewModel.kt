package com.artmcar.rksmp6_6.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.artmcar.rksmp6_6.domain.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state: StateFlow<UiState<Unit>> = _state.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = UiState.Error("Введите логин и пароль")
            return
        }
        _state.value = UiState.Loading
        viewModelScope.launch {
            runCatching { loginUseCase(username.trim(), password) }
                .onSuccess { _state.value = UiState.Success(Unit) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Ошибка входа") }
        }
    }

    fun reset() { _state.value = UiState.Idle }

    class Factory(private val loginUseCase: LoginUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LoginViewModel(loginUseCase) as T
    }
}