package com.example.mobile_assignment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class splashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)

        val button_login = findViewById<Button>(R.id.button_login)
        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser != null){
            Log.d("CHECKCURRENTUSER",currentUser!!.uid.toString())
            val intent = Intent(
                this,
                MainActivity::class.java
            )
            startActivity(intent)
        }
        button_login.setOnClickListener {
            val intent = Intent(
                this,
                Login::class.java
            )
            startActivity(intent)
        }

        val button_register = findViewById<Button>(R.id.button_register)
        button_register.setOnClickListener {
            val intent = Intent(
                this,
                Register::class.java
            )
            startActivity(intent)
        }
    }
}