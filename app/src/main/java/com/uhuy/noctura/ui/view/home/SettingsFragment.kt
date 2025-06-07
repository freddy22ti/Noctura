package com.uhuy.noctura.ui.view.home

import com.uhuy.noctura.ui.adapter.SettingsListAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.uhuy.noctura.R
import com.uhuy.noctura.data.firebase.FirebaseAuthService
import com.uhuy.noctura.data.firebase.FirebaseDatabaseService
import com.uhuy.noctura.data.local.SharedPreferencesHelper
import com.uhuy.noctura.data.model.SettingItem
import com.uhuy.noctura.data.repository.AuthRepository
import com.uhuy.noctura.databinding.FragmentSettingsBinding
import com.uhuy.noctura.ui.view.auth.AuthActivity
import com.uhuy.noctura.ui.viewmodel.AuthViewModel
import com.uhuy.noctura.ui.viewmodel.AuthViewModelFactory


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("USER_PROFILE", Context.MODE_PRIVATE)
        val sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences)
        val authRepository = AuthRepository(FirebaseAuthService(), FirebaseDatabaseService(), sharedPreferencesHelper)

        authViewModel = ViewModelProvider(this,
            AuthViewModelFactory(authRepository)
        )[AuthViewModel::class.java]

        setupListSettings()

        return binding.root
    }

    private fun setupListSettings() {
        val settingList = listOf(
            SettingItem(
                0, "Account", "Kelola profile anda",
            ),
            SettingItem(
                0, "Logout", "Keluar dari akun",
            ),
        )

        binding.lvSettings.adapter = SettingsListAdapter(requireContext(), settingList)
        binding.lvSettings.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = settingList[position]

            when (selectedItem.title) {
                "Account" -> openAccountSettings()
                "Logout" -> logout()
            }
        }
    }

    private fun openAccountSettings() {
        // Create an instance of the AccountSettingsFragment
        val accountSettingsFragment = AccountFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, accountSettingsFragment)  // R.id.fragment_container should be the ID of your container
            .addToBackStack(null)  // This allows the user to navigate back to the previous fragment
            .commit()
    }

    private fun logout() {
        // Handle logout functionality
        authViewModel.logout()
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

}