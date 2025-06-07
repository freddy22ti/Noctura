package com.uhuy.noctura.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uhuy.noctura.data.repository.AuthRepository
import com.uhuy.noctura.data.repository.UserRepository

class InferenceViewModelFactory(
    private val context: Context,
    private val userRepository: UserRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InferenceViewModel::class.java)) {
            // Return an instance of AuthViewModel with the required dependencies
            return InferenceViewModel(context, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}