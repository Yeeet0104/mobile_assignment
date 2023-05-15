package com.example.mobile_assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SleepRecordAdapter(
    private val recordList: MutableList<SleepRecord>,
    private val onRecordDeleted: (SleepRecord) -> Unit
) : RecyclerView.Adapter<SleepRecordAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder components
        private val moonIcon: ImageView = itemView.findViewById(R.id.sleep_record_img)
        private val date: TextView = itemView.findViewById(R.id.sleep_record_date)
        private val sleepTime: TextView = itemView.findViewById(R.id.sleep_record_total_hours)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.sleep_record_dlt_btn)

        init {
            // Set click listener for the delete button
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val deletedRecord = recordList[position]
                    recordList.removeAt(position)
                    notifyItemRemoved(position)
                    onRecordDeleted(deletedRecord)
                }
            }
        }

        fun bind(record: SleepRecord) {
            // Bind the data to the ViewHolder components
            moonIcon.setImageResource(R.drawable.moon)
            date.text = record.date
            sleepTime.text = record.sleepTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sleep_tracker_record, parent, false)
        return RecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val currentRecord = recordList[position]
        holder.bind(currentRecord)
    }

    override fun getItemCount() = recordList.size
}
