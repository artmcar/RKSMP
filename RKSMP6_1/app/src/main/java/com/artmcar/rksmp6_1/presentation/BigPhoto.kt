package com.artmcar.rksmp6_1.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.artmcar.rksmp6_1.domain.model.Photo
import com.artmcar.rksmp6_1.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

sealed interface BigPhotoUiState {
    data object Loading : BigPhotoUiState
    data class Success(val photo: Photo) : BigPhotoUiState
    data class Error(val message: String) : BigPhotoUiState
}

class BigPhotoViewModel(
    private val repo: PhotoRepository,
    private val photoId: String
) : ViewModel() {
    var state by mutableStateOf<BigPhotoUiState>(BigPhotoUiState.Loading)
        private set
    init { load() }
    fun load() {
        state = BigPhotoUiState.Loading
        viewModelScope.launch {
            state = try {
                val p = repo.getPhotoById(photoId) ?: error("Not found")
                BigPhotoUiState.Success(p)
            } catch (t: Throwable) {
                BigPhotoUiState.Error(t.message ?: "Unknown error")
            }
        }
    }
    companion object {
        fun factory(repo: PhotoRepository, photoId: String) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                BigPhotoViewModel(repo, photoId) as T
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BigPhotoScreen(
    photoId: String,
    repo: PhotoRepository,
    onBack: () -> Unit
) {
    val vm: BigPhotoViewModel = viewModel(
        factory = BigPhotoViewModel.factory(repo, photoId),
        key = "detail_$photoId"
    )
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var pendingUrl by remember { mutableStateOf<String?>(null) }
    val createDoc = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("image/jpeg")
    ) { uri: Uri? ->
        val url = pendingUrl ?: return@rememberLauncherForActivityResult
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    ctx.contentResolver.openOutputStream(uri).use { out ->
                        URL(url).openStream().use { input ->
                            input.copyTo(out!!)
                        }
                    }
                }
                Toast.makeText(ctx, "Сохранено", Toast.LENGTH_SHORT).show()
            } catch (t: Throwable) {
                Toast.makeText(ctx, "Ошибка: ${t.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фото") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val s = vm.state) {
                BigPhotoUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                is BigPhotoUiState.Error -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ошибка: ${s.message}")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { vm.load() }) { Text("Повторить") }
                }
                is BigPhotoUiState.Success -> Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
                ) {
                    AsyncImage(
                        model = s.photo.downloadUrl,
                        contentDescription = s.photo.author,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Автор: ${s.photo.author}", style = MaterialTheme.typography.titleMedium)
                    Text("Размер: ${s.photo.width} × ${s.photo.height}")
                    Text("URL: ${s.photo.url}", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            pendingUrl = s.photo.downloadUrl
                            createDoc.launch("photo_${s.photo.id}.jpg")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Скачать фото") }
                }
            }
        }
    }
}
