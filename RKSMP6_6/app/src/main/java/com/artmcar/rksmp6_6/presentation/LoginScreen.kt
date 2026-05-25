package com.artmcar.rksmp6_6.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.artmcar.rksmp6_6.domain.LoginUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginUseCase: LoginUseCase,
    onLoggedIn: () -> Unit,
    vm: LoginViewModel = viewModel(factory = LoginViewModel.Factory(loginUseCase))
) {
    val state by vm.state.collectAsState()
    var username by rememberSaveable { mutableStateOf("alice") }
    var password by rememberSaveable { mutableStateOf("alice123") }

    LaunchedEffect(state) {
        if (state is UiState.Success<*>) {
            vm.reset()
            onLoggedIn()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Вход") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логин") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { vm.login(username, password) },
                enabled = state !is UiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Войти")
                }
            }

            val msg = (state as? UiState.Error)?.message
            if (msg != null) {
                Text(msg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
