package com.uhuy.noctura.ui.view.home.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.uhuy.noctura.databinding.FragmentAlarmBinding
import com.uhuy.noctura.utils.AlarmReceiver
import java.util.Calendar

class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAlarmBinding.inflate(layoutInflater, container, false)

        setupBtnSetTime()
        setupBtnStartAlarm()
        createNotificationChannel()

        return binding.root
    }

    private fun setupBtnSetTime() {
        // Berdasarkan pengaturan sistem, gunakan format 24H atau 12H
        val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        // Inisialisasi MaterialTimePicker
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12) // Default jam
            .setMinute(10) // Default menit
            .setTitleText("Pilih waktu bangun")
            .build()

        // Tampilkan picker saat tombol diklik
        binding.btnSetWaktuBagun.setOnClickListener {
            picker.show(parentFragmentManager, "TIME_PICKER")
        }

        // Tangkap hasil waktu yang dipilih
        picker.addOnPositiveButtonClickListener {
            val formattedTime = String.format("%02d:%02d", picker.hour, picker.minute)
            binding.btnSetWaktuBagun.text = "$formattedTime"
        }
    }


    private fun setupBtnStartAlarm() {
        binding.btnMulaiAlarm.setOnClickListener {
            val selectedTime =
                binding.btnSetWaktuBagun.text.toString().replace("Waktu bangun: ", "")
            val timeParts = selectedTime.split(":")
            if (timeParts.size != 2) {
                Snackbar.make(it, "Set waktu terlebih dahulu!", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            // Set the alarm time
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }

            // Schedule the alarm
            val alarmManager =
                requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireContext(), AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            Snackbar.make(it, "Alarm set for $selectedTime", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ALARM_CHANNEL",
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Channel for alarm notifications"
            val manager = requireContext().getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}