package com.example.mobile_assignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_assignment.databinding.ActivityCaloriesSearchviewBinding
import com.example.mobile_assignment.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CaloriesSearchViewActivity: AppCompatActivity(), ConfirmFoodFragment.OnFoodConfirmedListener {

    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodList: ArrayList<FoodModel>
    private lateinit var binding: ActivityCaloriesSearchviewBinding
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var dbRef: DatabaseReference
    private var selectedValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaloriesSearchviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        foodRecyclerView = findViewById(R.id.food_list_recycler_view)
        foodRecyclerView.layoutManager = LinearLayoutManager(this)
        foodRecyclerView.setHasFixedSize(true)

        foodList = ArrayList()
        // Initialize the foodAdapter
        foodAdapter = FoodAdapter(foodList)
        foodRecyclerView.adapter = foodAdapter

        getFoodData()



        binding.caloriesSearchViewBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.caloriesSearchViewBar.clearFocus()
                // Perform the search and update the UI
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                foodAdapter.filter.filter(newText)
                // Update the UI as the user types
                return true
            }
        })
    }

    private fun getFoodData() {
        val dbRef = FirebaseDatabase.getInstance().getReference("FoodList")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodList.clear()
                if(snapshot.exists()){
                    for(foodSnap in snapshot.children){
                        val foodData = foodSnap.getValue(FoodModel::class.java)
                        foodList.add(foodData!!)
                    }
                    foodAdapter  = FoodAdapter(foodList)
                    foodRecyclerView.adapter = foodAdapter

                    selectedValue = intent.getStringExtra("selectedValue")

                    foodAdapter.setOnItemClickListener(object : FoodAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val myDialogFragment = ConfirmFoodFragment()
                            val bundle = Bundle()
                            bundle.putString("foodName", foodList[position].foodName)
                            bundle.putString("foodCalories", foodList[position].foodCalories)
                            bundle.putString("foodTime", selectedValue)
                            myDialogFragment.arguments = bundle
                            myDialogFragment.show(supportFragmentManager, "ConfirmFoodFragment")
                        }

                    })

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
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