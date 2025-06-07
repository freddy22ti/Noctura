package com.uhuy.noctura.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhuy.noctura.data.model.AuthResult
import com.uhuy.noctura.data.repository.AuthRepository
import com.uhuy.noctura.utils.NetworkUtils
import com.uhuy.noctura.utils.Resource
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    // LiveData untuk login dan register
    private val _loginResult = MutableLiveData<Resource<Boolean>>()
    val loginResult: LiveData<Resource<Boolean>> get() = _loginResult

    private val _registerResult = MutableLiveData<Resource<Boolean>>()
    val registerResult: LiveData<Resource<Boolean>> get() = _registerResult

    // Login user
    fun login(context: Context, email: String, password: String) {
        checkNetworkAndRun(context) {
            _loginResult.value = Resource.Loading() // Set status loading
            val loginResult = authRepository.login(email, password)
            handleResult(loginResult, _loginResult)
        }
    }

    // Register user
    fun register(context: Context, username: String, email: String, password: String) {
        checkNetworkAndRun(context) {
            _registerResult.value = Resource.Loading() // Set status loading
            val registerResult = authRepository.register(username, email, password)
            handleResult(registerResult, _registerResult)
        }
    }

    // Logout user
    fun logout() {
        authRepository.logout()
    }

    // Utility function for network check
    private fun checkNetworkAndRun(context: Context, action: suspend () -> Unit) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                action()
            }
        } else {
            _loginResult.postValue(Resource.Error("No internet connection"))
            _registerResult.postValue(Resource.Error("No internet connection"))
        }
    }

    // Handle success or error response
    private fun handleResult(result: AuthResult, liveData: MutableLiveData<Resource<Boolean>>) {
        if (result.success) {
            liveData.postValue(Resource.Success(true))
        } else {
            Log.e("AuthViewModel", result.errorMessage)
            liveData.postValue(Resource.Error(result.errorMessage))
        }
    }

}