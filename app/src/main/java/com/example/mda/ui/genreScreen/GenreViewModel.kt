package com.example.mda.ui.genreScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.Genre
import com.example.mda.data.repository.MoviesRepository
import kotlinx.coroutines.launch

class GenreViewModel(private val repository: MoviesRepository) : ViewModel(){
    var genres by mutableStateOf<List<Genre>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun loadGenres() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getGenres()
                genres = response.genres
                error =null
            }catch (e: Exception){
                error = e.localizedMessage
            }finally {
                    isLoading =false
            }
        }
    }

}
