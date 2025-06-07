package com.uhuy.noctura.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.uhuy.noctura.data.local.SharedPreferencesHelper
import com.uhuy.noctura.data.local.SharedPreferencesManager

class AlarmStopReceiver: BroadcastReceiver() {
    companion object {
        const val ACTION_ALARM_STOPPED = "com.uhuy.noctura.ALARM_STOPPED"
    }

    private val sharedPreferencesHelper by lazy {
        SharedPreferencesManager.getHelper()
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Stop the alarm sound and vibration
        AlarmReceiver.stopAlarm()

        // Optional: Cancel the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)  // Ensure you match the correct notification ID

        // Create an explicit intent for the AlarmStopReceiver
        val explicitIntent = Intent(context, AlarmStopReceiver::class.java).apply {
            action = ACTION_ALARM_STOPPED
        }

        // Broadcast the explicit intent
        context.sendBroadcast(explicitIntent)
    }
}