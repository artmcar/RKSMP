package com.artmcar.rksmp6_2.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.artmcar.rksmp6_2.domain.model.LaureateDetail
import com.artmcar.rksmp6_2.domain.usecase.GetLaureateDetailUseCase
import kotlinx.coroutines.launch


sealed interface MoreInfoUiState {
    data object Loading : MoreInfoUiState
    data class Success(val d: LaureateDetail) : MoreInfoUiState
    data class Error(val message: String) : MoreInfoUiState
}

class MoreInfoViewModel(
    private val useCase: GetLaureateDetailUseCase,
    private val id: String
) : ViewModel() {
    var state by mutableStateOf<MoreInfoUiState>(MoreInfoUiState.Loading); private set

    init { load() }

    fun load() {
        state = MoreInfoUiState.Loading
        viewModelScope.launch {
            state = try {
                MoreInfoUiState.Success(useCase(id))
            } catch (t: Throwable) {
                MoreInfoUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    companion object {
        fun factory(uc: GetLaureateDetailUseCase, id: String) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MoreInfoViewModel(uc, id) as T
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreInfoScreen(
    id: String,
    useCase: GetLaureateDetailUseCase,
    onBack: () -> Unit
) {
    val vm: MoreInfoViewModel = viewModel(
        factory = MoreInfoViewModel.factory(useCase, id),
        key = "laureate_$id"
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали премии") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val s = vm.state) {
                MoreInfoUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is MoreInfoUiState.Error -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ошибка: ${s.message}")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { vm.load() }) { Text("Повторить") }
                }
                is MoreInfoUiState.Success -> DetailBody(s.d)
            }
        }
    }
}

@Composable
private fun DetailBody(d: LaureateDetail) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        d.portraitUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = d.fullName,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().height(240.dp)
            )
            Spacer(Modifier.height(16.dp))
        }
        Text(d.fullName, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Text("${d.awardYear} - ${d.category}", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(16.dp))
        if (d.motivation.isNotBlank()) {
            Text("Мотивация:", style = MaterialTheme.typography.labelLarge)
            Text(d.motivation, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
        }
        d.birthCountry?.let {
            Text("Страна рождения: $it", style = MaterialTheme.typography.labelLarge)
        }
        d.birthPlace?.let {
            Text("Место рождения: $it", style = MaterialTheme.typography.bodyMedium)
        }
    }
}