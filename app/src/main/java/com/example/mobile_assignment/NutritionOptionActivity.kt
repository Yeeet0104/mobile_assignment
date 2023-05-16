package com.example.mobile_assignment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class NutritionOptionActivity : AppCompatActivity() , ConfirmFoodFragment.OnFoodConfirmedListener{

    //Below Time Selection
    private lateinit var foodSearchView: ImageView
    private lateinit var recentFood: TextView
    private lateinit var popularFood: TextView
    private lateinit var customBtn: Button
    private lateinit var updateBtn: Button

    //Time Selection
    private lateinit var breakfastBtn: Button
    private lateinit var lunchBtn: Button
    private lateinit var snackBtn: Button
    private lateinit var dinnerBtn: Button

    private var selectedValue: String? = null

    private lateinit var caloriesSearchLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories_option)

        var prevButton: Button? = null
        breakfastBtn = findViewById(R.id.breakfast_btn)
        lunchBtn = findViewById(R.id.lunch_btn)
        snackBtn = findViewById(R.id.snack_btn)
        dinnerBtn = findViewById(R.id.dinner_btn)
        customBtn = findViewById(R.id.custom_food_btn)
        foodSearchView = findViewById(R.id.search_food)
        updateBtn = findViewById(R.id.update_delete_btn)

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
                    Toast.makeText(this,"BOOM DATA GONE", Toast.LENGTH_SHORT).show()
                }
                // ...
            }
        }

        // Set onClickListener for each button
        breakfastBtn.setOnClickListener {
            // Revert previously clicked button to original color
            prevButton?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            prevButton?.setTextColor(Color.BLACK)
            // Change the color of the clicked button
            breakfastBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen))
            breakfastBtn.setTextColor(Color.WHITE)
            // Set the currently clicked button as the previously clicked button
            prevButton = breakfastBtn
            // Set the selected value to Breakfast
            selectedValue = "Breakfast"
        }

        lunchBtn.setOnClickListener {
            // Revert previously clicked button to original color
            prevButton?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            prevButton?.setTextColor(Color.BLACK)
            // Change the color of the clicked button
            lunchBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen))
            lunchBtn.setTextColor(Color.WHITE)
            // Set the currently clicked button as the previously clicked button
            prevButton = lunchBtn
            // Set the selected value to Lunch
            selectedValue = "Lunch"
        }

        snackBtn.setOnClickListener {
            // Revert previously clicked button to original color
            prevButton?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            prevButton?.setTextColor(Color.BLACK)
            // Change the color of the clicked button
            snackBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen))
            snackBtn.setTextColor(Color.WHITE)
            // Set the currently clicked button as the previously clicked button
            prevButton = snackBtn
            // Set the selected value to Snack
            selectedValue = "Snack"
        }

        dinnerBtn.setOnClickListener {
            // Revert previously clicked button to original color
            prevButton?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            prevButton?.setTextColor(Color.BLACK)
            // Change the color of the clicked button
            dinnerBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen))
            dinnerBtn.setTextColor(Color.WHITE)
            // Set the currently clicked button as the previously clicked button
            prevButton = dinnerBtn
            // Set the selected value to Dinner
            selectedValue = "Dinner"
        }

        customBtn.setOnClickListener {
            val intent = Intent(this, AddCustomFoodActivity::class.java)
            finish()
            startActivity(intent)
        }

        // Use the launcher to start the activity
        foodSearchView.setOnClickListener{
            // Check if any of the four buttons are selected
            if (selectedValue != null) {
                // Start CaloriesSearchViewActivity
                val intent = Intent(this, CaloriesSearchViewActivity::class.java)
                intent.putExtra("selectedValue", selectedValue)
                caloriesSearchLauncher.launch(intent)
            } else {
                // If none of the buttons are selected, disable the foodSearchView
                Toast.makeText(this, "Please choose one of the time", Toast.LENGTH_SHORT).show()
            }
        }

        updateBtn.setOnClickListener {
            val intent = Intent(this, UpdateFoodSearchActivity::class.java)
            finish()
            startActivity(intent)
        }

    }


    override fun onFoodConfirmed(foodName: String, calories: String, time: String) {
        val resultIntent = Intent()
        resultIntent.putExtra("foodName", foodName)
        resultIntent.putExtra("foodCalories", calories)
        resultIntent.putExtra("foodTime", time)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


}