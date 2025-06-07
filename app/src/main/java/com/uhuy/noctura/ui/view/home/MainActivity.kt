package com.uhuy.noctura.ui.view.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.uhuy.noctura.R
import com.uhuy.noctura.databinding.ActivityMainBinding
import com.uhuy.noctura.utils.loadFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupInitialFragment()
        setupBottomNavigationView()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun setupBottomNavigationView() {
        val fragmentMap = mapOf(
            R.id.home to HomeFragment(),
            R.id.alarm to AlarmFragment(),
            R.id.statistics to StatisticFragment(),
            R.id.settings to SettingsFragment()
        )

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            fragmentMap[menuItem.itemId]?.let { fragment ->
                loadFragment(
                    fragment = fragment,
                    containerId = binding.fragmentContainer.id,
                    addToBackStack = false
                )
                true
            } ?: false
        }
    }

    private fun setupInitialFragment() {
        loadFragment(
            fragment = HomeFragment(),
            containerId = binding.fragmentContainer.id,
            addToBackStack = false
        )
    }
}