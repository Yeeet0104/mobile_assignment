package com.example.mobile_assignment


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class WaterReminderSettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var reminderInterval: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_reminder_settings)

        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)

        // Set up back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Water Reminder Settings"

        val intervalSpinner: Spinner = findViewById(R.id.reminder_interval_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.reminder_intervals,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            intervalSpinner.adapter = adapter
        }
        intervalSpinner.onItemSelectedListener = this

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position).toString()
        //temp toast
        //Toast.makeText(this, selectedItem, Toast.LENGTH_SHORT).show()
        val reminderInterval = when (selectedItem) {
            "Every 30 minutes" -> 1
            "Every 60 minutes" -> 2
            "Every 90 minutes" -> 3
            "Every 120 minutes" -> 4
            else -> 30 // Default value if none of the options match
        }
        scheduleReminder(reminderInterval)

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    private fun scheduleReminder(reminderInterval: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderIntent = Intent(this, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate the interval time in milliseconds
        val intervalMillis = (reminderInterval * 60 * 1000).toLong()

        // Calculate the initial reminder time
        val currentTimeMillis = System.currentTimeMillis()
        val initialReminderTime = currentTimeMillis + intervalMillis

        // Schedule the repeated reminder using AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            initialReminderTime,
            intervalMillis,
            pendingIntent
        )
    }


}