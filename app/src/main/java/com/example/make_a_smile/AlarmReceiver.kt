package com.example.make_a_smile

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "daily_notification_channel"
        private const val NOTIFICATION_ID = 1
        private const val PREFS_NAME = "MyPrefs"
        private const val BUTTON_STATUS_KEY = "buttonStatus"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isButtonEnabled = sharedPreferences.getBoolean(BUTTON_STATUS_KEY, false)

        if (isButtonEnabled) {
            showNotification(context)
        }
    }

    private fun showNotification(context: Context) {
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Daily Notification")
            .setContentText("This is your daily notification.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun scheduleDailyNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun rescheduleDailyNotification(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isButtonEnabled = sharedPreferences.getBoolean(BUTTON_STATUS_KEY, false)

        if (isButtonEnabled) {
            scheduleDailyNotification(context)
        }
    }

    fun cancelDailyNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}