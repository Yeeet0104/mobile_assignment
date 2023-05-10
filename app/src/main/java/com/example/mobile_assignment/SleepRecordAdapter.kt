package com.example.mobile_assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SleepRecordAdapter(
    private val recordList: MutableList<com.example.mobile_assignment.Record>,
    private val onRecordDeleted: () -> Unit
) : RecyclerView.Adapter<SleepRecordAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moonIcon: ImageView = itemView.findViewById(R.id.sleep_record_img)
        val timeAdded: TextView = itemView.findViewById(R.id.sleep_record_time)
        val amountConsumed: TextView = itemView.findViewById(R.id.sleep_record_amt)
        val deleteButton: ImageButton = itemView.findViewById(R.id.sleep_record_dlt_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sleep_tracker_record, parent, false)
        return RecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val currentRecord = recordList[position]
        holder.moonIcon.setImageResource(R.drawable.moon)
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
