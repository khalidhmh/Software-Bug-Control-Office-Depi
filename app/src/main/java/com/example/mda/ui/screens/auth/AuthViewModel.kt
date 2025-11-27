package com.example.mda.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.auth.AccountDetails
import com.example.mda.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val authUrl: String? = null,
    val requestToken: String? = null,
    val accountDetails: AccountDetails? = null,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.getSessionId().first() != null
            _uiState.value = _uiState.value.copy(isAuthenticated = isLoggedIn)
        }
    }

    /**
     * Step 1: Start the authentication flow by creating a request token
     */
    fun startAuthentication() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = authRepository.createRequestToken()
            result.fold(
                onSuccess = { tokenResponse ->
                    Log.d("AuthViewModel", "Request token created: ${tokenResponse.requestToken}")
                    val authUrl = authRepository.getAuthUrl(tokenResponse.requestToken)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        authUrl = authUrl,
                        requestToken = tokenResponse.requestToken
                    )
                },
                onFailure = { exception ->
                    Log.e("AuthViewModel", "Failed to create token", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    /**
     * Step 2: Complete authentication after user approves in WebView
     */
    suspend fun completeAuthentication() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        val requestToken = _uiState.value.requestToken ?: run {
            Log.e("AuthViewModel", "No request token found")
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "No request token found"
            )
            return
        }

        val result = authRepository.createSession(requestToken)
        result.fold(
            onSuccess = { sessionId ->
                Log.d("AuthViewModel", "Session created successfully: $sessionId")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    error = null
                )

                // 🟢 بعد نجاح التسجيل، نستدعي حساب المستخدم بالكامل من TMDB
                viewModelScope.launch {
                    fetchAccountDetails()
                }
            },
            onFailure = { exception ->
                Log.e("AuthViewModel", "Failed to create session", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to create session: ${exception.message}\n\nPlease make sure you approved the authentication in your browser."
                )
            }
        )
    }

    /**
     * Fetch account details after login
     */
    fun fetchAccountDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = authRepository.getAccountDetails()
            result.fold(
                onSuccess = { accountDetails ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        accountDetails = accountDetails
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to fetch account details"
                    )
                }
            )
        }
    }

    /**
     * Logout
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState()
        }
    }
}