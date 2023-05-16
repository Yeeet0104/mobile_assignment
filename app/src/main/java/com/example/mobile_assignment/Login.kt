package com.example.mobile_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.mobile_assignment.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogin.setOnClickListener {
            isEmailExist(binding.editEmail.text.toString()) { isRegistered, errorMessage ->
                if (!isRegistered) {
                    binding.titleEmail.error = "Login Failed! Invalid Email or passwrd!"
                    binding.titlePassword.error = "Login Failed! Invalid Email or passwrd!"
                } else{
                    binding.titleEmail.error = null
                    binding.titlePassword.error = null
                    login(binding.editEmail.text.toString(), binding.editPassword.text.toString())
                }
            }

        }

        binding.haventAccountText.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }

    }


    private fun login(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }
    private fun isEmailExist(email: String, callback: (Boolean, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()

        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    val isRegistered = signInMethods != null && signInMethods.isNotEmpty()
                    callback(isRegistered, null)
                } else {
                    val exception = task.exception
                    val errorMessage = exception?.message
                    callback(false, errorMessage)
                }
            }
    }
}