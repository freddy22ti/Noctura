package com.uhuy.noctura.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhuy.noctura.data.model.User
import com.uhuy.noctura.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    private val _isUserSaved = MutableLiveData<Boolean>()
    val isUserSaved: LiveData<Boolean> get() = _isUserSaved

    fun saveUserProfile(
        username: String? = null,
        umur: Int? = null,
        jenisKelamin: String? = null,
        smokingStatus: Boolean? = null,
        frekuensiOlahraga: Int? = null,
    ) {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.fetchUserProfile()

                // Ensure we have a valid user
                if (currentUser == null) {
                    _isUserSaved.value = false
                    return@launch
                }

                val user = User(
                    uid = currentUser.uid,
                    username = username ?: currentUser.username ?: "",
                    email = currentUser.email ?: "",
                    umur = umur ?: currentUser.umur ?: -1, // Use -1 if 'umur' is invalid
                    jenisKelamin = jenisKelamin ?: currentUser.jenisKelamin ?: "",
                    smokingStatus = smokingStatus ?: false,
                    frekuensiOlahraga = frekuensiOlahraga ?: 0,
                )

                val result = userRepository.saveUserProfile(user)
                _isUserSaved.value = result
            } catch (e: Exception) {
                // Log or handle the exception
                _isUserSaved.value = false
            }
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            val user = userRepository.fetchUserProfile()
            _userProfile.value = user
        }
    }
}