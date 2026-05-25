package com.artmcar.rksmp6_6.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.artmcar.rksmp6_6.domain.GetFavoritesUseCase
import com.artmcar.rksmp6_6.domain.GetPrizesUseCase
import com.artmcar.rksmp6_6.domain.LogoutUseCase
import com.artmcar.rksmp6_6.domain.Prize
import com.artmcar.rksmp6_6.domain.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrizesState(
    val all: UiState<List<Prize>> = UiState.Loading,
    val favoriteIds: Set<Int> = emptySet(),
    val togglingId: Int? = null,
    val toastError: String? = null,
    val loggedOut: Boolean = false
)

class PrizesViewModel(
    private val getPrizes: GetPrizesUseCase,
    private val getFavorites: GetFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val logout: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PrizesState())
    val state: StateFlow<PrizesState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        _state.update { it.copy(all = UiState.Loading) }
        viewModelScope.launch {
            runCatching {
                val prizes = getPrizes()
                val favs = runCatching { getFavorites() }.getOrDefault(emptyList())
                prizes to favs.map { it.id }.toSet()
            }.onSuccess { (prizes, favs) ->
                _state.update { it.copy(all = UiState.Success(prizes), favoriteIds = favs) }
            }.onFailure { e ->
                _state.update { it.copy(all = UiState.Error(e.message ?: "Не удалось загрузить премии")) }
            }
        }
    }

    fun toggle(prizeId: Int) {
        val current = _state.value
        val isFav = prizeId in current.favoriteIds
        _state.update {
            it.copy(
                togglingId = prizeId,
                favoriteIds = if (isFav) it.favoriteIds - prizeId else it.favoriteIds + prizeId
            )
        }
        viewModelScope.launch {
            runCatching { toggleFavorite(prizeId, add = !isFav) }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            favoriteIds = if (isFav) it.favoriteIds + prizeId else it.favoriteIds - prizeId,
                            toastError = e.message ?: "Ошибка избранного"
                        )
                    }
                }
            _state.update { it.copy(togglingId = null) }
        }
    }

    fun clearToast() = _state.update { it.copy(toastError = null) }

    fun logout() {
        viewModelScope.launch {
            runCatching { logout.invoke() }
            _state.update { it.copy(loggedOut = true) }
        }
    }

    class Factory(
        private val getPrizes: GetPrizesUseCase,
        private val getFavorites: GetFavoritesUseCase,
        private val toggleFavorite: ToggleFavoriteUseCase,
        private val logout: LogoutUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PrizesViewModel(getPrizes, getFavorites, toggleFavorite, logout) as T
    }
}
