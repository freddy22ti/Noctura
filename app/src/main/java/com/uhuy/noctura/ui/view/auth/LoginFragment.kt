package com.uhuy.noctura.ui.view.auth

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.uhuy.noctura.R
import com.uhuy.noctura.data.firebase.FirebaseAuthService
import com.uhuy.noctura.data.firebase.FirebaseDatabaseService
import com.uhuy.noctura.data.local.SharedPreferencesHelper
import com.uhuy.noctura.data.local.SharedPreferencesManager
import com.uhuy.noctura.data.repository.AuthRepository
import com.uhuy.noctura.databinding.FragmentLoginBinding
import com.uhuy.noctura.ui.view.home.MainActivity
import com.uhuy.noctura.ui.viewmodel.AuthViewModel
import com.uhuy.noctura.ui.viewmodel.AuthViewModelFactory
import com.uhuy.noctura.utils.KeyboardUtils.hideKeyboard
import com.uhuy.noctura.utils.Resource

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Callback untuk navigasi ke RegisterFragment
    var onSwitchToRegister: (() -> Unit)? = null

    private val sharedPreferencesHelper by lazy {
        SharedPreferencesManager.getHelper()
    }


    private val authRepository by lazy {
        AuthRepository(FirebaseAuthService(), FirebaseDatabaseService(), sharedPreferencesHelper)
    }

    private val authViewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(authRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupBtnLogin()
        setupBtnSwitchToRegister()

        return binding.root
    }

    private fun setupBtnLogin() {
        binding.btnLogin.setOnClickListener {
            // Hide keyboard using the utility function
            requireActivity().hideKeyboard()

//            Read email and password
            val email: String = binding.etEmailLogin.text.toString()
            val password: String = binding.etPasswordLogin.text.toString()

//            Validate email and password
            if (isInputValid(email, password)) {
                authViewModel.login(requireContext(), email, password)
            }
        }

        // Observasi hasil login
        authViewModel.loginResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoadingState()
                is Resource.Success -> navigateToHome()
                is Resource.Error -> showErrorState(resource.message)
                else -> Unit
            }
        }
    }

    private fun setupBtnSwitchToRegister() {
        binding.btnSwitchToRegister.setOnClickListener {
            onSwitchToRegister?.invoke()
        }
    }

    private fun isInputValid(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showMessage(getString(R.string.error_auth_empty_input))
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage(getString(R.string.error_invalid_email))
            return false
        }

        if(password.length < 6) {
            showMessage(getString(R.string.error_password_length))
            return false
        }
        return true
    }

    private fun showLoadingState() {
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = ""
        binding.pbLogin.visibility = View.VISIBLE
    }

    private fun navigateToHome() {
        sharedPreferencesHelper.setLoggedIn(true)

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showErrorState(errorMessage: String?) {
        showMessage(errorMessage ?: "An unexpected error occurred." , false)
        binding.btnLogin.isEnabled = true
        binding.btnLogin.text = getString(R.string.login_text)
        binding.pbLogin.visibility = View.GONE
    }


    private fun showMessage(message: String, useSnackbar: Boolean = true) {
        if (useSnackbar) {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}