package com.example.mobile_assignment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar


data class FoodRecord(val foodName: String, val foodCalories: String, val foodTime: String)

class NutritionTrackerFragment : Fragment(), SetDailyCaloriesTargetListener {

    //user
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid.toString()


    //text view
    private lateinit var tvCaloriesProgress : TextView
    private lateinit var tvCaloriesTarget : TextView
    private lateinit var tvCaloriesReminder : TextView

    //Clickable Button and progress
    private lateinit var navCaloriesBtn: ImageButton
    private lateinit var editCaloriesPop: ImageButton
    private lateinit var navHistoryBtn: Button
    private lateinit var caloriesProgressBar: ProgressBar

    //data
    private val foodRecordFirebase = FoodRecordFirebase()
    private var records = mutableListOf<FoodRecord>()
    private var dailyTarget = 2400
    @RequiresApi(Build.VERSION_CODES.O)
    private val zoneID = ZoneId.of("Asia/Kuala_Lumpur")
    private var historyRecords = mutableListOf<FoodHistoryModel>()

    //adapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordAdapter: FoodRecordAdapter

    //temporary data
    private var updatedProgress = 0
    private var isTargetReached = false

    //intent
    private lateinit var caloriesSearchLauncher: ActivityResultLauncher<Intent>




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        caloriesSearchLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result here
                val data = result.data
                val foodName = data?.getStringExtra("foodName")
                val foodCalories = data?.getStringExtra("foodCalories")
                val foodTime = data?.getStringExtra("foodTime")
                if (foodName != null && foodCalories != null && foodTime != null) {
                        onFoodConfirmed(foodName, foodCalories, foodTime)
                }
                else {
                    Toast.makeText(context,"BOOM DATA GONE", Toast.LENGTH_SHORT).show()
                }
                // ...
            }
        }

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_nutrition_tracker, container, false)

        //Navigation to add food into the data
        navCaloriesBtn = view.findViewById(R.id.add_calories_btn)

        navCaloriesBtn.setOnClickListener {
            val intent = Intent(activity, NutritionOptionActivity::class.java)
            caloriesSearchLauncher.launch(intent)
        }

        //Navigate into history fragment
        navHistoryBtn = view.findViewById(R.id.calories_history_btn)

        navHistoryBtn.setOnClickListener {
            val intent = Intent(activity, NutritionHistoryActivity::class.java)
            startActivity(intent)
        }

        //Clickable ImageButton to edit the daily calories amount
        editCaloriesPop = view.findViewById(R.id.edit_calories_btn)

        editCaloriesPop.setOnClickListener {
            val showPopUp = EditCaloriesFragment()
            showPopUp.setDailyTargetListener = this
            showPopUp.show((activity as AppCompatActivity).supportFragmentManager,"showPopUp")
        }

        return view

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        // Find the RecyclerView in the layout and set its layout manager (in reverse order)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = false
        recyclerView = view.findViewById(R.id.calories_records_recycler_view)
        recyclerView.layoutManager = linearLayoutManager

        // Create a list of records and set up the adapter
        recordAdapter = FoodRecordAdapter(records,::onRecordDeleted, historyRecords)
        recyclerView.adapter = recordAdapter


    }

    private fun saveData(){

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs_$currentUser", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Convert the list of records to a JSON string
        val gson = Gson()
        val recordsJson = gson.toJson(records)
        val historyRecordsJson = gson.toJson(historyRecords)

        editor.apply {
            putInt("dailyTarget", dailyTarget)
            putString("records", recordsJson)
            putString("historyRecords", historyRecordsJson)
            putInt("updatedProgress", updatedProgress)
        }.apply()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs_$currentUser", Context.MODE_PRIVATE)
        val storedDay = sharedPreferences.getInt("storedDay", -1)

        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        if (currentDay != storedDay) {
            // Clear the SharedPreferences
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.putInt("storedDay", currentDay)
            editor.apply()

        }

        tvCaloriesTarget = requireView().findViewById(R.id.calories_target)
        // Retrieve the saved data using the keys used during saving
        dailyTarget = sharedPreferences.getInt("dailyTarget", 2400)

        // Update the UI or variables with the retrieved data
        tvCaloriesTarget.text = "Daily Target: ${dailyTarget.toString()} cals"

        // Retrieve the saved records JSON string from SharedPreferences
        val recordsJson = sharedPreferences.getString("records", null)

        // Convert the JSON string back to a list of records
        if (recordsJson != null) {
            val gson = Gson()
            val type = object : TypeToken<List<FoodRecord>>() {}.type
            records = gson.fromJson(recordsJson, type)
        }

        val historyRecordsJson = sharedPreferences.getString("historyRecords", null)

        // Convert the JSON string back to a list of records
        if (historyRecordsJson != null) {
            val gson = Gson()
            val type = object : TypeToken<List<FoodHistoryModel>>() {}.type
            historyRecords = gson.fromJson(historyRecordsJson, type)
        }

        //Update Progress Bar
        caloriesProgressBar = requireView().findViewById(R.id.calories_bar)
        caloriesProgressBar.progress = updatedProgress

        foodRecordFirebase.setDailyCaloriesTarget(dailyTarget)
        updateCaloriesUI()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun onFoodConfirmed(foodName: String, foodCalories: String, foodTime: String) {

        //Add record
        val foodTime = "$foodTime"
        val foodCalories = "$foodCalories"
        val foodName = "$foodName"
        val record = FoodRecord(foodName, foodCalories, foodTime)
        records.add(record)
        recordAdapter.notifyDataSetChanged()


        //Add Records
        val currentDateTime = LocalDateTime.now(zoneID)
        val dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDay = currentDateTime.format(dayFormatter)
        val recordHistory = FoodHistoryModel(formattedDay, foodCalories)

        historyRecords.add(recordHistory)

        // Add data to firebase
        foodRecordFirebase.addFoodRecord(recordHistory)

        //Show toast after add water
        Toast.makeText(context, "$foodCalories of calories added.", Toast.LENGTH_SHORT).show()

        //update UI
        updateCaloriesUI()
    }

    private fun updateCaloriesUI(){
        tvCaloriesProgress = requireView().findViewById(R.id.text_calories_progress)
        tvCaloriesReminder = requireView().findViewById(R.id.calories_reminder)
        var caloriesAdded = 0
        for (r in records){
            caloriesAdded += r.foodCalories.replace(Regex("\\D"), "").toInt()
        }

        tvCaloriesProgress.text = "$caloriesAdded CALS"

        //Update Progress Bar
        updateProgressBar(caloriesAdded)

        //Update visibility of empty text
        updateConsumeFoodVisibility()

        saveData()

    }

    private fun updateProgressBar(caloriesAdded: Int) {
        tvCaloriesTarget = requireView().findViewById(R.id.calories_target)
        dailyTarget = tvCaloriesTarget.text.toString()
            .replace(Regex("\\D"), "")
            .toInt()
        updatedProgress = (((caloriesAdded.toDouble()/dailyTarget))*100).toInt()

        var caloriesLeft =0
        caloriesLeft = dailyTarget-caloriesAdded
        tvCaloriesReminder.text = "$caloriesLeft cals to go. Good Luck!"

        caloriesProgressBar = requireView().findViewById(R.id.calories_bar)
        caloriesProgressBar.progress = updatedProgress

        //Display congratulations msg to user when user hits the daily target
        if (!isTargetReached && (caloriesAdded >= dailyTarget)) {
            isTargetReached = true
            Toast.makeText(context, "Congratulations! You have reached your daily calories intake target.", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setDailyCaloriesTarget(newTarget: Int){
        tvCaloriesTarget = requireView().findViewById(R.id.calories_target)
        dailyTarget = newTarget
        tvCaloriesTarget.text = "Daily Target: ${dailyTarget.toString()} cals"

        foodRecordFirebase.setDailyCaloriesTarget(dailyTarget)

        //Show toast after set daily target
        Toast.makeText(context, "Daily target set to ${dailyTarget.toString()} cals", Toast.LENGTH_SHORT).show()
        updateCaloriesUI()
    }

    private fun onRecordDeleted(){
        // Update the calories tracker UI
        updateCaloriesUI()
    }

    override fun onResume() {
        super.onResume()
        // Update the calories tracker UI
        updateCaloriesUI()

    }

    private fun updateConsumeFoodVisibility() {
        view?.findViewById<TextView>(R.id.consume_some_food)?.apply {
            visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun getCurrentDailyTarget(): Int {
        // Retrieve the current daily target from your application or class
        return dailyTarget
    }




}