package com.example.mobile_assignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_assignment.databinding.ActivityUpdateFoodBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UpdateFoodSearchActivity: AppCompatActivity(){

    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodList: ArrayList<FoodModel>
    private lateinit var binding: ActivityUpdateFoodBinding
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var dbRef: DatabaseReference
    private var selectedValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateFoodBinding.inflate(layoutInflater)
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
                            val intent = Intent(this@UpdateFoodSearchActivity, FoodDetailsActivity::class.java)
                            intent.putExtra("foodId", foodList[position].foodId)
                            intent.putExtra("foodName", foodList[position].foodName)
                            intent.putExtra("foodCalories", foodList[position].foodCalories)
                            finish()
                            startActivity(intent)
                        }

                    })

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}