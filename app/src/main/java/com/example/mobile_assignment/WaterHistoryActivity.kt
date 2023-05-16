package com.example.mobile_assignment

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate


class WaterHistoryActivity : AppCompatActivity() {

    private lateinit var waterRecords: List<WaterRecordData>

    private lateinit var waterCompletionRecyclerView: RecyclerView
    private lateinit var waterCompletionAdapter: WaterCompletionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_history)

        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)

        // Set up back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "History"

        // Find the RecyclerView in the layout and set its layout manager (in reverse order)
        val layoutManager = LinearLayoutManager(this)
        waterCompletionRecyclerView = findViewById(R.id.water_completion_recycler_view)
        waterCompletionRecyclerView.layoutManager = layoutManager

        // Load water records from Firebase
        loadWaterRecordsFromFirebase()

    }

    private fun loadWaterRecordsFromFirebase() {
        val waterRecordsRef = FirebaseDatabase.getInstance().getReference("waterRecords")
        waterRecordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                // Loop through the children of the snapshot and create a WaterRecordData object for each
                val waterRecordsDB = mutableListOf<WaterRecordData>()
                for (recordSnapshot in snapshot.children) {
                    val record = recordSnapshot.getValue(WaterRecordData::class.java)
                    record?.let {
                        waterRecordsDB.add(it)
                    }
                }

                // Assign the retrieved data to the global variable
                waterRecords = waterRecordsDB

                // Calculate averages and update UI
                calculateAverages(waterRecords)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAverages(records: List<WaterRecordData>) {
        val now = LocalDate.now()
        val weekAgo = now.minusWeeks(1)
        val monthAgo = now.minusMonths(1)

        val weekRecords = records.filter { LocalDate.parse(it.dayAdded) >= weekAgo }
        val weekTotal = weekRecords.fold(0.0) { acc, record ->
            val amountConsumed = record.amountConsumed.replace(" ml", "").toIntOrNull() ?: 0
            acc + amountConsumed
        }
        val weekAverage = weekTotal / weekRecords.size

        // Calculate monthly average
        val monthRecords = records.filter { LocalDate.parse(it.dayAdded) >= monthAgo }
        val monthTotal = monthRecords.fold(0.0) { acc, record ->
            val amountConsumed = record.amountConsumed.replace(" ml", "").toIntOrNull() ?: 0
            acc + amountConsumed
        }
        val monthAverage = monthTotal / monthRecords.size

        // Calculate Drink Frequency
        val days = records.map { it.dayAdded }.distinct() // get distinct days
        val totalDrinkFreq = records.size
        val avgDrinkFreq = if (days.isNotEmpty()) totalDrinkFreq / days.size else 0

        // Retrieve daily targets and compare with total amount consumed to check whether user hit their daily target
        val targetsRef = FirebaseDatabase.getInstance().reference.child("waterDailyTargets")
        var averageCompletion: Double = 0.0

        targetsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val waterCompletionItems = mutableListOf<WaterCompletionAdapter.WaterCompletionItem>()

                    for (dateSnapshot in snapshot.children) {
                        val date = dateSnapshot.key
                        val value = dateSnapshot.value

                        val dailyTarget = if (value is Long) {
                            val target = value.toInt()
                            WaterDailyTarget(date.toString(), target)
                        } else {
                            dateSnapshot.getValue(WaterDailyTarget::class.java)
                        }

                        if (dailyTarget != null && date != null) {
                            val target = dailyTarget.target
                            val recordsForDate = records.filter { it.dayAdded == date }
                            val totalAmountConsumed = calculateTotalAmountConsumed(recordsForDate)
                            Log.d("WaterHistoryActivity", "Date: $date, Total Amount Consumed: $totalAmountConsumed, daily target: $target")

                            val waterCompletionItem = WaterCompletionAdapter.WaterCompletionItem(
                                date,
                                totalAmountConsumed.toString(),
                                target.toString(),
                                totalAmountConsumed >= target
                            )
                            waterCompletionItems.add(waterCompletionItem)
                        }
                    }

                    // Calculate the average completion percentage
                    averageCompletion = waterCompletionItems.map { if (it.isTargetMet) 100.0 else 0.0 }.average()

                    // Initialize the RecyclerView and its adapter
                    waterCompletionAdapter = WaterCompletionAdapter(waterCompletionItems)
                    // Set the adapter to the RecyclerView
                    waterCompletionRecyclerView.adapter = waterCompletionAdapter

                    Log.d("WaterHistoryActivity", "averageCompletion2: $averageCompletion")
                    // Update UI with the calculated averages
                    updateWaterReportUI(weekAverage, monthAverage, averageCompletion, avgDrinkFreq)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation
            }
        })
    }

    fun calculateTotalAmountConsumed(records: List<WaterRecordData>): Int {
        return records.sumOf { record ->
            record.amountConsumed.replace(" ml", "").toIntOrNull() ?: 0
        }
    }

    private fun updateWaterReportUI(weekAverage: Double, monthAverage: Double, averageCompletion: Double, avgDrinkFreq: Int) {
        // Find the water_weekly_avg_value and water_monthly_avg_title views
        val weeklyDrinkAvg = findViewById<TextView>(R.id.water_weekly_avg_value)
        val monthlyDrinkAvg = findViewById<TextView>(R.id.water_monthly_avg_value)
        val waterCompletionAvg = findViewById<TextView>(R.id.water_avg_completion_value)
        val drinkFreqAvg = findViewById<TextView>(R.id.water_drink_freq_value)

        // Set the text of the views to the calculated averages
        weeklyDrinkAvg.text = if (weekAverage.isNaN()) "0" else weekAverage.toInt().toString() + "ml / day"
        monthlyDrinkAvg.text = if (monthAverage.isNaN()) "0" else monthAverage.toInt().toString()+ "ml / day"
        waterCompletionAvg.text = "$averageCompletion %"
        drinkFreqAvg.text = "$avgDrinkFreq times / day"
    }
}