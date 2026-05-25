package com.artmcar.rksmp6_3.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.artmcar.rksmp6_3.domain.LoginUseCase
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Error(val message: String) : LoginUiState
    data object Success : LoginUiState
}

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    var username by mutableStateOf(""); private set
    var password by mutableStateOf(""); private set
    var state by mutableStateOf<LoginUiState>(LoginUiState.Idle); private set

    fun onUsername(v: String) { username = v }
    fun onPassword(v: String) { password = v }

    fun submit() {
        if (username.isBlank() || password.isBlank()) {
            state = LoginUiState.Error("Заполните все поля"); return
        }
        state = LoginUiState.Loading
        viewModelScope.launch {
            state = try {
                loginUseCase(username, password)
                LoginUiState.Success
            } catch (t: Throwable) {
                val msg = t.message ?: ""
                LoginUiState.Error(
                    if (msg.contains("400") || msg.contains("401")) "Неверные данные"
                    else "Ошибка: $msg"
                )
            }
        }
    }

    fun reset() { state = LoginUiState.Idle }

    companion object {
        fun factory(loginUseCase: LoginUseCase) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                LoginViewModel(loginUseCase) as T
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginUseCase: LoginUseCase,
    onLoggedIn: () -> Unit
) {
    val vm: LoginViewModel = viewModel(factory = LoginViewModel.factory(loginUseCase))

    LaunchedEffect(vm.state) {
        if (vm.state is LoginUiState.Success) { onLoggedIn(); vm.reset() }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Вход") }) }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = vm.username,
                onValueChange = vm::onUsername,
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.password,
                onValueChange = vm::onPassword,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = vm::submit,
                enabled = vm.state !is LoginUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Войти") }
            Spacer(Modifier.height(16.dp))
            when (val s = vm.state) {
                LoginUiState.Loading -> CircularProgressIndicator()
                is LoginUiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
                else -> {}
            }
        }
    }
}
