package com.example.mobile_assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WaterRecordAdapter(
    private val recordList: MutableList<Record>,
    private val onRecordDeleted: () -> Unit
    ) : RecyclerView.Adapter<WaterRecordAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cupIcon: ImageView = itemView.findViewById(R.id.water_record_img)
        val timeAdded: TextView = itemView.findViewById(R.id.water_record_time)
        val amountConsumed: TextView = itemView.findViewById(R.id.water_record_amt)
        val deleteButton: ImageButton = itemView.findViewById(R.id.water_record_dlt_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.water_tracker_records, parent, false)
        return RecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val currentRecord = recordList[position]
        holder.cupIcon.setImageResource(R.drawable.mug)
        holder.timeAdded.text = currentRecord.timeAdded
        holder.amountConsumed.text = currentRecord.amountConsumed
        holder.deleteButton.setOnClickListener {
            recordList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, recordList.size)
            onRecordDeleted()
        }
    }

    override fun getItemCount() = recordList.size
}
