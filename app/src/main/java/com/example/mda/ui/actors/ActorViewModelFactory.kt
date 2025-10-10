package com.example.mda.ui.actors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mda.data.remote.repository.ActorRepository
import com.example.mda.data.remote.RetrofitInstance
class ActorViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActorViewModel::class.java)) {
            val repository = ActorRepository(RetrofitInstance.api)
            return ActorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
