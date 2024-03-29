package com.sriv.shivam.roaster

import android.annotation.SuppressLint
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
import android.widget.Toast
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.sriv.shivam.roaster.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    var isAlarmAlreadySet: Boolean = false
    lateinit var sharedPref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendar = Calendar.getInstance()

        // Get shared preferences
        sharedPref = getSharedPreferences("roasterPref", Context.MODE_PRIVATE)
        editor = sharedPref.edit()

        // Load data from shared preferences
        loadData()

        createNotificationChannel()

        binding.btnSelectTime.setOnClickListener {
            showTimePicker()
        }

        binding.btnSetAlarm.setOnClickListener {
            Log.d("roaster", "In Set Alarm")
            if(!isAlarmAlreadySet) {
                Log.d("roaster", "In if Set Alarm")
                setAlarm()
            }
            else {
                Log.d("roaster", "In else Set Alarm")
                Toast.makeText(this, "Alarm is already set!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancelAlarm.setOnClickListener {
            if(isAlarmAlreadySet) {
                cancelAlarm()
            }
            else {
                Toast.makeText(this, "No alarm is currently active!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadData() {
        val alarm_status = sharedPref.getString("alarmStatus", null)
        isAlarmAlreadySet = sharedPref.getBoolean("isAlarmAlreadySet", false)

        // Set values to respective view
        binding.alarmStatus.text = alarm_status
    }

    private fun cancelAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.cancel(pendingIntent)

        val alarm_status = "Alarm is off"
        isAlarmAlreadySet = false

        // Save data to shared preferences
        saveData(alarm_status, isAlarmAlreadySet)

        binding.alarmStatus.text = alarm_status

        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show()
    }

    private fun setAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        val alarm_status = "Alarm is set to ${binding.tvTime.text}"
        isAlarmAlreadySet = true

        // Save data to shared preferences
        saveData(alarm_status, isAlarmAlreadySet)

        binding.alarmStatus.text = alarm_status
        Toast.makeText(this, "Alarm Set successfully", Toast.LENGTH_SHORT).show()
    }

    private fun saveData(alarmStatus: String, isAlarmAlreadySet: Boolean) {
        editor.apply {
            putString("alarmStatus", alarmStatus)
            putBoolean("isAlarmAlreadySet", isAlarmAlreadySet)
            apply()
        }
    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        picker.show(supportFragmentManager, "roaster")

        picker.addOnPositiveButtonClickListener {
            if(picker.hour > 12) {
                binding.tvTime.text = String.format("%02d", picker.hour - 12) + " : " + String.format("%02d", picker.minute) + " PM"
            }
            else {
                binding.tvTime.text = String.format("%02d", picker.hour) + " : " + String.format("%02d", picker.minute) + " AM"
            }

            if((calendar[Calendar.HOUR_OF_DAY] > picker.hour) || ((calendar[Calendar.HOUR_OF_DAY] == picker.hour) && (calendar[Calendar.MINUTE] > picker.minute))) {
                Log.i("calendar", "picker.hour < HOUR_OF_DAY")
                Log.i("calendar", "Current day: ${calendar[Calendar.DAY_OF_MONTH]}")
                calendar[Calendar.DAY_OF_MONTH]++
                Log.i("calendar", "Alarm shift for: ${calendar[Calendar.DAY_OF_MONTH]}")
            }

            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "RoasterReminderChannel"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("roaster", name, importance)
            channel.description = description

            val notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }
}