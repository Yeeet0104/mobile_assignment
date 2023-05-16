package com.example.mobile_assignment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class splashScreenLoading : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_loading)

        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        var bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        var image = findViewById<ImageView>(R.id.imageView)
        var logo = findViewById<TextView>(R.id.textView)
        image.setAnimation(topAnim)
        logo.setAnimation(bottomAnim)

        Handler(Looper.getMainLooper()).postDelayed({
            if(currentUser != null){
                Log.d("CHECKCURRENTUSER",currentUser!!.uid.toString())
                val intent = Intent(
                    this,
                    MainActivity::class.java
                )
                startActivity(intent)
            }else{
                val intent = Intent(
                    application,
                    SplashScreen::class.java
                )
                startActivity(intent)
            }
        }, 3000)
    }
}