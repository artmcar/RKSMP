package com.artmcar.rksmp6_1.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.artmcar.rksmp6_1.domain.model.Photo
import com.artmcar.rksmp6_1.domain.usecase.GetPhotoUseCase
import kotlinx.coroutines.launch


sealed interface PhotoListUiState {
    data object Loading : PhotoListUiState
    data class Success(val photos: List<Photo>) : PhotoListUiState
    data class Error(val message: String) : PhotoListUiState
}

class PhotoListViewModel(
    private val getPhoto: GetPhotoUseCase
) : ViewModel() {
    var state by mutableStateOf<PhotoListUiState>(PhotoListUiState.Loading)
        private set
    init { load() }
    fun load() {
        state = PhotoListUiState.Loading
        viewModelScope.launch {
            state = try {
                PhotoListUiState.Success(getPhoto())
            } catch (t: Throwable) {
                PhotoListUiState.Error(t.message ?: "Unknown error")
            }
        }
    }
    companion object {
        fun factory(getPhotos: GetPhotoUseCase) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                PhotoListViewModel(getPhotos) as T
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    getPhotos: GetPhotoUseCase,
    onPhotoClick: (String) -> Unit
) {
    val vm: PhotoListViewModel = viewModel(factory = PhotoListViewModel.factory(getPhotos))
    Scaffold(
        topBar = { TopAppBar(title = { Text("Каталог фотографий") }) }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val s = vm.state) {
                PhotoListUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is PhotoListUiState.Error -> ErrorView(s.message) { vm.load() }
                is PhotoListUiState.Success -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(s.photos, key = { it.id }) { p -> PhotoCard(p, onPhotoClick) }
                }
            }
        }
    }
}
@Composable
private fun PhotoCard(photo: Photo, onClick: (String) -> Unit) {
    Card(
        onClick = { onClick(photo.id) },
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    ){
        Column {
            AsyncImage(
                model = photo.thumbnailUrl,
                contentDescription = photo.author,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(160.dp)
            )
            Column(Modifier.padding(8.dp)) {
                Text(photo.author, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium)
                Text("${photo.width} x ${photo.height}",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ошибка: $message", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Попробовать снова") }
    }
}