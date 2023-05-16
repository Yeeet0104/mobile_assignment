package com.example.mobile_assignment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.mobile_assignment.databinding.ActivityLoginMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(){
    private lateinit var bottomNav:BottomNavigationView
    private lateinit var navController:NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginMainBinding

    override fun onStart() {
        super.onStart()
        workoutResetValues()
    }
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

        drawerLayout = findViewById(R.id.drawer_layout)


        var toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }


    private fun workoutResetValues(){
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        var dbref = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("workoutPlans")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    snapshot.children.forEach{
                        if(it.key == "lastWorkoutDate"){
                            //date 1 follow by date 2
                            var result =compareDate(convertStringToDate(it.value.toString())!!,Date())
                            if (result < 0) {
                                dbref.child(it.key.toString()).setValue(getTodayDate())
                                resetProgress(dbref)
                                Log.d("checkDate","now is after the db date")
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun resetProgress( dbref: DatabaseReference){

        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    snapshot.children.forEach{
                        if(it.key != "lastWorkoutDate" && it.key != "dailyTarget" ){
                            //date 1 follow by date 2
                            var newProgress = "0/0"
                            it.children.forEach{value->
                                if (value.key == "progress"){
                                    var returnString = value.value.toString().split("/")
                                    newProgress = "0/" + returnString[1]
                                }
                            }
                            dbref.child(it.key.toString()).child("progress").setValue(newProgress)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    private fun compareDate(date1: Date,date2:Date):Int{
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2

        cal1.set(Calendar.HOUR_OF_DAY, 0)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)
        cal1.set(Calendar.MILLISECOND, 0)

        cal2.set(Calendar.HOUR_OF_DAY, 0)
        cal2.set(Calendar.MINUTE, 0)
        cal2.set(Calendar.SECOND, 0)
        cal2.set(Calendar.MILLISECOND, 0)

        var value = cal1.time.compareTo(cal2.time)
        Log.d("checkCompareDate",value.toString())
        return value
    }
    private fun convertStringToDate(dateString: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            return format.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    private fun getTodayDate() : String{
        val calender = Calendar.getInstance().time
        val formatterDate = SimpleDateFormat("yyyy-MM-dd")
        val date = formatterDate.format(calender)
        val formatterTimer = SimpleDateFormat("yyyy-MM-dd")
        val time = formatterTimer.format(calender)

        return time
    }
}