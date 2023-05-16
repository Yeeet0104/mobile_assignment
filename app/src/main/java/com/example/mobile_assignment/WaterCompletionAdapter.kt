package com.example.mobile_assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WaterCompletionAdapter(private val items: List<WaterCompletionItem>) :
    RecyclerView.Adapter<WaterCompletionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.water_completion_date)
        val totalConsumedTextView: TextView = itemView.findViewById(R.id.water_completion_total_consumed_amt)
        val dailyTargetTextView: TextView = itemView.findViewById(R.id.water_completion_daily_target)
        val completionImageView: ImageView = itemView.findViewById(R.id.water_completion_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.water_weekly_completion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.dateTextView.text = item.date
        holder.totalConsumedTextView.text = "${item.totalConsumed} ml"
        holder.dailyTargetTextView.text = "${item.dailyTarget} ml"

        if (item.isTargetMet) {
            holder.completionImageView.setImageResource(R.drawable.water_tick_circle)
        } else {
            holder.completionImageView.setImageResource(R.drawable.water_close_circle)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    data class WaterCompletionItem(
        val date: String,
        val totalConsumed: String,
        val dailyTarget: String,
        val isTargetMet: Boolean
    )
}