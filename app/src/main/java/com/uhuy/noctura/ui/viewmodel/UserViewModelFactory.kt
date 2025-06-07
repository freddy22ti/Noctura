package com.uhuy.noctura.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uhuy.noctura.data.repository.AuthRepository
import com.uhuy.noctura.data.repository.UserRepository

class UserViewModelFactory (
    private val userRepository: UserRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            // Return an instance of AuthViewModel with the required dependencies
            return UserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}