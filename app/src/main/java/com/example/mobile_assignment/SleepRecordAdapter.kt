package com.example.mobile_assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SleepRecordAdapter(
    private val recordList: MutableList<Record>,
    private val onRecordDeleted: () -> Unit
) : RecyclerView.Adapter<SleepRecordAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moonIcon: ImageView = itemView.findViewById(R.id.sleep_record_img)
//        val timeAdded: TextView = itemView.findViewById(R.id.sleep_record_time)
        val weekday: TextView = itemView.findViewById(R.id.sleep_record_weekday)
        val totalHour: TextView = itemView.findViewById(R.id.sleep_record_total_hours)
        val deleteButton: ImageButton = itemView.findViewById(R.id.sleep_record_dlt_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sleep_tracker_record, parent, false)
        return RecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val currentRecord = recordList[position]
        holder.moonIcon.setImageResource(R.drawable.moon)
//        holder.timeAdded.text = currentRecord.timeAdded

        // Get the weekday from the record's timeAdded property
//        val calendar = Calendar.getInstance().apply {
//            time = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).parse(currentRecord.timeAdded)!!
//        }
//        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
//
//        holder.date.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)
//        holder.weekday.text = dayOfWeek
        holder.totalHour.text = currentRecord.amountConsumed
        holder.deleteButton.setOnClickListener {
            recordList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, recordList.size)
            onRecordDeleted()
        }
    }

    override fun getItemCount() = recordList.size
}
