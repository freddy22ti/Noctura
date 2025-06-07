package com.uhuy.noctura.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.uhuy.noctura.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseDatabaseService {
    private val database = FirebaseDatabase.getInstance("https://noctura-51ea2-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private val auth = FirebaseAuth.getInstance()

    // Save user profile to Realtime Database
    suspend fun saveUserProfile(user: User): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            withContext(Dispatchers.IO) {
                database.child("users")
                    .child(uid)
                    .setValue(user)
                    .await() // Wait for the operation to complete
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



    // Fetch user profile from Realtime Database
    suspend fun fetchUserProfile(uid: String): User? {
        return try {
            withContext(Dispatchers.IO) {
                val snapshot = database.child("users")
                    .child(uid)
                    .get()
                    .await()
                snapshot.getValue(User::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    // Get the current authenticated user ID
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

}