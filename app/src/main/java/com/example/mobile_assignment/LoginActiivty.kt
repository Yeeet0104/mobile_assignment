package com.example.mobile_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobile_assignment.databinding.ActivityLoginAccountBinding

class LoginActiivty : AppCompatActivity() {

    private lateinit var binding: ActivityLoginAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_account)

        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(this,LoginHomeActivity::class.java))
        }
       binding.haventAccountText.setOnClickListener {
           startActivity(Intent(this,RegisterActivity::class.java))
       }
    }
}