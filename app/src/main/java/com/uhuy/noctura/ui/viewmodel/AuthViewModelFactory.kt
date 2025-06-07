package com.uhuy.noctura.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uhuy.noctura.data.repository.AuthRepository

class AuthViewModelFactory(
    private val authRepository: AuthRepository
): ViewModelProvider.Factory {
    // This method creates and returns an instance of AuthViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Return an instance of AuthViewModel with the required dependencies
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}