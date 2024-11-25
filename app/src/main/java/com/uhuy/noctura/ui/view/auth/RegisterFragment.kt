package com.uhuy.noctura.ui.view.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.uhuy.noctura.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Callback untuk navigasi ke LoginFragment
    var onSwitchToLogin: (() -> Unit)? = null

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
            val password: String = binding.etPasswordRegister.toString()

            if (isInputValid(username, email, password)) {
                // logika buat akun


                // Pindah ke halaman login
                onSwitchToLogin?.invoke()
            } else {
                // Menampilkan pesan error dengan toast
                Toast.makeText(
                    requireContext(),
                    "Tidak boleh ada data kosong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupBtnSwitchToLogin() {
        binding.btnSwitchToLogin.setOnClickListener {
            // Notify activity to switch to LoginFragment
            onSwitchToLogin?.invoke()
        }
    }

    private fun isInputValid(username: String, email: String, password: String): Boolean {
        return username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}