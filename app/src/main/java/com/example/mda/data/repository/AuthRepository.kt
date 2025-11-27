package com.example.mda.data.repository

import com.example.mda.data.datastore.SessionManager
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.auth.AccountDetails
import com.example.mda.data.remote.model.auth.SessionRequest
import com.example.mda.data.remote.model.auth.TokenResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthRepository(
    private val api: TmdbApi,
    private val sessionManager: SessionManager
) {

    /**
     * Step 1: Create a new request token
     */
    suspend fun createRequestToken(): Result<TokenResponse> {
        return try {
            val response = api.createRequestToken()
            if (response.isSuccessful && response.body() != null) {
                val tokenResponse = response.body()!!
                // Save the request token for later use
                sessionManager.saveRequestToken(tokenResponse.requestToken)
                Result.success(tokenResponse)
            } else {
                Result.failure(Exception("Failed to create request token: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Step 2: Get the approval URL for the user to authenticate
     * The user will open this in a WebView and approve the app
     */
    fun getAuthUrl(requestToken: String): String {
        return "https://www.themoviedb.org/authenticate/$requestToken"
    }

    /**
     * Step 3: After user approves, exchange the request token for a session ID
     */
    suspend fun createSession(requestToken: String): Result<String> {
        return try {
            val response = api.createSession(SessionRequest(requestToken))
            if (response.isSuccessful && response.body() != null) {
                val sessionId = response.body()!!.sessionId
                // Save the session ID
                sessionManager.saveSessionId(sessionId)
                Result.success(sessionId)
            } else {
                Result.failure(Exception("Failed to create session: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get account details using the saved session ID
     * Also saves account_id locally in SessionManager.
     */
    suspend fun getAccountDetails(): Result<AccountDetails> {
        return try {
            val sessionId = sessionManager.sessionId.first()

            if (sessionId.isNullOrEmpty()) {
                return Result.failure(Exception("No session ID found. Please login first."))
            }

            val response = api.getAccountDetails(sessionId)

            if (response.isSuccessful && response.body() != null) {
                val details = response.body()!!

                // Save account id
                sessionManager.saveAccountId(details.id)
                sessionManager.saveAccountInfo(details.name, details.username)

                return Result.success(details)

            } else {
                Result.failure(Exception("Failed to get account details: ${response.message()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Boolean> {
        return sessionManager.sessionId.map { it != null }
    }

    /**
     * Get the saved session ID
     */
    fun getSessionId(): Flow<String?> {
        return sessionManager.sessionId
    }

    /**
     * Get the saved request token
     */
    fun getRequestToken(): Flow<String?> {
        return sessionManager.requestToken
    }

    /**
     * Get the saved account ID
     */
    fun getAccountId(): Flow<Int?> {
        return sessionManager.accountId
    }

    /**
     * Logout - clear all saved session data
     */
    suspend fun logout() {
        sessionManager.clearSession()
    }
}
