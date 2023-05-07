package com.example.mobile_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobile_assignment.databinding.ActivityLoginHomeBinding

class LoginHomeActivity : AppCompatActivity() {

    private lateinit var binding: LoginHomeActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogout.setOnClickListener {
            startActivity(Intent(this, LoginActiivty::class.java))
        }

    }
}