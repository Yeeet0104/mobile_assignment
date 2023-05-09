package com.example.mobile_assignment

import android.app.Notification.Action
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.mobile_assignment.databinding.ActivityLoginMainBinding
import com.example.mobile_assignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        setContentView(R.layout.activity_main)
        bottomNav = findViewById(R.id.bottomNavigationView)

        navController = findNavController(R.id.fragment)
        bottomNav.setupWithNavController(navController)
//        setupBottomNavigation()

        drawerLayout = findViewById(R.id.drawer_layout)


        var toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.workoutFragment,
                R.id.waterTrackerFragment,
                R.id.nutritionTrackerFragment,
                R.id.sleepTrackerFragment,
                R.id.profileFragment
            ),
            drawerLayout
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        NavigationUI.setupWithNavController(navigationView, navController)


    }

    override fun onSupportNavigateUp(): Boolean {
        //return navController.navigateUp()
        return NavigationUI.navigateUp(navController, appBarConfiguration)

    }

    private fun setupBottomNavigation() {
        bottomNav.setupWithNavController(navController)
    }

}