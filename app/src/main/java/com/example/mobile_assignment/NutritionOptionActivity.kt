package com.example.mobile_assignment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.w3c.dom.Text


class NutritionOptionActivity : AppCompatActivity() , ConfirmFoodFragment.OnFoodConfirmedListener{


    //Below Time Selection
    private lateinit var foodSearchView: ImageView
    private lateinit var recentFoodLayout: ConstraintLayout
    private lateinit var recentFoodText : TextView
    private lateinit var recentFoodCalories : TextView
    private lateinit var customBtn: Button
    private lateinit var updateBtn: Button
    private lateinit var caloriesSearchLauncher: ActivityResultLauncher<Intent>

    //Time Selection
    private lateinit var breakfastBtn: Button
    private lateinit var lunchBtn: Button
    private lateinit var snackBtn: Button
    private lateinit var dinnerBtn: Button
    private var selectedValue: String? = null

    //popular food result
    private lateinit var foodList: ArrayList<FoodModel>
    private lateinit var foodAdapter: PopularFoodAdapter
    private lateinit var popularFoodRecyclerView: RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories_option)

        var toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        var prevButton: Button? = null
        breakfastBtn = findViewById(R.id.breakfast_btn)
        lunchBtn = findViewById(R.id.lunch_btn)
        snackBtn = findViewById(R.id.snack_btn)
        dinnerBtn = findViewById(R.id.dinner_btn)
        customBtn = findViewById(R.id.custom_food_btn)
        foodSearchView = findViewById(R.id.search_food)
        updateBtn = findViewById(R.id.update_delete_btn)
        recentFoodLayout = findViewById(R.id.recent_constraint_layout)
        recentFoodText = findViewById(R.id.recent_food_text)
        recentFoodCalories = findViewById(R.id.recent_food_cal)

        popularFoodRecyclerView = findViewById(R.id.popular_food_list_recycler_view)
        popularFoodRecyclerView.layoutManager = LinearLayoutManager(this)
        popularFoodRecyclerView.setHasFixedSize(true)

        foodList = ArrayList()
        // Initialize the foodAdapter
        foodAdapter = PopularFoodAdapter(foodList)
        popularFoodRecyclerView.adapter = foodAdapter


        //Load data from phone's storage
        loadData()

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

        //Recent Food
        recentFoodLayout.setOnClickListener {
            if(selectedValue != null) {
                val myDialogFragment = ConfirmFoodFragment()
                val bundle = Bundle()
                bundle.putString("foodName", recentFoodText.text.toString())
                bundle.putString("foodCalories", recentFoodCalories.text.toString().replace(Regex("\\D"), ""))
                bundle.putString("foodTime", selectedValue)
                myDialogFragment.arguments = bundle
                myDialogFragment.show(
                    supportFragmentManager,
                    "ConfirmFoodFragment"
                )
            }else {
                // If none of the buttons are selected, disable the foodSearchView
                Toast.makeText(this@NutritionOptionActivity, "Please choose one of the time", Toast.LENGTH_SHORT).show()
            }
        }


        updateBtn.setOnClickListener {
            val intent = Intent(this, UpdateFoodSearchActivity::class.java)
            finish()
            startActivity(intent)
        }



        getFoodData()
    }

    private fun getFoodData() {
        val dbRef = FirebaseDatabase.getInstance().getReference("FoodList")

        dbRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                foodList.clear()
                if(snapshot.exists()){
                    for(foodSnap in snapshot.children){
                        val foodData = foodSnap.getValue(FoodModel::class.java)
                        foodList.add(foodData!!)
                    }

                    // Limit the number of items to four
                    val limitedFoodList = ArrayList(foodList.take(4))

                    // Initialize the foodAdapter with the limited list
                    foodAdapter = PopularFoodAdapter(limitedFoodList)
                    popularFoodRecyclerView.adapter = foodAdapter


                        foodAdapter.setOnItemClickListener(object :
                            PopularFoodAdapter.onItemClickListener {
                            override fun onItemClick(position: Int) {
                                if(selectedValue != null) {
                                    val myDialogFragment = ConfirmFoodFragment()
                                    val bundle = Bundle()
                                    bundle.putString("foodName", foodList[position].foodName)
                                    bundle.putString("foodCalories", foodList[position].foodCalories)
                                    bundle.putString("foodTime", selectedValue)
                                    myDialogFragment.arguments = bundle
                                    myDialogFragment.show(supportFragmentManager, "ConfirmFoodFragment")
                                }else {
                                    // If none of the buttons are selected, disable the foodSearchView
                                    Toast.makeText(this@NutritionOptionActivity, "Please choose one of the time", Toast.LENGTH_SHORT).show()
                                }
                            }

                        })


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun saveData(){

        val sharedPreferences: SharedPreferences = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()

        editor.apply {
            putString("recentFood", recentFoodText.text.toString())
            putString("recentCalories", recentFoodCalories.text.toString())
        }.apply()
    }

    private fun loadData() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        // Retrieve the saved data using the keys used during saving
        val calories = sharedPreferences.getString("recentCalories", "400 cals")
        val foodName = sharedPreferences.getString("recentFood", "Fried Rice")

        // Update the UI or variables with the retrieved data
        recentFoodCalories.text = "$calories"
        recentFoodText.text = "$foodName"

    }

    private fun updateRecentFood(foodName: String, calories: String){
        recentFoodText.text = "$foodName"
        recentFoodCalories.text = "$calories cals"
    }

    override fun onFoodConfirmed(foodName: String, calories: String, time: String) {

        updateRecentFood(foodName, calories)
        saveData()
        val resultIntent = Intent()
        resultIntent.putExtra("foodName", foodName)
        resultIntent.putExtra("foodCalories", calories)
        resultIntent.putExtra("foodTime", time)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
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