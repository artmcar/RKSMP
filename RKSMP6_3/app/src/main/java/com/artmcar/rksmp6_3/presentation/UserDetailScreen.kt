package com.artmcar.rksmp6_3.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.artmcar.rksmp6_3.domain.GetUserByIdUseCase
import com.artmcar.rksmp6_3.domain.LogoutUseCase
import com.artmcar.rksmp6_3.domain.User
import kotlinx.coroutines.launch

sealed interface UserDetailUiState {
    data object Loading : UserDetailUiState
    data class Success(val user: User) : UserDetailUiState
    data class Error(val message: String) : UserDetailUiState
}

class UserDetailViewModel(
    private val getById: GetUserByIdUseCase,
    private val logout: LogoutUseCase,
    private val id: Int
) : ViewModel() {
    var state by mutableStateOf<UserDetailUiState>(UserDetailUiState.Loading); private set

    init { load() }

    fun load() {
        state = UserDetailUiState.Loading
        viewModelScope.launch {
            state = try {
                UserDetailUiState.Success(getById(id))
            } catch (t: Throwable) {
                UserDetailUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            logout.invoke()
            onDone()
        }
    }

    companion object {
        fun factory(g: GetUserByIdUseCase, l: LogoutUseCase, id: Int) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    UserDetailViewModel(g, l, id) as T
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    id: Int,
    getById: GetUserByIdUseCase,
    logoutUseCase: LogoutUseCase,
    onBack: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val vm: UserDetailViewModel = viewModel(
        factory = UserDetailViewModel.factory(getById, logoutUseCase, id),
        key = "user_$id"
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали пользователя") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val s = vm.state) {
                UserDetailUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UserDetailUiState.Error -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ошибка: ${s.message}")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { vm.load() }) { Text("Повторить") }
                }
                is UserDetailUiState.Success -> {
                    val u = s.user
                    Column(
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = u.image,
                            contentDescription = u.fullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(180.dp).clip(CircleShape)

                        )
                        Spacer(Modifier.height(16.dp))
                        Text(u.fullName, style = MaterialTheme.typography.headlineSmall)
                        Text(u.email, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        u.phone?.let { Text("Тел: $it") }
                        u.age?.let { Text("Возраст: $it") }
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { vm.logout(onLoggedOut) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Выйти") }
                    }
                }
            }
        }
    }
}
