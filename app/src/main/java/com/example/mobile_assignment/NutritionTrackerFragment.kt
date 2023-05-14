package com.example.mobile_assignment

import android.app.Activity
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


data class Record(val foodName: String, val foodCalories: String, val foodTime: String)

class NutritionTrackerFragment : Fragment(), SetDailyTargetListener {

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
    private var records = mutableListOf<Record>()
    private var dailyTarget = 2400

    //adapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordAdapter: FoodRecordAdapter

    //temporary data
    private var updatedProgress = 0
    private var isTargetReached = false

    //intent
    private lateinit var caloriesSearchLauncher: ActivityResultLauncher<Intent>




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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the RecyclerView in the layout and set its layout manager (in reverse order)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = false
        recyclerView = view.findViewById(R.id.calories_records_recycler_view)
        recyclerView.layoutManager = linearLayoutManager

        // Create a list of records and set up the adapter
        val foodRecordList = records
        recordAdapter = FoodRecordAdapter(foodRecordList, ::onRecordDeleted)
        recyclerView.adapter = recordAdapter
    }


    fun onFoodConfirmed(foodName: String, foodCalories: String, foodTime: String) {

        //Add record
        val foodTime = "$foodTime"
        val foodCalories = "$foodCalories"
        val foodName = "$foodName"
        val record = Record(foodName, foodCalories, foodTime)
        records.add(record)
        recordAdapter.notifyDataSetChanged()

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

    override fun setDailyTarget(newTarget: Int){
        tvCaloriesTarget = requireView().findViewById(R.id.calories_target)
        dailyTarget = newTarget
        tvCaloriesTarget.text = "Daily Target: ${dailyTarget.toString()} cals"

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