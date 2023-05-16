package com.example.mobile_assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PopularFoodAdapter (private var foodList: ArrayList<FoodModel>) : RecyclerView.Adapter<PopularFoodAdapter.ViewHolder>(), Filterable {

    private lateinit var mListener: onItemClickListener
    private var foodListFilter: ArrayList<FoodModel> = ArrayList(foodList)
    private val foodListRecover: ArrayList<FoodModel> = ArrayList(foodList)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.popular_calories_tracker_food_list, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFood = foodListFilter[position]
        holder.tvFoodName.text = currentFood.foodName
        holder.tvFoodCalories.text = "${currentFood.foodCalories} cals"

    }

    override fun getItemCount(): Int {
        return foodListFilter.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val tvFoodName: TextView = itemView.findViewById(R.id.food_list_name)
        val tvFoodCalories: TextView = itemView.findViewById(R.id.food_list_calories)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val searchChr = charSequence.toString().lowercase().trim()
                val filterResults = FilterResults()

                if (charSequence.isNullOrEmpty()) {
                    filterResults.count = foodList.size
                    filterResults.values = foodList
                } else {
                    val foodModel = ArrayList<FoodModel>()

                    for (food in foodList) {
                        if (food.foodName?.lowercase()?.contains(searchChr) == true) {
                            foodModel.add(food)
                        }
                    }

                    filterResults.count = foodModel.size
                    filterResults.values = foodModel
                    foodListFilter = foodModel
                }

                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                if (p1 != null && p1.values is ArrayList<*>) {
                    foodListFilter = p1.values as ArrayList<FoodModel>
                    if (p0.isNullOrEmpty()) {
                        foodList.clear()
                        foodList.addAll(foodListRecover)
                    } else {
                        foodList.clear()
                        foodList.addAll(foodListFilter)
                    }
                    notifyDataSetChanged()
                }

            }
        }
    }
}