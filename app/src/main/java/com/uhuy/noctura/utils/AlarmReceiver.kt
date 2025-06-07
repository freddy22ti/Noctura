package com.uhuy.noctura.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "ALARM_CHANNEL"
        const val NOTIFICATION_ID = 1

        private var ringtone: Ringtone? = null
        private var vibrator: Vibrator? = null

        // Fungsi statis untuk menghentikan alarm
        fun stopAlarm() {
            ringtone?.stop()
            vibrator?.cancel()

            // Reset variabel
            ringtone = null
            vibrator = null
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    override fun onReceive(context: Context, intent: Intent) {
//        Membuat notifikasi
        createNotificationChannel(context)
        showStopAlarmNotification(context)

//        Membunyikan alarm
        playAlarmSound(context)
    }

    // Tambahkan fungsi ini
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm notifications"
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playAlarmSound(context: Context) {
        // Use RingtoneManager to play the default alarm sound
        ringtone = RingtoneManager.getRingtone(
            context,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        )
        if (ringtone == null) {
            // Fallback ke ringtone notifikasi jika alarm tidak tersedia
            ringtone = RingtoneManager.getRingtone(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            )
        }
        ringtone?.play()

        // Optional: Increase volume if necessary (example: set to max volume)
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            0
        )

        // Optionally, add vibration
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrator?.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    @SuppressLint("LaunchActivityFromNotification")
    private fun showStopAlarmNotification(context: Context) {
        // Create the stop action for the notification
        val stopIntent = Intent(context, AlarmStopReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Alarm is ringing!")
            .setContentText("Tap to stop the alarm.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(stopPendingIntent, true)
            .addAction(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent)
            .setAutoCancel(true) // Automatically remove the notification after tapping
            .build()

        // Show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification) // Unique notification ID
    }
}