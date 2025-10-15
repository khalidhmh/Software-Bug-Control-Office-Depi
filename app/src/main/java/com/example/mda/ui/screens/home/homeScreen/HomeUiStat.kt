package com.example.mda.ui.screens.home.homeScreen

import com.example.mda.data.local.entities.MediaEntity

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val trending: List<MediaEntity>,
        val popular: List<MediaEntity>,
        val tv: List<MediaEntity>,
        val mixed: List<MediaEntity>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
