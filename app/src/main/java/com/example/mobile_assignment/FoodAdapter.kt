package com.example.mobile_assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter (private val foodList: ArrayList<FoodModel>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>(){
    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvFoodName : TextView = itemView.findViewById(R.id.food_list_name)
        val tvFoodCalories : TextView = itemView.findViewById(R.id.food_list_calories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.calories_tracker_food_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFood = foodList[position]
        holder.tvFoodName.text = currentFood.foodName
        holder.tvFoodCalories.text = "${currentFood.foodCalories} cals"
    }

    override fun getItemCount(): Int {
        return foodList.size
    }


}