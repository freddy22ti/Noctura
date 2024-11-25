package com.uhuy.noctura.ui.view.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.uhuy.noctura.R
import com.uhuy.noctura.databinding.FragmentLoginBinding
import com.uhuy.noctura.ui.view.home.MainActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Callback untuk navigasi ke RegisterFragment
    var onSwitchToRegister: (() -> Unit)? = null

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
            val email: String = binding.etEmailLogin.text.toString()
            val password: String = binding.etPasswordLogin.text.toString()

            if (isInputValid(email, password)) {
                // check credential logic

                // pindah ke halaman main
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                requireActivity().finish()
            } else {
                // Menampilkan pesan error dengan toast
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_empty_input),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupBtnSwitchToRegister() {
        binding.btnSwitchToRegister.setOnClickListener {
            onSwitchToRegister?.invoke()
        }
    }

    private fun isInputValid(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}