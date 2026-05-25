package com.artmcar.rksmp6_3.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.artmcar.rksmp6_3.domain.GetUsersUseCase
import com.artmcar.rksmp6_3.domain.User
import kotlinx.coroutines.launch

sealed interface UsersUiState {
    data object Loading : UsersUiState
    data class Success(val users: List<User>) : UsersUiState
    data class Error(val message: String) : UsersUiState
}

class UsersViewModel(private val useCase: GetUsersUseCase) : ViewModel() {
    var state by mutableStateOf<UsersUiState>(UsersUiState.Loading); private set

    init { load() }

    fun load() {
        state = UsersUiState.Loading
        viewModelScope.launch {
            state = try {
                UsersUiState.Success(useCase())
            } catch (t: Throwable) {
                UsersUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    companion object {
        fun factory(uc: GetUsersUseCase) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                UsersViewModel(uc) as T
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersListScreen(
    getUsers: GetUsersUseCase,
    onUserClick: (Int) -> Unit
) {
    val vm: UsersViewModel = viewModel(factory = UsersViewModel.factory(getUsers))
    Scaffold(topBar = { TopAppBar(title = { Text("Пользователи") }) }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val s = vm.state) {
                UsersUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UsersUiState.Error -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ошибка: ${s.message}")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { vm.load() }) { Text("Повторить") }
                }
                is UsersUiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(s.users, key = { it.id }) { UserRow(it) { onUserClick(it.id) } }
                }
            }
        }
    }
}

@Composable
private fun UserRow(user: User, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = user.image,
                contentDescription = user.fullName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(user.fullName, style = MaterialTheme.typography.titleMedium)
                Text("@${user.username}", style = MaterialTheme.typography.bodySmall)
                Text(user.email, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}