package com.example.mobile_assignment

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale



class HistoryFoodAdapter (private var completionItem: List<FoodCompletionItem>) : RecyclerView.Adapter<HistoryFoodAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.calories_weekly_completion, parent, false)
        return ViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFood = completionItem[position]

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        val date = LocalDate.parse(currentFood.dateHistory, formatter)
        val dayOfWeek = date.dayOfWeek

        val dayOfWeekString = when (dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "MON"
            java.time.DayOfWeek.TUESDAY -> "TUES"
            java.time.DayOfWeek.WEDNESDAY -> "WED"
            java.time.DayOfWeek.THURSDAY -> "THURS"
            java.time.DayOfWeek.FRIDAY -> "FRI"
            else -> "Unknown"
        }
        holder.tvDayOfWeek.text = "$dayOfWeekString"
        holder.tvTotalCalories.text = "${currentFood.totalCalories}cals"

        var progress = 0
        var totalCalories = 0
        var dailyTarget = 0

        dailyTarget = currentFood.caloriesTarget.toString().replace(Regex("\\D"), "").toInt()
        totalCalories = holder.tvTotalCalories.text.toString().replace(Regex("\\D"), "").toInt()

        progress = (((totalCalories.toDouble()/dailyTarget))*100).toInt()

        if (progress < 50) {
            holder.ivTargetImage.setImageResource(R.drawable.orange)
        } else if (progress < 100) {
            holder.ivTargetImage.setImageResource(R.drawable.yellow)
        } else if (progress >= 100) {
            holder.ivTargetImage.setImageResource(R.drawable.green)
        } else {
            holder.ivTargetImage.setImageResource(R.drawable.gray)
        }

    }

    override fun getItemCount(): Int {
        return completionItem.size
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvDayOfWeek: TextView = itemView.findViewById(R.id.day_of_week)
        val tvTotalCalories: TextView = itemView.findViewById(R.id.history_calories)
        val ivTargetImage : ImageView = itemView.findViewById(R.id.target_images)

    }

    data class FoodCompletionItem(
        val dateHistory: String,
        val totalCalories: String,
        val caloriesTarget: String,
        val isTargetMet: Boolean
    )



}