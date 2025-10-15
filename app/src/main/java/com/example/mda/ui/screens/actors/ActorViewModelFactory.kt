package com.example.mda.ui.screens.actors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mda.data.local.dao.ActorDao
import com.example.mda.data.remote.RetrofitInstance
import com.example.mda.data.repository.ActorsRepository

/**
 * Khalid: Factory that accepts an optional ActorDao. If you already construct a repository
 * elsewhere, you can use the other constructor.
 */
class ActorViewModelFactory(
    private val repository: ActorsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActorViewModel::class.java)) {
            return ActorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        // convenience: create repository with only network (no dao)
        fun createNetworkOnly(): ActorViewModelFactory {
            val repo = ActorsRepository(RetrofitInstance.api, null)
            return ActorViewModelFactory(repo)
        }
    }
}
