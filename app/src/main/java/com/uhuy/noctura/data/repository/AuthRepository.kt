package com.uhuy.noctura.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.uhuy.noctura.data.firebase.FirebaseAuthService
import com.uhuy.noctura.data.firebase.FirebaseDatabaseService
import com.uhuy.noctura.data.local.SharedPreferencesHelper
import com.uhuy.noctura.data.model.AuthResult
import com.uhuy.noctura.data.model.User

class AuthRepository(
    private val authService: FirebaseAuthService,
    private val databaseService: FirebaseDatabaseService,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) {

    suspend fun login(email: String, password: String): AuthResult {
        val emailValidation = validateEmail(email)
        if (!emailValidation.success) {
            return emailValidation
        }
        val passwordValidation = validatePassword(password)
        if (!passwordValidation.success) {
            return passwordValidation
        }

        return try {
            val authResult = authService.login(email, password)
            if (authResult != null) {
                // Mapping Firebase user data to UserResponse
                sharedPreferencesHelper.setLoggedIn(true)
                val user = databaseService.fetchUserProfile(authResult.uid)
                if (user != null) {
                    sharedPreferencesHelper.saveUserProfile(user) // Save locally
                }
                AuthResult(true)
            } else {
                AuthResult(false, "Login failed")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed: ${e.message}")
            AuthResult(false, e.message ?: "An unexpected error occurred.")
        }
    }

    suspend fun register(username: String, email: String, password: String): AuthResult {
//        Validasi email dan password
        val emailValidation = validateEmail(email)
        if (!emailValidation.success) {
            return emailValidation
        }
        val passwordValidation = validatePassword(password)
        if (!passwordValidation.success) {
            return passwordValidation
        }

        return try {
            val authResult = authService.register(email, password)
            if (authResult != null) {
                val user = User(
                    uid = authResult.uid,
                    email = email,
                    username = username,
                )
                val isSaved = databaseService.saveUserProfile(user)
                if (isSaved) {
                    AuthResult(true)
                } else {
                    AuthResult(false, "Failed to save user profile.")
                }
            } else {
                AuthResult(false, "Registration failed.")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration failed: ${e.message}")
            AuthResult(false, e.message ?: "An unexpected error occurred.")
        }
    }

    // Mendapatkan user saat ini
    fun getCurrentUser(): FirebaseUser? {
        return authService.getCurrentUser()
    }

    //    Logout user saat  ini
    fun logout() {
        sharedPreferencesHelper.setLoggedIn(false)
        sharedPreferencesHelper.clearUserProfile()
        return authService.logout()
    }

    // Private utility methods for validation
    private fun validateEmail(email: String): AuthResult {
        return if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            AuthResult(false, "Invalid email format.")
        } else {
            AuthResult(true)
        }
    }

    private fun validatePassword(password: String): AuthResult {
        return if (password.length < 6) {
            AuthResult(false, "Password must be at least 6 characters long.")
        } else {
            AuthResult(true)
        }
    }
}