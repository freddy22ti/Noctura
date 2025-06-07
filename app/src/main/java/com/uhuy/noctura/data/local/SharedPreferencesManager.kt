package com.uhuy.noctura.data.local

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val PREF_NAME = "USER_PROFILE"
    private lateinit var sharedPreferences: SharedPreferences

    // Initialize the SharedPreferences instance
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getSharedPreferences(): SharedPreferences {
        if (!::sharedPreferences.isInitialized) {
            throw IllegalStateException("SharedPreferencesManager is not initialized. Call init() first.")
        }
        return sharedPreferences
    }

    fun getHelper(): SharedPreferencesHelper {
        return SharedPreferencesHelper(getSharedPreferences())
    }


}