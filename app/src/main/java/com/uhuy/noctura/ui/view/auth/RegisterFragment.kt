package com.uhuy.noctura.ui.view.auth

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.uhuy.noctura.R
import com.uhuy.noctura.data.firebase.FirebaseAuthService
import com.uhuy.noctura.data.firebase.FirebaseDatabaseService
import com.uhuy.noctura.data.local.SharedPreferencesHelper
import com.uhuy.noctura.data.local.SharedPreferencesManager
import com.uhuy.noctura.data.model.User
import com.uhuy.noctura.data.repository.AuthRepository
import com.uhuy.noctura.databinding.FragmentRegisterBinding
import com.uhuy.noctura.ui.view.home.MainActivity
import com.uhuy.noctura.ui.viewmodel.AuthViewModel
import com.uhuy.noctura.ui.viewmodel.AuthViewModelFactory
import com.uhuy.noctura.utils.KeyboardUtils.hideKeyboard
import com.uhuy.noctura.utils.Resource

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Callback untuk navigasi ke LoginFragment
    var onSwitchToLogin: (() -> Unit)? = null

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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)


        setupBtnRegister()
        setupBtnSwitchToLogin()

        return binding.root
    }

    private fun setupBtnRegister() {
        binding.btnRegister.setOnClickListener {
            val username: String = binding.etUsernameRegister.text.toString()
            val email: String = binding.etEmailRegister.text.toString()
            val password: String = binding.etPasswordRegister.text.toString()

            requireActivity().hideKeyboard()

            if (validateInput(username, email, password)) {
                // logika buat akun
                authViewModel.register(requireContext(), username, email, password)
            }
        }

        authViewModel.registerResult.observe(viewLifecycleOwner) { resource ->
            handleRegisterState(resource)
        }
    }

    private fun handleRegisterState(resource: Resource<Boolean>) {
        when (resource) {
            is Resource.Loading -> showLoadingState()
            is Resource.Success -> navigateToLogin()
            is Resource.Error -> showErrorState(resource.message)
            else -> Unit
        }
    }

    private fun showErrorState(errorMessage: String?) {
        showMessage(errorMessage ?: "An unexpected error occurred.", false)
        binding.btnRegister.isEnabled = true // Enable the button again
        binding.btnRegister.text = getString(R.string.register_text) // Restore the original button text
        binding.pbRegister.visibility = View.GONE // Hide the progress bar
    }

    private fun navigateToLogin() {
        // Register berhasil pindah ke halaman login
        onSwitchToLogin?.invoke()
    }

    private fun showLoadingState() {
        // Tampilkan progress bar
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = ""
        binding.pbRegister.visibility = View.VISIBLE
    }

    private fun setupBtnSwitchToLogin() {
        binding.btnSwitchToLogin.setOnClickListener {
            // Notify activity to switch to LoginFragment
            onSwitchToLogin?.invoke()
        }
    }

    private fun validateInput(username: String, email: String, password: String): Boolean {
        return when {
            username.isEmpty() -> {
                showMessage(getString(R.string.error_empty_username))
                false
            }
            email.isEmpty() -> {
                showMessage(getString(R.string.error_empty_email))
                false
            }
            password.isEmpty() -> {
                showMessage(getString(R.string.error_empty_password))
                false
            }
            password.length < 6 -> {
                showMessage("Password harus memiliki setidaknya 6 digit")
                false
            }
            else -> true
        }
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