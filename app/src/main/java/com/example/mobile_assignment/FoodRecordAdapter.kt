package com.example.mobile_assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodRecordAdapter (
    private var foodRecordList: MutableList<FoodRecord>,
    private val onRecordDeleted: () -> Unit,
    private val foodHistoryList: MutableList<FoodHistoryModel> = mutableListOf()
    ) : RecyclerView.Adapter<FoodRecordAdapter.ViewHolder>() {

    private val foodRecordFirebase = FoodRecordFirebase()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.calories_tracker_records, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFood = foodRecordList[position]
        holder.tvFoodName.text = currentFood.foodName
        holder.tvFoodCalories.text = "${currentFood.foodCalories} cals"
        holder.tvFoodTime.text = currentFood.foodTime

        when (currentFood.foodTime) {
            "Breakfast" -> holder.foodImage.setImageResource(R.drawable.breakfast)
            "Lunch" -> holder.foodImage.setImageResource(R.drawable.lunch)
            "Dinner" -> holder.foodImage.setImageResource(R.drawable.dinner)
            "Snack" -> holder.foodImage.setImageResource(R.drawable.snack)
        }


        holder.dltButton.setOnClickListener {
            val deleteRecord  = foodHistoryList[position]
            foodRecordFirebase.removeFoodRecord(deleteRecord)


            foodRecordList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, foodRecordList.size)
            onRecordDeleted()
        }


    }

    override fun getItemCount(): Int {
        return foodRecordList.size
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvFoodName: TextView = itemView.findViewById(R.id.food_name)
        val tvFoodCalories: TextView = itemView.findViewById(R.id.calories_input)
        val tvFoodTime: TextView = itemView.findViewById(R.id.calories_time)
        val dltButton : ImageButton = itemView.findViewById(R.id.calories_record_dltbtn)
        val foodImage : ImageView = itemView.findViewById(R.id.food_image)



    }


}