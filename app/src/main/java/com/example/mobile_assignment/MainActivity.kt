package com.example.mobile_assignment

import android.app.Notification.Action
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

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav:BottomNavigationView
    private lateinit var navController:NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav = findViewById(R.id.bottomNavigationView)

        navController = findNavController(R.id.fragment)
        bottomNav.setupWithNavController(navController)
//        setupBottomNavigation()

        drawerLayout = findViewById(R.id.drawer_layout)


        var toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)

        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        val navigation_view = findViewById<NavigationView>(R.id.nav_view)
        NavigationUI.setupWithNavController(navigation_view,navController)
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.workoutFragment,
//                R.id.waterTrackerFragment,
//                R.id.nutritionTrackerFragment,
//                R.id.sleepTrackerFragment,
//                R.id.profileFragment
//            )
//        )

//        setupActionBarWithNavController(navController, appBarConfiguration)
//
    }
    override fun onSupportNavigateUp(): Boolean {
        //return navController.navigateUp()
        return NavigationUI.navigateUp(navController,appBarConfiguration)

    }

    private fun setupBottomNavigation() {
        bottomNav.setupWithNavController(navController)
    }

}