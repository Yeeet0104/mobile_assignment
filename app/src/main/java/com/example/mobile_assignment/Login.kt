package com.example.mobile_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.mobile_assignment.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonLogin.setOnClickListener {
            login(binding.editEmail.text.toString(), binding.editPassword.text.toString()) { user, errorMessage ->
                if (user != null) {
                    // Login successful, user is logged in
                    // You can perform any additional actions here
                    var intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed or an error occurred
                    if (errorMessage != null) {
                        // Handle the error message
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        binding.haventAccountText.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

    }


    private fun login(email: String, password: String, callback: (FirebaseUser?, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    callback(user, null)
                } else {
                    val exception = task.exception
                    val errorMessage = exception?.message
                    callback(null, errorMessage)
                }
            }
    }
}