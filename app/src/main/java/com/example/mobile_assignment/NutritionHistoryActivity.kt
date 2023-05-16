package com.example.mobile_assignment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate


class NutritionHistoryActivity : AppCompatActivity() {

    //user
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid.toString()

    private lateinit var caloriesRecords: List<FoodHistoryModel>
    private lateinit var caloriesCompletionAdapter: HistoryFoodAdapter
    private lateinit var caloriesCompletionRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories_history)

        var toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        caloriesCompletionRecyclerView = findViewById(R.id.calories_completion_recycler_view)
        caloriesCompletionRecyclerView.layoutManager = layoutManager

        loadCaloriesRecordsFromFirebase()

    }


    private fun loadCaloriesRecordsFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("NutritionTracker").child("FoodRecord")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                // Loop through the children of the snapshot and create a WaterRecordData object for each
                val calRecordsDB = mutableListOf<FoodHistoryModel>()
                for (recordSnapshot in snapshot.children) {
                    val record = recordSnapshot.getValue(FoodHistoryModel::class.java)
                    record?.let {
                        calRecordsDB.add(it)
                    }
                }

                // Assign the retrieved data to the global variable
                caloriesRecords = calRecordsDB

                // Calculate averages and update UI
                calculateAverages(caloriesRecords)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAverages(records: List<FoodHistoryModel>) {
        val now = LocalDate.now()
        val weekAgo = now.minusWeeks(1)
        val monthAgo = now.minusMonths(1)

        val weekRecords = records.filter { LocalDate.parse(it.dateHistory) >= weekAgo }
        val weekTotal = weekRecords.fold(0.0) { acc, record ->
            val caloriesConsumed = record.dailyCalories?.replace(" cals", "")?.toIntOrNull() ?: 0
            acc + caloriesConsumed
        }
        val weekAverage = weekTotal / weekRecords.size

        // Calculate monthly average
        val monthRecords = records.filter { LocalDate.parse(it.dateHistory) >= monthAgo }
        val monthTotal = monthRecords.fold(0.0) { acc, record ->
            val caloriesConsumed = record.dailyCalories?.replace(" cals", "")?.toIntOrNull() ?: 0
            acc + caloriesConsumed
        }
        val monthAverage = monthTotal / monthRecords.size

        // Calculate Drink Frequency
        val days = records.map { it.dateHistory }.distinct() // get distinct days
        val totalCalFreq = records.size
        val avgCalFreq = if (days.isNotEmpty()) totalCalFreq / days.size else 0

        // Retrieve daily targets and compare with total amount consumed to check whether user hit their daily target
        val targetsRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("NutritionTracker").child("CaloriesTarget")
        var averageCompletion: Double = 0.0

        targetsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val caloriesCompletionItems = mutableListOf<HistoryFoodAdapter.FoodCompletionItem>()

                    for (dateSnapshot in snapshot.children) {
                        val date = dateSnapshot.key
                        val value = dateSnapshot.value

                        val dailyTarget = if (value is Long) {
                            val target = value.toInt()
                            CaloriesDailyTarget(date.toString(), target)
                        } else {
                            dateSnapshot.getValue(CaloriesDailyTarget::class.java)
                        }

                        if (dailyTarget != null && date != null) {
                            val target = dailyTarget.caloriesTarget
                            val recordsForDate = records.filter { it.dateHistory == date }
                            val totalCalConsumed = calculateTotalCaloriesAmount(recordsForDate)


                            val caloriesCompletionItem = HistoryFoodAdapter.FoodCompletionItem(
                                date,
                                totalCalConsumed.toString(),
                                target.toString(),
                                totalCalConsumed >= target
                            )
                            caloriesCompletionItems.add(caloriesCompletionItem)
                        }
                    }

                    // Sort the list by date in descending order
                    caloriesCompletionItems.sortByDescending { it.dateHistory }

                    // Take only the first 7 items
                    val limitedCaloriesCompletionItems = caloriesCompletionItems.take(7)

                    // Calculate the average completion percentage
                    averageCompletion = limitedCaloriesCompletionItems.map { if (it.isTargetMet) 100.0 else 0.0 }.average()

                    // Initialize the RecyclerView and its adapter
                    caloriesCompletionAdapter = HistoryFoodAdapter(limitedCaloriesCompletionItems)
                    // Set the adapter to the RecyclerView
                    caloriesCompletionRecyclerView.adapter = caloriesCompletionAdapter

                    // Update UI with the calculated averages
                    updateNutritionHistoryUI(weekAverage, monthAverage, averageCompletion, avgCalFreq)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation
            }
        })
    }

    fun calculateTotalCaloriesAmount(records: List<FoodHistoryModel>): Int {
        return records.sumOf { record ->
            record.dailyCalories?.replace(" cals", "")?.toIntOrNull() ?: 0
        }
    }
    private fun updateNutritionHistoryUI(weekAverage: Double, monthAverage: Double, averageCompletion: Double, avgDrinkFreq: Int) {
        // Find the water_weekly_avg_value and water_monthly_avg_title views
        val weeklyCalAvg = findViewById<TextView>(R.id.weekly_data)
        val monthlyCalAvg = findViewById<TextView>(R.id.monthly_data)
        val caloriesCompletionAvg = findViewById<TextView>(R.id.completion_data)

        // Set the text of the views to the calculated averages
        weeklyCalAvg.text = if (weekAverage.isNaN()) "0" else weekAverage.toInt().toString() + "cals / day"
        monthlyCalAvg.text = if (monthAverage.isNaN()) "0" else monthAverage.toInt().toString()+ "cals / month"
        caloriesCompletionAvg.text = "$averageCompletion %"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle toolbar item clicks
        when (item.itemId) {
            android.R.id.home -> {
                // If the back button is clicked, finish the activity
                finish()
                return true
            }
            // Handle other menu items if needed

        }
        return super.onOptionsItemSelected(item)
    }
}