package com.example.make_a_smile

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var toggleButton: ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleButton = findViewById<ToggleButton>(R.id.toggleButton)
        Log.d("MYLOG","load state")
        toggleButton.isChecked = load_state("button_state")
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            Log.d("MYLOG","save state")
            save_state("button_state", toggleButton.isChecked)
            if (isChecked) {
                Toast.makeText(this, "알림예약", Toast.LENGTH_SHORT).show()
                setRepeatingAlarm(this@MainActivity)
                //showNotification(this)
            } else {
                Toast.makeText(this, "알림중지", Toast.LENGTH_SHORT).show()
                cancelRepeatingAlarm(this@MainActivity)
            }
        }
    }



    private fun save_state(key: String, value: Boolean) {
        val sharedPreferences = getSharedPreferences("button_state", MODE_PRIVATE) // "Button_State"라는 이름으로 파일생성, MODE_PRIVATE는 자기 앱에서만 사용하도록 설정하는 기본값
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value) // 키와 값을 boolean으로 저장
        editor.apply() // 실제로 저장
    }
    private fun load_state(key: String): Boolean {
        val sharedPreferences = getSharedPreferences("button_state", MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, false)
    }

    private fun setRepeatingAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE) //FLAG_UPDATE_CURRENT로 하니 app이 꺼짐.
        //val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT) //있으면 새로만든걸로 update

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 0)

        // Set repeating alarm every day
        //alarmManager.setRepeating : 알림이 안옴.
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelRepeatingAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    private fun showNotification(context: Context) {
        val channelId = "alarm_channel"
        val notificationId = 1

        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("MYLOG","SDK >= VERSION") // alarm이 제대로 들어오는지 확인용
            val channel = NotificationChannel(
                channelId,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setShowBadge(false) //앱위에 알림 표시 안함
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Make-A-Smile")
            .setContentText("한번 미소지어 보세요:)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}