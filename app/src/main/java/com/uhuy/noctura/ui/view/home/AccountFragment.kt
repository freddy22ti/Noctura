package com.uhuy.noctura.ui.view.home

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uhuy.noctura.data.firebase.FirebaseDatabaseService
import com.uhuy.noctura.data.local.SharedPreferencesHelper
import com.uhuy.noctura.data.repository.UserRepository
import com.uhuy.noctura.databinding.DialogGenderSelectionBinding
import com.uhuy.noctura.databinding.DialogInputTextBinding
import com.uhuy.noctura.databinding.DialogSmokingStatusSelectionBinding
import com.uhuy.noctura.databinding.FragmentAccountBinding
import com.uhuy.noctura.ui.viewmodel.UserViewModel
import com.uhuy.noctura.ui.viewmodel.UserViewModelFactory

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private var isDialogShowing: Boolean = false


    private val userRepository by lazy {
        val sharedPreferences =
            requireContext().getSharedPreferences("USER_PROFILE", Context.MODE_PRIVATE)
        val sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences)
        UserRepository(FirebaseDatabaseService(), sharedPreferencesHelper)
    }

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModelFactory(userRepository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        setupProfileValues()
        setupDialogJenisKelamin()
        setupDialogUmur()
        setupDialogUsername()
        setupStatusPerokok()
        setupFrekuensiOlahraga()

        return binding.root
    }

    private fun setupProfileValues() {
        // Observe the user profile LiveData
        userViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Update UI with user profile data
                binding.tvValueUsername.text = user.username
                binding.tvValueEmail.text = user.email
                binding.tvValueJenisKelamin.text = user.jenisKelamin
                binding.tvValueUmur.text = user.umur.toString()
                binding.tvValueSmokingStatus.text = if (user.smokingStatus) "Ya" else "Tidak"
                binding.tvValueFrekuensiOlahraga.text =
                    user.frekuensiOlahraga.toString() + "x dalam seminggu"
            }
        }

        // Trigger the ViewModel to fetch user profile
        userViewModel.fetchUserProfile()
    }

    private fun setupDialogJenisKelamin() {
        binding.layoutJenisKelamin.setOnClickListener {
            if (isDialogShowing) return@setOnClickListener // Cegah dialog baru dibuka jika sudah ada dialog aktif

            // Inflate the custom layout using ViewBinding
            val dialogBinding = DialogGenderSelectionBinding.inflate(layoutInflater)

            isDialogShowing = true // Tandai dialog sedang ditampilkan

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pilih Jenis Kelamin")
                .setView(dialogBinding.root) // Set the root of the binding as the view
                .setPositiveButton("OK") { dialog, _ ->
                    // Get the selected RadioButton's text
                    val selectedRadioButtonId = dialogBinding.rgGender.checkedRadioButtonId
                    val selectedRadioButton =
                        dialogBinding.root.findViewById<RadioButton>(selectedRadioButtonId)
                    val selectedOption = selectedRadioButton?.text.toString()

                    if (selectedOption.isNotEmpty()) {
                        // Update the UI
                        binding.tvValueJenisKelamin.text = selectedOption

                        // Save the selected gender using ViewModel
                        userViewModel.saveUserProfile(
                            jenisKelamin = selectedOption // Pass the selected gender
                        )
                    } else {
                        Toast.makeText(requireContext(), "Harap pilih opsi", Toast.LENGTH_SHORT)
                            .show()
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener { isDialogShowing = false } // Reset ketika dialog ditutup
                .show()
        }
    }

    private fun setupDialogUmur() {
        binding.layoutUmur.setOnClickListener {
            if (isDialogShowing) return@setOnClickListener // Cegah dialog baru dibuka jika sudah ada dialog aktif

            // Inflate layout using binding
            val dialogBinding =
                DialogInputTextBinding.inflate(LayoutInflater.from(requireContext()))

            dialogBinding.etInput.hint = "Umur Anda"

            // Set input type to number
            dialogBinding.etInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            // Set imeOptions to Done action
            dialogBinding.etInput.imeOptions = EditorInfo.IME_ACTION_DONE

            // Set max length to 3
            val maxLengthFilter = InputFilter.LengthFilter(3)
            dialogBinding.etInput.filters = arrayOf(maxLengthFilter)

            isDialogShowing = true // Tandai dialog sedang ditampilkan

            // Build the dialog
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Masukkan Umur")
                .setView(dialogBinding.root)
                .setPositiveButton("Simpan") { dialog, _ ->
                    val umurInput = dialogBinding.etInput.text.toString()
                    if (umurInput.isNotEmpty() && umurInput.toIntOrNull() != null) {
                        binding.tvValueUmur.text = umurInput // Update UI

                        // save to firebase
                        userViewModel.saveUserProfile(
                            umur = umurInput.toInt()
                        )

                    } else {
                        dialogBinding.etInput.error = "Masukkan angka yang valid"
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener { isDialogShowing = false } // Reset ketika dialog ditutup
                .show()
        }
    }


    private fun setupDialogUsername() {
        binding.layoutUsername.setOnClickListener {
            if (isDialogShowing) return@setOnClickListener // Cegah dialog baru dibuka jika sudah ada dialog aktif

            // Inflate layout using binding
            val dialogBinding =
                DialogInputTextBinding.inflate(LayoutInflater.from(requireContext()))

            dialogBinding.etInput.hint = "Masukkan Username"

            dialogBinding.etInput.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME

            // Set max length to 30
            val maxLengthFilter = InputFilter.LengthFilter(30)
            dialogBinding.etInput.filters = arrayOf(maxLengthFilter)

            // Set imeOptions to Done action
            dialogBinding.etInput.imeOptions = EditorInfo.IME_ACTION_DONE

            isDialogShowing = true // Tandai dialog sedang ditampilkan


            // Build the dialog
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Username")
                .setView(dialogBinding.root)
                .setPositiveButton("Simpan") { dialog, _ ->
                    val usernameInput = dialogBinding.etInput.text.toString()
                    if (usernameInput.isNotEmpty()) {
                        binding.tvValueUsername.text = usernameInput // Update UI

                        userViewModel.saveUserProfile(
                            username = usernameInput
                        )
                    } else {
                        dialogBinding.etInput.error = "Username tidak boleh kosong"
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener { isDialogShowing = false } // Reset ketika dialog ditutup
                .show()
        }
    }

    private fun setupStatusPerokok() {
        binding.layoutSmokingStatus.setOnClickListener {
            if (isDialogShowing) return@setOnClickListener // Cegah dialog baru dibuka jika sudah ada dialog aktif

            // Inflate the custom layout using ViewBinding
            val dialogBinding = DialogSmokingStatusSelectionBinding.inflate(layoutInflater)

            isDialogShowing = true // Tandai dialog sedang ditampilkan

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pilih Status Perokok")
                .setView(dialogBinding.root) // Set the root of the binding as the view
                .setPositiveButton("OK") { dialog, _ ->
                    // Get the selected RadioButton's text
                    val selectedRadioButtonId = dialogBinding.rgSmokingStatus.checkedRadioButtonId
                    val selectedRadioButton =
                        dialogBinding.root.findViewById<RadioButton>(selectedRadioButtonId)
                    val selectedOption = selectedRadioButton?.text.toString()

                    if (selectedOption.isNotEmpty()) {
                        // Update the UI
                        binding.tvValueSmokingStatus.text = selectedOption

                        // Save the selected gender using ViewModel
                        userViewModel.saveUserProfile(
                            smokingStatus = selectedOption == "Ya" // Pass the selected gender
                        )
                    } else {
                        Toast.makeText(requireContext(), "Harap pilih opsi", Toast.LENGTH_SHORT)
                            .show()
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener { isDialogShowing = false } // Reset ketika dialog ditutup
                .show()


        }
    }

    private fun setupFrekuensiOlahraga() {
        binding.layoutFrekuensiOlahraga.setOnClickListener {
            if (isDialogShowing) return@setOnClickListener // Cegah dialog baru dibuka jika sudah ada dialog aktif

            // Inflate layout using binding
            val dialogBinding =
                DialogInputTextBinding.inflate(LayoutInflater.from(requireContext()))

            dialogBinding.etInput.hint = "Frekuensi Olahraga Dalam Seminggu"

            // Set input type to number
            dialogBinding.etInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            // Set imeOptions to Done action
            dialogBinding.etInput.imeOptions = EditorInfo.IME_ACTION_DONE

            // Set max length to 2
            val maxLengthFilter = InputFilter.LengthFilter(2)
            dialogBinding.etInput.filters = arrayOf(maxLengthFilter)

            isDialogShowing = true // Tandai dialog sedang ditampilkan

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Masukkan Frekuensi Olahraga Dalam 1 Minggu")
                .setView(dialogBinding.root)
                .setPositiveButton("Simpan") { dialog, _ ->
                    val frekuensiOlahragaInput = dialogBinding.etInput.text.toString()
                    if (frekuensiOlahragaInput.isNotEmpty() && frekuensiOlahragaInput.toIntOrNull() != null) {
                        binding.tvValueFrekuensiOlahraga.text = frekuensiOlahragaInput + "x dalam seminggu" // Update UI

                        // save to firebase
                        userViewModel.saveUserProfile(
                            frekuensiOlahraga = frekuensiOlahragaInput.toInt()
                        )

                    } else {
                        dialogBinding.etInput.error = "Masukkan angka yang valid"
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener { isDialogShowing = false } // Reset ketika dialog ditutup
                .show()

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}