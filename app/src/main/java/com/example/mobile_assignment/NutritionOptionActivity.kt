package com.example.mobile_assignment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class NutritionOptionActivity : AppCompatActivity() {

    private lateinit var foodSearchView: androidx.appcompat.widget.SearchView
    private lateinit var recentFood: TextView
    private lateinit var popularFood: TextView
    private lateinit var breakfastBtn: Button
    private lateinit var lunchBtn: Button
    private lateinit var snackBtn: Button
    private lateinit var dinnerBtn: Button
    private lateinit var customBtn: Button



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
        }

        lunchBtn.setOnClickListener {
            prevButton?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            prevButton?.setTextColor(Color.BLACK)
            lunchBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen))
            lunchBtn.setTextColor(Color.WHITE)
            prevButton = lunchBtn
        }

        snackBtn.setOnClickListener {
            prevButton?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            prevButton?.setTextColor(Color.BLACK)
            snackBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen))
            snackBtn.setTextColor(Color.WHITE)
            prevButton = snackBtn
        }

        dinnerBtn.setOnClickListener {
            prevButton?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            prevButton?.setTextColor(Color.BLACK)
            dinnerBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen))
            dinnerBtn.setTextColor(Color.WHITE)
            prevButton = dinnerBtn
        }

        customBtn.setOnClickListener {
            val intent = Intent(this, AddCustomFoodActivity::class.java)
            startActivity(intent)
        }

        foodSearchView.setOnClickListener{
            val intent = Intent(this, CaloriesSearchViewActivity::class.java)
            startActivity(intent)
        }

    }


}