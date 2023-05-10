package com.example.mobile_assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddCustomFoodActivity : AppCompatActivity() {

    private lateinit var etfoodName: EditText
    private lateinit var etfoodCalories: EditText
    private lateinit var addDataBtn: Button

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_custom_food)

        etfoodName = findViewById(R.id.add_food_name)
        etfoodCalories = findViewById(R.id.add_food_calories)
        addDataBtn = findViewById(R.id.add_food_btn)

        dbRef = FirebaseDatabase.getInstance().getReference("FoodList")

        addDataBtn.setOnClickListener {
            saveFoodData()
        }
    }

    private fun saveFoodData() {
        //getting values
        val foodName = etfoodName.text.toString()
        val foodCalories = etfoodCalories.text.toString()

        if (foodName.isEmpty()){
            etfoodName.error = "Please enter food name!"
            return
        } else {
            etfoodName.error = null
        }

        if (foodCalories.isEmpty()){
            etfoodCalories.error = "Please enter food calories!"
            return
        } else {
            etfoodCalories.error = null
        }


        val foodId = dbRef.push().key!!

        val food = FoodModel(foodId, foodName, foodCalories)

        dbRef.child(foodId).setValue(food).addOnCompleteListener {
            Toast.makeText(this,"Food Added Successfully", Toast.LENGTH_SHORT).show()
            etfoodName.text.clear()
            etfoodCalories.text.clear()

        }.addOnFailureListener {
            err -> Toast.makeText(this,"Error $err.get", Toast.LENGTH_SHORT).show()
        }

    }


}