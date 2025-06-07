package com.uhuy.noctura.ui.view.home

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.uhuy.noctura.data.firebase.FirebaseAuthService
import com.uhuy.noctura.data.firebase.FirebaseDatabaseService
import com.uhuy.noctura.data.local.SharedPreferencesManager
import com.uhuy.noctura.data.model.SleepPostRequest
import com.uhuy.noctura.data.repository.AuthRepository
import com.uhuy.noctura.data.repository.UserRepository
import com.uhuy.noctura.databinding.BottomSheetLayoutBinding
import com.uhuy.noctura.databinding.DialogPredictionResultBinding
import com.uhuy.noctura.ui.viewmodel.InferenceViewModel
import com.uhuy.noctura.ui.viewmodel.SleepDataViewModel
import com.uhuy.noctura.utils.Resource
import com.uhuy.noctura.utils.TimeConverter
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SleepDataBottomSheet(
    private val context: Context,
    private val viewModel: SleepDataViewModel,
    private val inferenceViewModel: InferenceViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val fragmentManager: FragmentManager  // Tambahkan parameter ini
) {
    private var binding: BottomSheetLayoutBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private var datePicker: MaterialDatePicker<Long>? = null
    private var timePicker: MaterialTimePicker? = null
    private var alertDialog: AlertDialog? = null

    private var isShowing = false
    private var isSaving = false  // Add this flag to prevent multiple save attempts

    fun show() {
        if (isShowing) return // Prevent multiple dialogs from showing

        isShowing = true
        isSaving = false  // Reset saving state

        // Create new instance every time to ensure fresh state
        binding = BottomSheetLayoutBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog = BottomSheetDialog(context).apply {
            setContentView(binding!!.root)
            setOnDismissListener {
                binding = null
                // Clean up all references
                binding = null
                bottomSheetDialog = null
                alertDialog?.dismiss()
                alertDialog = null
                datePicker = null
                timePicker = null
                this@SleepDataBottomSheet.isShowing = false
            }
        }
        initDatePicker()
        initTimePicker()
        initSaveButton()

        bottomSheetDialog!!.show()
    }

    private fun initDatePicker() {
        binding!!.etDate.setOnClickListener {
            setupDatePicker { selectedDate ->
                binding!!.etDate.setText(selectedDate)
            }
        }
    }

    private fun initTimePicker() {
        binding!!.etSleepTime.setOnClickListener {
            setupTimePicker("Pilih waktu tidur") { selectedTime ->
                binding!!.etSleepTime.setText(selectedTime)
            }
        }

        binding!!.etWakeupTime.setOnClickListener {
            setupTimePicker("Pilih waktu bangun") { selectedTime ->
                binding!!.etWakeupTime.setText(selectedTime)
            }
        }
    }

    private fun initSaveButton() {
        binding!!.btnSave.isEnabled = false

        val textWatcher = createTextWatcher()

        binding!!.apply {
            etDate.addTextChangedListener(textWatcher)
            etSleepTime.addTextChangedListener(textWatcher)
            etWakeupTime.addTextChangedListener(textWatcher)
            etAwakenings.addTextChangedListener(textWatcher)
            etCaffeineAmount.addTextChangedListener(textWatcher)
            etAlcoholAmount.addTextChangedListener(textWatcher)
        }

        binding!!.btnSave.setOnClickListener {
            handleSaveButtonClick()
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding!!.btnSave.isEnabled = areFieldsValid()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun areFieldsValid(): Boolean {
        return binding!!.run {
            val requiredFieldsValid = etDate.text.toString().trim().isNotEmpty() &&
                    etSleepTime.text.toString().trim().isNotEmpty() &&
                    etWakeupTime.text.toString().trim().isNotEmpty()

            val awakeningValid = etAwakenings.text.toString().trim().let {
                it.isEmpty() || it.toIntOrNull() != null
            }
            val caffeineValid = etCaffeineAmount.text.toString().trim().let {
                it.isEmpty() || it.toIntOrNull() != null
            }
            val alcoholValid = etAlcoholAmount.text.toString().trim().let {
                it.isEmpty() || it.toIntOrNull() != null
            }
            requiredFieldsValid && awakeningValid && caffeineValid && alcoholValid

        }
    }

    private fun handleSaveButtonClick() {
        Log.d("AlarmFragment", "Handle save button click")
        val sleepData = createSleepDataFromInputs()
        Log.d("AlarmFragment", sleepData.toString())
        // Launch coroutine to handle async prediction
        lifecycleOwner.lifecycleScope.launch {
            try {
                binding?.btnSave?.isEnabled = false  // Disable the button while processing
                // Start prediction
                Log.d("AlarmFragment", "Starting prediction...")
                inferenceViewModel.predict(sleepData)
                // Observe prediction changes
                inferenceViewModel.prediction
                    .onEach {
                        Log.d("AlarmFragment", "Prediction flow value: $it")
                    }
                    .filterNotNull()
                    .collect { predictionValue ->
                        Log.d("AlarmFragment", "Prediction: $predictionValue")
                        sleepData.sleepQuality = predictionValue
                        Log.d("AlarmFragment", "Saving sleep data with prediction...")
                        viewModel.createSleepData(context, listOf(sleepData))
                        showStatusDialog(predictionValue)
                        observeSaveResult()
                        return@collect
                    }
            } catch (e: Exception) {
                Log.e("AlarmFragment", "Error during prediction: ${e.message}")
                isSaving = false
                binding?.btnSave?.isEnabled = true  // Re-enable the button on error
                Snackbar.make(
                    binding?.root ?: return@launch,
                    "Error: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showStatusDialog(prediction: Float) {
        if (alertDialog != null && alertDialog!!.isShowing) {
            Log.d("AlarmFragment", "Dialog already showing")
            return // Prevent creating another instance if one is already showing
        }

        // Inflate the custom dialog layout using ViewBinding
        val dialogBinding = DialogPredictionResultBinding.inflate(LayoutInflater.from(context))

        // Set the emoji and message in the dialog
        val statusEmoji: String
        val statusMsg: String
        val predictionRounded = String.format("%.2f", prediction).toFloat()

        when (predictionRounded) {
            in 0.5f..0.69f -> {
                statusEmoji = "😢"
                statusMsg = "Kualitas tidur anda masih kurang baik"
            }

            in 0.7f..0.79f -> {
                statusEmoji = "😊"
                statusMsg = "Kualitas tidur anda sudah cukup, tapi masih bisa ditingkatkan lagi"
            }

            in 0.8f..1.0f -> {
                statusEmoji = "😃"
                statusMsg = "Kualitas tidur anda sudah sangat baik"
            }

            else -> {
                statusEmoji = "❓"
                statusMsg = "Nilai prediksi tidak valid"
            }
        }

        // Set the emoji and message in the dialog
        dialogBinding.dialogEmoji.text = statusEmoji
        dialogBinding.dialogMessage.text = statusMsg

        // Create and show the dialog
        alertDialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .setCancelable(true) // Allow the dialog to be dismissed
            .create()

        // Set the OK button's click listener
        dialogBinding.btnOk.setOnClickListener {
            alertDialog!!.dismiss() // Dismiss the dialog
            bottomSheetDialog?.dismiss()
        }
        alertDialog!!.setOnDismissListener {
            alertDialog = null
            if (!isSaving) bottomSheetDialog!!.dismiss()
        }
        //        Tampilkan alert dialog
        alertDialog!!.show()
    }


    private fun calculateSleepDuration(sleepTime: Float, wakeUpTime: Float): Float {
        return if (wakeUpTime >= sleepTime) {
            // Wake-up time is on the same day as sleep time
            wakeUpTime - sleepTime
        } else {
            // Wake-up time is on the next day
            (24 - sleepTime) + wakeUpTime
        }
    }

    private fun createSleepDataFromInputs(): SleepPostRequest {
        val sleepDate = binding!!.etDate.text.toString().trim()
        val sleepTime = binding!!.etSleepTime.text.toString().trim()
        val wakeUpTime = binding!!.etWakeupTime.text.toString().trim()
        val awakenings = binding!!.etAwakenings.text.toString().toIntOrNull()
        val caffeineConsumption = binding!!.etCaffeineAmount.text.toString().toIntOrNull()
        val alcoholConsumption = binding!!.etAlcoholAmount.text.toString().toIntOrNull()

//        Kalkulasi waktu tidur
        val sleepTimeInNumber = TimeConverter.convertTimeToFloat(sleepTime)
        val wakeUpTimeInNumber = TimeConverter.convertTimeToFloat(wakeUpTime)
        val sleepDuration = calculateSleepDuration(sleepTimeInNumber, wakeUpTimeInNumber)

        return SleepPostRequest(
            FirebaseAuthService().getCurrentUser()?.uid ?: "",
            sleepDate,
            sleepTime,
            wakeUpTime,
            sleepDuration,
            awakenings ?: 0,
            caffeineConsumption ?: 0,
            alcoholConsumption ?: 0
        )
    }

    private fun observeSaveResult() {
        viewModel.createStatus.observe(lifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("AlarmFragment", "Mengirim Data...")
                    binding?.btnSave?.isEnabled = false
                }

                is Resource.Success -> {
                    Log.d("AlarmFragment", "Data berhasil disimpan")
                    isSaving = false
                }

                is Resource.Error -> {
                    Log.d("AlarmFragment", "Terjadi Kesalahan: ${resource.message}")
                    isSaving = false
                    binding?.btnSave?.isEnabled = true
                    Snackbar.make(
                        binding!!.root,
                        resource.message ?: "Gagal save data.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                else -> {}
            }
        }
    }

    private fun setupTimePicker(
        title: String,
        hour: Int? = null,
        minute: Int? = null,
        onTimeSelected: (String) -> Unit
    ) {
        if (timePicker != null && timePicker!!.isVisible) {
            return // Prevent showing multiple instances
        }
        val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(context)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(hour ?: 12)
            .setMinute(minute ?: 0)
            .setTitleText(title)
            .build()

        timePicker!!.addOnPositiveButtonClickListener {
            val formattedTime = String.format("%02d:%02d", timePicker!!.hour, timePicker!!.minute)
            onTimeSelected(formattedTime)
        }
        timePicker!!.addOnDismissListener {
            timePicker = null
        }
        timePicker!!.show(fragmentManager, title)
    }

    private fun setupDatePicker(
        onDateSelected: (String) -> Unit
    ) {
        if (datePicker != null && datePicker!!.isVisible) {
            return // Prevent showing multiple instances
        }

        // Create a SimpleDateFormat instance to format the selected date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Initialize separate MaterialDatePickers for sleep time and wakeup time
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker!!.addOnPositiveButtonClickListener { selection ->
            val selectedDate = dateFormat.format(Date(selection))
            onDateSelected(selectedDate)
        }

        // Menambahkan listener untuk dismiss
        datePicker!!.addOnDismissListener {
            datePicker = null
        }

        // Menampilkan date picker
        datePicker!!.show(fragmentManager, "DatePicker")
    }


}