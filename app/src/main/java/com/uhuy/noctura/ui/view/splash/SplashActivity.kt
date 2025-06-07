package com.uhuy.noctura.ui.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.uhuy.noctura.R
import com.uhuy.noctura.ui.view.welcome_screen.WelcomeScreenActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Navigate to the welcome screen
        installSplashScreen()
        startActivity(Intent(this, WelcomeScreenActivity::class.java))
        finish()
    }
}