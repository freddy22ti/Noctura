package com.uhuy.noctura.data.repository

import com.uhuy.noctura.data.firebase.FirebaseDatabaseService
import com.uhuy.noctura.data.local.SharedPreferencesHelper
import com.uhuy.noctura.data.model.User

class UserRepository(
    private val firebaseDatabase: FirebaseDatabaseService,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) {

    suspend fun saveUserProfile(user: User): Boolean {
        val isSavedToFirebase = firebaseDatabase.saveUserProfile(user)
        if (isSavedToFirebase) {
            sharedPreferencesHelper.saveUserProfile(user)
        }
        return isSavedToFirebase
    }

    suspend fun fetchUserProfile(): User? {
        // Try to get the user profile from SharedPreferences
        val cachedUser = sharedPreferencesHelper.getUserProfile()
        if (cachedUser != null) {
            return cachedUser
        }

        // If not available, fetch from Firebase
        val uid = firebaseDatabase.getCurrentUserId() ?: return null
        val userFromFirebase = firebaseDatabase.fetchUserProfile(uid)

        // Save the fetched user profile to SharedPreferences for future use
        if (userFromFirebase != null) {
            sharedPreferencesHelper.saveUserProfile(userFromFirebase)
        }

        return userFromFirebase
    }
}