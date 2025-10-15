package com.example.mda.ui.screens.actors

import com.example.mda.data.remote.model.Actor

// Khalid: UI states used by ActorsViewModel and ActorsScreen
sealed class ActorUiState {
    object Loading : ActorUiState()
    data class Success(val actors: List<Actor>) : ActorUiState()
    data class Error(val message: String?, val type: ErrorType? = null) : ActorUiState()
}

sealed interface ErrorType {
    object NetworkError : ErrorType
    data class ApiError(val code: Int) : ErrorType
    data class UnknownError(val message: String?) : ErrorType
}
