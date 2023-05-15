package com.example.mobile_assignment

import android.app.AlertDialog
import android.icu.text.AlphabeticIndex
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class WaterRecordAdapter(
    private val recordList: MutableList<WaterRecordData>,
    private val onRecordDeleted: () -> Unit
    ) : RecyclerView.Adapter<WaterRecordAdapter.RecordViewHolder>() {

    private val waterRecordFirebase = WaterRecordFirebase()

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cupIcon: ImageView = itemView.findViewById(R.id.water_record_img)
        val dayAdded: TextView = itemView.findViewById(R.id.water_record_day)
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
        holder.dayAdded.text = currentRecord.dayAdded
        holder.timeAdded.text = currentRecord.timeAdded
        holder.amountConsumed.text = currentRecord.amountConsumed

        holder.deleteButton.setOnClickListener {
            // Remove data from firebase
            val recordToDelete = recordList[position]

            // Display an AlertDialog to confirm whether the user wants to delete the record or not
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this record?")
                .setPositiveButton("Delete") { dialog, _ ->
                    // Remove data from Firebase
                    waterRecordFirebase.removeWaterRecord(recordToDelete)

                    // Remove the record from the list and notify the adapter of the change
                    recordList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, recordList.size)

                    // Call the onRecordDeleted callback function
                    onRecordDeleted()

                    // Dismiss the dialog
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
                .show()
        }

    }

    fun updateRecords(newRecords: List<WaterRecordData>) {
        recordList.clear()
        recordList.addAll(newRecords)
    }

    override fun getItemCount() = recordList.size
}
