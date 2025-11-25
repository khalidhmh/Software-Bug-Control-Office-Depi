package com.example.mda.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie
import com.example.mda.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: FavoritesRepository) : ViewModel() {

    private val _favorites = MutableStateFlow<List<MediaEntity>>(emptyList())
    val favorites: StateFlow<List<MediaEntity>> = _favorites.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { favoritesList ->
                _favorites.value = favoritesList
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            val isNowFavorite = repository.toggleFavorite(movie)
            _snackbarMessage.value = if (isNowFavorite) {
                "تمت الإضافة إلى المفضلة ❤️"
            } else {
                "تمت الإزالة من المفضلة 🖤"
            }
        }
    }

    fun isFavorite(id: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.isFavorite(id)
            callback(result)
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
    fun clearLocalFavorites() {
        viewModelScope.launch {
            repository.clearAllLocalFavorites()
        }
    }

    fun syncFavoritesFromTmdb() {
        viewModelScope.launch {
            repository.syncFavoritesFromTmdb()
        }
    }





}
