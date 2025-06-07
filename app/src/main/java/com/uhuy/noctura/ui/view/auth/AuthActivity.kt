package com.uhuy.noctura.ui.view.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.uhuy.noctura.R
import com.uhuy.noctura.data.local.SharedPreferencesHelper
import com.uhuy.noctura.data.local.SharedPreferencesManager
import com.uhuy.noctura.databinding.ActivityAuthBinding
import com.uhuy.noctura.ui.view.home.MainActivity
import com.uhuy.noctura.utils.loadFragment

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private lateinit var loginFragment: LoginFragment
    private lateinit var registerFragment: RegisterFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the binding object
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Kalau sudah login langsung ke home
        val sharedPreferencesHelper = SharedPreferencesManager.getHelper()
        if(sharedPreferencesHelper.isLoggedIn()) {
            navigateToHome()
        }

        setupWindowInsets()
        setupInitialFragment()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun setupInitialFragment() {
        // Initialize fragments
        loginFragment = LoginFragment().apply {
            onSwitchToRegister = { loadRegisterFragment() }
        }
        registerFragment = RegisterFragment().apply {
            onSwitchToLogin = { loadLoginFragment() }
        }

        // Set default fragment (LoginFragment)
        loadFragment(
            fragment = loginFragment,
            containerId = binding.fragmentContainer.id,
            addToBackStack = false // No need to add default fragment to back stack
        )
    }

    private fun loadRegisterFragment() {
        loadFragment(
            fragment = registerFragment,
            containerId = binding.fragmentContainer.id,
            addToBackStack = true
        )
    }

    private fun loadLoginFragment() {
        loadFragment(
            fragment = loginFragment,
            containerId = binding.fragmentContainer.id,
            addToBackStack = true
        )
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }
}