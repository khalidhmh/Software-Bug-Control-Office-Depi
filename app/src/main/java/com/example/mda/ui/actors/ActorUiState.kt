package com.example.mda.ui.actors


import com.example.mda.data.remote.model.Actor


sealed interface ErrorType {
    data object NetworkError : ErrorType
    data class ApiError(val code: Int) : ErrorType
    data class UnknownError(val message: String?) : ErrorType
}

sealed interface ActorUiState {
    data object Loading : ActorUiState
    data class Success(val actors: List<Actor>) : ActorUiState
    data class Error(val type: ErrorType) : ActorUiState
}
