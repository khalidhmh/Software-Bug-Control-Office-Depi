package com.example.mda.ui.screens.actors

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.Actor
import com.example.mda.data.repository.ActorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.mda.data.remote.model.KnownFor
import kotlin.collections.emptyList
import kotlin.collections.map


enum class ViewType { GRID, LIST }

class ActorViewModel(private val repository: ActorsRepository) : ViewModel() {

    private val _state = MutableStateFlow<ActorUiState>(ActorUiState.Loading)
    val state: StateFlow<ActorUiState> = _state

    private val _viewType = MutableStateFlow(ViewType.GRID)

    val gson = Gson()
    val type = object : TypeToken<List<KnownFor>>() {}.type

    val viewType: StateFlow<ViewType> = _viewType

    // ✅ --- الخطوة 1: إضافة متغير isRefreshing ---
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Pagination variables
    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    init {
        Log.d("ActorVM", "Initializing ActorViewModel")
        loadActors() // استدعاء loadActors بدلاً من loadMoreActors
        // ✅ تحويل knownFor من JSON string إلى List<KnownFor>

    }

    fun toggleViewType() {
        _viewType.value = if (_viewType.value == ViewType.GRID) ViewType.LIST else ViewType.GRID
        Log.d("ActorVM", "ViewType toggled: ${_viewType.value}")
    }

    // ✅ --- الخطوة 2: تعديل دالة loadActors ---
    fun loadActors(forceRefresh: Boolean = false) {
        if (isLoading && !forceRefresh) { // اسمحي بالتحديث القسري حتى لو كان التحميل جاريًا
            Log.d("ActorVM", "loadActors ignored: already loading")
            return
        }
        if (forceRefresh) {
            currentPage = 1
            isLastPage = false
        }

        Log.d("ActorVM", "loadActors called, forceRefresh=$forceRefresh, currentPage=$currentPage")
        viewModelScope.launch {
            isLoading = true
            // أخبري الواجهة بأن التحديث بدأ (فقط في حالة السحب)
            if (forceRefresh) {
                _isRefreshing.value = true
            }

            // إذا كانت هذه هي الصفحة الأولى، أظهري شاشة التحميل الرئيسية
            if (currentPage == 1 && !forceRefresh) {
                _state.value = ActorUiState.Loading
            }

            try {
                val newEntities = repository.getPopularActorsWithCache(page = currentPage)
                Log.d("ActorVM", "Fetched ${newEntities.size} actors from repository")

                if (newEntities.isEmpty() && currentPage == 1) {
                    _state.value = ActorUiState.Error("No actors found", ErrorType.NetworkError)
                    isLastPage = true
                } else {
                    val currentList = if (currentPage == 1) emptyList() else (_state.value as? ActorUiState.Success)?.actors.orEmpty()
                    val updatedList = (currentList + newEntities.map { entity ->
                        val knownForList = try {
                            // نحاول نحول النص اللي جوّا الكاش (لو موجود)
                            val field = entity::class.java.getDeclaredField("knownFor")
                            field.isAccessible = true
                            val jsonValue = field.get(entity) as? String
                            jsonValue?.let { gson.fromJson<List<KnownFor>>(it, type) } ?: emptyList()
                        } catch (e: Exception) {
                            emptyList()
                        }

                        Actor(
                            id = entity.id,
                            name = entity.name,
                            profilePath = entity.profilePath,
                            biography = entity.biography,
                            birthday = entity.birthday,
                            placeOfBirth = entity.placeOfBirth,
                            knownFor = knownForList
                        )
                    }).distinctBy { it.id }


                    _state.value = ActorUiState.Success(updatedList)
                    Log.d("ActorVM", "loadActors success, updated state with ${updatedList.size} actors")
                    currentPage++
                    if (newEntities.isEmpty()) {
                        isLastPage = true
                    }
                }
            } catch (e: Exception) {
                if ((_state.value as? ActorUiState.Success)?.actors.orEmpty().isEmpty()) {
                    _state.value = ActorUiState.Error(e.message ?: "Unknown error", ErrorType.NetworkError)
                }
                Log.d("ActorVM", "loadActors exception: ${e.localizedMessage}")
            } finally {
                isLoading = false
                // أخبري الواجهة بأن التحديث انتهى
                _isRefreshing.value = false
                Log.d("ActorVM", "loadActors finished, isLoading=false")
            }
        }
    }

    // ✅ --- الخطوة 3: تبسيط دالة loadMoreActors ---
    // الآن يمكنها فقط استدعاء loadActors
    fun loadMoreActors() {
        if (isLoading || isLastPage) {
            return
        }
        loadActors(forceRefresh = false)
    }

    fun retry() {
        Log.d("ActorVM", "retry called")
        loadActors(forceRefresh = true)
    }
}


