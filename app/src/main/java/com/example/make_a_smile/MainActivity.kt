package com.example.make_a_smile

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "MyPrefs"
        private const val BUTTON_STATUS_KEY = "buttonStatus"
    }

    private lateinit var button: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.Button)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isButtonEnabled = sharedPreferences.getBoolean(BUTTON_STATUS_KEY, false)
        button.isEnabled = isButtonEnabled

        button.setOnClickListener {
            toggleButtonStatus()
            scheduleOrCancelDailyNotification()
        }
    }

    private fun toggleButtonStatus() {
        val isButtonEnabled = button.isEnabled
        button.isEnabled = !isButtonEnabled

        // Save the button status in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean(BUTTON_STATUS_KEY, button.isEnabled)
        editor.apply()
    }

    private fun scheduleOrCancelDailyNotification() {
        val isButtonEnabled = button.isEnabled
        if (isButtonEnabled) {
            scheduleDailyNotification(this)
            Toast.makeText(this, "Daily notification scheduled.", Toast.LENGTH_SHORT).show()
        } else {
            cancelDailyNotification(this)
            Toast.makeText(this, "Daily notification canceled.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleDailyNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Set the time for the notification (here, it's set to 9 AM)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // Schedule the notification to be shown daily at the specified time
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelDailyNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Cancel the scheduled daily notification
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

}