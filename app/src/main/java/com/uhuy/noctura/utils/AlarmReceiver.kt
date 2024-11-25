package com.uhuy.noctura.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.uhuy.noctura.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Trigger a notification or action
        val notification = NotificationCompat.Builder(context, "ALARM_CHANNEL")
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Alarm!")
            .setContentText("It's time to wake up!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1001, notification)
    }
}