package com.example.mda.ui.actors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.repository.ActorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException


enum class ViewType { GRID, LIST }

class ActorViewModel(
    private val repository: ActorRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ActorUiState>(ActorUiState.Loading)
    val state: StateFlow<ActorUiState> = _state

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    private val _viewType = MutableStateFlow(ViewType.GRID)
    val viewType: StateFlow<ViewType> = _viewType

    fun toggleViewType() {
        _viewType.value = if (_viewType.value == ViewType.GRID) ViewType.LIST else ViewType.GRID
    }

    init {
        loadMoreActors()
    }

    fun loadMoreActors() {
        if (isLoading || isLastPage) return

        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getPopularActors(currentPage)
                if (response.isSuccessful) {
                    val actorResponse = response.body()
                    actorResponse?.let {
                        val newActors = it.results

                        val currentList = (state.value as? ActorUiState.Success)?.actors.orEmpty()

                        val updatedList = (currentList + newActors).toSet().toList()

                        _state.value = ActorUiState.Success(updatedList)


                        if (currentPage >= it.total_pages) {
                            isLastPage = true
                        } else {
                            currentPage++
                        }
                    }
                } else {

                    _state.value = ActorUiState.Error(ErrorType.ApiError(response.code()))
                }
            } catch (e: IOException) {

                _state.value = ActorUiState.Error(ErrorType.NetworkError)
            } catch (e: Exception) {

                _state.value = ActorUiState.Error(ErrorType.UnknownError(e.message))
            } finally {
                isLoading = false
            }
        }
    }
}
