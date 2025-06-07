package com.uhuy.noctura.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.uhuy.noctura.data.model.User

class SharedPreferencesHelper(private val sharedPreferences: SharedPreferences) {
    companion object {
        private const val KEY_USER_PROFILE = "key_user_profile"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_ALARM_TIME = "key_alarm_time"
        private const val KEY_IS_ALARM_STARTED = "key_alarm_started"
    }

    private val gson = Gson()

    // Save user profile
    fun saveUserProfile(user: User) {
        val userJson = gson.toJson(user) // Convert the User object to JSON
        sharedPreferences.edit().putString(KEY_USER_PROFILE, userJson).apply()
    }

    // Retrieve user profile
    fun getUserProfile(): User? {
        val userJson = sharedPreferences.getString(KEY_USER_PROFILE, null)
        return if (userJson != null) gson.fromJson(userJson, User::class.java) else null
    }

    fun clearUserProfile() {
        sharedPreferences.edit().remove(KEY_USER_PROFILE).apply()
    }

    // Save a boolean value
    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    // Get a boolean value
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

//    get waktu alarm
    fun getAlarmTime(): String? {
        return sharedPreferences.getString(KEY_ALARM_TIME, "12:00")
    }

    fun setAlarmTime(alarmTime: String) {
        sharedPreferences.edit().putString(KEY_ALARM_TIME, alarmTime).apply()
    }

    fun getAlarmStatus(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_ALARM_STARTED, false)
    }

    fun setAlarmStatus(started: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_ALARM_STARTED, started).apply()
    }
}