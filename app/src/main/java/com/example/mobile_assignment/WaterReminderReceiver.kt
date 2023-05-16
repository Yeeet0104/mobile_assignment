package com.example.mobile_assignment

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Check if the received broadcast is for the reminder
        Log.d("ReminderReceiver", "Reminder received!")
        showNotification(context?.applicationContext)
    }

    private fun showNotification(context: Context?) {
        // Create a notification channel (required for Android 8.0 and above)
        createNotificationChannel(context)

        // Create an intent to launch the app
        val intent = context?.packageManager?.getLaunchIntentForPackage(context.packageName)
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


        // Create the pending intent to open the WaterTrackerActivity
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the notification
        val notificationBuilder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setContentTitle("Drink Water Reminder")
            .setContentText("It's time to drink water.")
            .setSmallIcon(R.drawable.mug)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // Set the pending intent for notification click

        // Show the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Reminder Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "reminder_channel"
        private const val NOTIFICATION_ID = 123
    }
}