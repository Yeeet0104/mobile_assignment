package com.example.mobile_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobile_assignment.databinding.ActivityRegisterAccountBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAccountBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_register_account)

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this,LoginActiivty::class.java))
        }

        binding.alreadyAccountText.setOnClickListener {
            startActivity(Intent(this, LoginActiivty::class.java))
        }
    }
}