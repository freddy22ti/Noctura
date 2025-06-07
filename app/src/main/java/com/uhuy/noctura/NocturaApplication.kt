package com.uhuy.noctura

import android.app.Application
import com.uhuy.noctura.data.local.SharedPreferencesManager

class NocturaApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize SharedPreferencesManager with the application context
        SharedPreferencesManager.init(this)
    }
}