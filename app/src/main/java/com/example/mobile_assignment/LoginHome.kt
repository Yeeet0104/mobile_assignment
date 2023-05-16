package com.example.mobile_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobile_assignment.databinding.ActivityLoginHomeBinding

class LoginHome : AppCompatActivity() {

    private lateinit var binding: ActivityLoginHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_home)
        binding = ActivityLoginHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogout.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }
}