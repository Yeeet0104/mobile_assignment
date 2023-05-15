package com.example.mobile_assignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


class SleepHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_history)

        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)

        // Set up back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "History"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}