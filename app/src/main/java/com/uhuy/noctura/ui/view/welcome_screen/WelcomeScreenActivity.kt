package com.uhuy.noctura.ui.view.welcome_screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.uhuy.noctura.R
import com.uhuy.noctura.databinding.ActivityWelcomeScreenBinding
import com.uhuy.noctura.ui.adapter.WelcomeScreenAdapter
import com.uhuy.noctura.ui.view.auth.AuthActivity

class WelcomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupViewPager()
        setupButtons()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun setupViewPager() {
        val fragmentList = listOf(
            Welcome1Fragment(),
            Welcome2Fragment(),
            Welcome3Fragment()
        )
        val adapter = WelcomeScreenAdapter(this, fragmentList)

        binding.viewPager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtons(position, fragmentList.size)
            }
        })
    }

    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            val viewPager = binding.viewPager
            val nextPage = viewPager.currentItem + 1
            if (nextPage < (viewPager.adapter?.itemCount ?: 0)) {
                viewPager.currentItem = nextPage
            } else {
                finishWelcomeScreen()
            }
        }

        binding.btnSkip.setOnClickListener {
            finishWelcomeScreen()
        }
    }

    private fun updateButtons(position: Int, totalPages: Int) {
        if (position == totalPages - 1) {
            binding.btnNext.text = "Finish"
            binding.btnSkip.visibility = View.GONE
        } else {
            binding.btnNext.text = "Next"
            binding.btnSkip.visibility = View.VISIBLE
        }
    }

    private fun finishWelcomeScreen() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}