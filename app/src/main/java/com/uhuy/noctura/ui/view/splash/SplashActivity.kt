package com.uhuy.noctura.ui.view.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.uhuy.noctura.R
import com.uhuy.noctura.ui.view.welcome_screen.WelcomeScreenActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set animation
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val splashScreenView = findViewById<View>(android.R.id.content)
        splashScreenView.startAnimation(animation)

        // Navigate to the welcome screen
        startActivity(Intent(this, WelcomeScreenActivity::class.java))
        finish()
    }
}