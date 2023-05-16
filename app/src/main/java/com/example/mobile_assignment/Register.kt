package com.example.mobile_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.compose.ui.text.android.TextLayout
import androidx.core.content.ContextCompat
import com.example.mobile_assignment.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbRef: DatabaseReference
    private var isNotMatch = false
    private var passwordInvalidLength = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        // check for email

        binding.alreadyAccountText.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }


        binding.editEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("check",p0.toString())
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("check",p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                if(!isEmailValid(binding.editEmail.text.toString())){
                    binding.editEmail.error = "Invalid Email Address!"
                }else{
                    binding.editEmail.error = null
                }
            }

        })
        //checking for password length
        checkpasswordLength(binding.editPassword,binding.titlePassword)
        checkpasswordLength(binding.confirmEditPassword,binding.confirmPassword)

        binding.buttonRegister.setOnClickListener {
            if(passwordCompare(binding.editPassword.text.toString(), binding.confirmEditPassword.text.toString()) && binding.editPassword.text.toString().length >= 6 && binding.confirmEditPassword.text.toString().length >= 6){
                isEmailRegistered(binding.editEmail.text.toString()) { isRegistered, errorMessage ->
                    if (isRegistered) {
                        Toast.makeText(this,"Email Already Taken",Toast.LENGTH_SHORT).show()
                    } else {
                        if(isEmailValid(binding.editEmail.text.toString())){
                            val email = binding.editEmail.text.toString()
                            val password = binding.editPassword.text.toString()
                            val username = binding.editName.text.toString()
                            registerUser(username,email, password)
                        }
                        // Email is not registered or an error occurred
                        if (errorMessage != null) {
                            Log.d("checkEmailError",errorMessage)
                        }
                    }
                }
            }else{
                if(isNotMatch && !passwordInvalidLength){
                    binding.titlePassword.error = "Password Not Match"
                    binding.confirmPassword.error = "Password Not Match"
                }else{
                    binding.editPassword.error = null
                    binding.titlePassword.error = null
                    binding.confirmPassword.error = null
                }
            }
        }
    }
    private fun registerUser(userName: String,email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    val uid = user!!.uid
                    val userInfo = registerData(userName,email)
                    if (uid != null) {
                        addIntoRTFireBase(uid, userInfo)
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return emailRegex.matches(email)
    }

    private fun addIntoRTFireBase(uid:String,userInfo:registerData){
        dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child(uid).setValue(userInfo)
    }

    private fun isEmailRegistered(email: String, callback: (Boolean, String?) -> Unit) {
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
    private fun checkpasswordLength(editText: EditText,editTextLayout: TextInputLayout){
         editText.addTextChangedListener(object: TextWatcher {
             override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                 Log.d("check",p0.toString())
             }

             override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                 Log.d("check",p0.toString())
             }

             override fun afterTextChanged(p0: Editable?) {
                    if (editText.text.toString().length < 6){
                        editTextLayout.error = "Must More Than 6 characters"
                        passwordInvalidLength = true
                    }else{
                        passwordInvalidLength = false
                        editTextLayout.error = null
                    }
             }

         })
    }
    private fun passwordCompare(password1: String, password2: String): Boolean {
        isNotMatch = password1 != password2
        return password1 == password2
    }
}