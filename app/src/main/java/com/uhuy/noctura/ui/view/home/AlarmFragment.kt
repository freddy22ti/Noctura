package com.uhuy.noctura.ui.view.home

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.uhuy.noctura.data.firebase.FirebaseDatabaseService
import com.uhuy.noctura.data.local.SharedPreferencesManager
import com.uhuy.noctura.data.model.SleepPostRequest
import com.uhuy.noctura.data.network.RetrofitInstance
import com.uhuy.noctura.data.repository.SleepRepository
import com.uhuy.noctura.data.repository.UserRepository
import com.uhuy.noctura.databinding.BottomSheetLayoutBinding
import com.uhuy.noctura.databinding.DialogPredictionResultBinding
import com.uhuy.noctura.databinding.FragmentAlarmBinding
import com.uhuy.noctura.ui.viewmodel.InferenceViewModel
import com.uhuy.noctura.ui.viewmodel.InferenceViewModelFactory
import com.uhuy.noctura.ui.viewmodel.SleepDataViewModel
import com.uhuy.noctura.utils.AlarmReceiver
import com.uhuy.noctura.utils.AlarmStopReceiver
import com.uhuy.noctura.utils.Resource
import com.uhuy.noctura.utils.ViewModelFactory
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    private val sleepDataViewModel: SleepDataViewModel by activityViewModels {
        ViewModelFactory(SleepDataViewModel::class.java) {
            val repository = SleepRepository(RetrofitInstance.getCrudApi())
            SleepDataViewModel(repository)
        }
    }

    private val sharedPreferencesHelper by lazy {
        SharedPreferencesManager.getHelper()
    }

    private val userRepository by lazy {
        UserRepository(FirebaseDatabaseService(), sharedPreferencesHelper)
    }

    private val inferenceViewModel: InferenceViewModel by activityViewModels {
        InferenceViewModelFactory(requireContext(), userRepository)
    }

    private lateinit var alarmStopReceiver: BroadcastReceiver

    private var isDialogShowing = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi receiver
        alarmStopReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == AlarmStopReceiver.ACTION_ALARM_STOPPED) {
                    // Update UI ketika alarm dihentikan
                    sharedPreferencesHelper.setAlarmStatus(false)
                    updateAlarmButtonState()
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAlarmBinding.inflate(layoutInflater, container, false)

        setupBtnSetTime()
        setupBtnStartAlarm()
        setupFAB()

        return binding.root
    }

    private fun setupFAB() {
        binding.floatingActionButton.setOnClickListener {
            val bottomSheet = SleepDataBottomSheet(
                requireContext(),
                sleepDataViewModel,
                inferenceViewModel,
                viewLifecycleOwner,
                parentFragmentManager
            )
            bottomSheet.show()
        }
    }

    private fun setupBtnSetTime() {
        // Tampilkan picker saat tombol diklik
        var alarmTime = sharedPreferencesHelper.getAlarmTime()

        if (alarmTime.isNullOrEmpty()) {
            alarmTime = "12:00"
        }
        // Set jam alarm
        binding.btnSetWaktuBagun.text = alarmTime

        binding.btnSetWaktuBagun.setOnClickListener {
            if (isDialogShowing) return@setOnClickListener

            isDialogShowing = true

            // Split the alarm time into hour and minute
            val timeParts = alarmTime?.split(":")
            if (timeParts?.size != 2) {
                Snackbar.make(it, "Set waktu terlebih dahulu!", Snackbar.LENGTH_LONG).show()
                isDialogShowing = false
                return@setOnClickListener
            }

            // Extract hour and minute, ensuring they're valid integers
            val hour = timeParts[0].toIntOrNull() ?: 12  // Default to 12 if invalid
            val minute = timeParts[1].toIntOrNull() ?: 0  // Default to 0 if invalid


            setupTimePicker("Pilih waktu bangun", hour, minute) { selectedTime ->
                sharedPreferencesHelper.setAlarmTime(selectedTime)
                binding.btnSetWaktuBagun.text = selectedTime
            }
        }
    }


    private fun setupBtnStartAlarm() {
        updateAlarmButtonState()
        binding.btnMulaiAlarm.setOnClickListener {
            val selectedTime = binding.btnSetWaktuBagun.text.toString()
            val timeParts = selectedTime.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager =
                    requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.e("AlarmFragment", "Permission for exact alarms is not granted.")
                    Snackbar.make(
                        it,
                        "Permission required to set exact alarms!",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Grant") {
                            requestExactAlarmPermission()
                        }.show()
                    return@setOnClickListener
                }
            }

            val isAlarmStart = sharedPreferencesHelper.getAlarmStatus()
            Log.d("AlarmFragment", "Alarm state is $isAlarmStart when clicked" )
            if (isAlarmStart) {
                Log.d("AlarmFragment", "Cancelling the alarm.")
                cancelAlarm()
                sharedPreferencesHelper.setAlarmStatus(false)  // Save the state after canceling
            } else {
                Log.d("AlarmFragment", "Scheduling the alarm.")
                scheduleAlarm(hour, minute)
                sharedPreferencesHelper.setAlarmStatus(true)  // Save the state after canceling
            }
            updateAlarmButtonState()
        }
    }


    // Method to update the UI elements based on the alarm state
    private fun updateAlarmButtonState() {
        val isAlarmStart = sharedPreferencesHelper.getAlarmStatus()
        if (isAlarmStart) {
            binding.btnMulaiAlarm.text = "Batalkan Alarm"
//            binding.ivCancel.visibility = View.VISIBLE
            binding.btnSetWaktuBagun.isEnabled = false // Disable the button
        } else {
            binding.btnMulaiAlarm.text = "Mulai Alarm"
//            binding.ivCancel.visibility = View.GONE
            binding.btnSetWaktuBagun.isEnabled = true // Enable the button
        }
        Log.d("AlarmFragment", "Alarm state is $isAlarmStart")
    }


    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("AlarmFragment", "Requesting permission for exact alarms.")

            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }
    }


    @SuppressLint("DefaultLocale")
    //    Untuk memulai alarm
    private fun scheduleAlarm(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // If the time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Snackbar.make(
            binding.root,
            "Alarm set for ${String.format("%02d:%02d", hour, minute)}",
            Snackbar.LENGTH_LONG
        ).show()
    }


    //    Untuk menghentikan alarm
    private fun cancelAlarm() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,  // Same requestCode used during scheduling
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        Snackbar.make(binding.root, "Alarm canceled", Snackbar.LENGTH_SHORT).show()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    private fun setupTimePicker(
        title: String,
        hour: Int? = null,
        minute: Int? = null,
        onTimeSelected: (String) -> Unit
    ) {
        val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(hour ?: 12)
            .setMinute(minute ?: 0)
            .setTitleText(title)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val formattedTime = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
            onTimeSelected(formattedTime)
        }

        // Menambahkan listener untuk dismiss
        timePicker.addOnDismissListener {
            isDialogShowing = false  // Mengubah isDialogShowing menjadi false ketika dialog ditutup
        }

        timePicker.show(parentFragmentManager, title)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        // Ensure the alarm time is displayed correctly whenever the fragment is resumed
        val alarmTime = sharedPreferencesHelper.getAlarmTime()

        if (!alarmTime.isNullOrEmpty()) {
            binding.btnSetWaktuBagun.text = alarmTime
        } else {
            binding.btnSetWaktuBagun.text = "12:00"
        }

        // Register receiver
        requireActivity().registerReceiver(
            alarmStopReceiver,
            IntentFilter(AlarmStopReceiver.ACTION_ALARM_STOPPED), Context.RECEIVER_NOT_EXPORTED
        )
        // Update UI state
        updateAlarmButtonState()
    }

    override fun onPause() {
        super.onPause()
        // Unregister receiver
        try {
            Log.d("AlarmFragment", "Unregistering receiver")
            requireActivity().unregisterReceiver(alarmStopReceiver)
        } catch (e: Exception) {
            Log.e("AlarmFragment", "Error unregistering receiver: ${e.message}")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNotificationPermission()
        updateAlarmButtonState()
    }
}