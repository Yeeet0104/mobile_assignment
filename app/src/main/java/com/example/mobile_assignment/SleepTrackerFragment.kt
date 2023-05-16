package com.example.mobile_assignment


import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


data class SleepRecord(
    var date: String = "", var sleepTime: String = "", var key: String? = null
    // Add a key property
) {
    // Empty constructor required for Firebase deserializationl
    constructor() : this("", "")
}


@Suppress("NAME_SHADOWING", "DEPRECATION")
class SleepTrackerFragment : Fragment(), View.OnClickListener, SetSleepDailyTargetListener,
    SetDailyTargetListener {

    //data
    private var records = mutableListOf<SleepRecord>()
    private val recordList: MutableList<SleepRecord> = mutableListOf()
    private var dailyTarget = 0

    // Define the callback function for deleting sleep records
    private val onRecordDeleted: (SleepRecord) -> Unit = ::onDeleteRecord

    // Define the RecyclerView and its adapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordAdapter: SleepRecordAdapter

    // Add a variable to hold the current time
    private var currentTime: Long = 0

    // Add a variable to hold the timer
    private var timer: CountDownTimer? = null

    // Display system current time for title
    private lateinit var currentDateTextView: TextView

    // Write a message to the database
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid.toString()
    val database = Firebase.database
    val sleepRecordsRef = database.getReference("sleepRecords")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sleep_tracker, container, false)

        // Set onClickListener
        view.findViewById<Button>(R.id.sleep_dailytarget_btn).setOnClickListener(this)

        // Set the onClickListener for the start button
        view.findViewById<ImageButton>(R.id.playsleep_btn).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.stopsleep_btn).setOnClickListener(this)

        // Initialize the RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.sleep_records_recycler_view)
        recordAdapter = SleepRecordAdapter(recordList, onRecordDeleted)

        // Read a message from the database ( from user )
        getRecordFromFirebase()

        // Set up the RecyclerView with the adapter
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recordAdapter
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the currentDateTextView in the layout
        currentDateTextView = view.findViewById(R.id.currentDateTextView)

        // Set the current date to the currentDateTextView
        val currentDate =
            DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format(Date())
        currentDateTextView.text = currentDate

        // Find the RecyclerView in the layout and set its layout manager (in reverse order)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        recyclerView = view.findViewById(R.id.sleep_records_recycler_view)
        recyclerView.layoutManager = linearLayoutManager

        // Set up the adapter with the existing recordList
        recordAdapter = SleepRecordAdapter(recordList, ::onDeleteRecord)
        recyclerView.adapter = recordAdapter

        // Update the RecyclerView with the records
        recordAdapter.notifyDataSetChanged()

    }


    private fun onDeleteRecord(record: SleepRecord) {
        // Remove the record from the local list
        recordList.remove(record)
        recordAdapter.notifyDataSetChanged()

        // Remove the record from Firebase
        val recordKey = record.key
        if (recordKey != null) {
            val recordRef =
                FirebaseDatabase.getInstance().getReference("sleepRecords").child(recordKey)
            recordRef.removeValue().addOnSuccessListener {
                // Record deleted successfully
                Log.d("Firebase", "Record deleted successfully: $recordKey")
            }.addOnFailureListener { e ->
                // Error occurred while deleting the record
                Log.e("Firebase", "Error deleting record: $recordKey", e)
            }
        } else {
            Log.e("Firebase", "Record key is null")
        }
    }


    //OnClick Listeners
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.playsleep_btn -> {
                // Set the current time to the duration of the timer (in milliseconds)
                currentTime = Long.MAX_VALUE

                // Update bed_record_time with the current time
                updateBedRecordTime(view)

                // Create a new CountDownTimer object
                timer = object : CountDownTimer(currentTime, 1000) {
                    var elapsedTime: Long = 0

                    override fun onTick(millisUntilFinished: Long) {
                        // Update the elapsed time
                        elapsedTime += 1000

                        // Update the UI with the elapsed time
                        view?.findViewById<TextView>(R.id.sleep_time)?.text = String.format(
                            "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(elapsedTime),
                            TimeUnit.MILLISECONDS.toSeconds(elapsedTime) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    elapsedTime
                                )
                            )
                        )
                    }

                    override fun onFinish() {
                        // Handle the timer finishing
                        view?.findViewById<TextView>(R.id.sleep_time)?.text = "00:00"
                    }
                }

                // Start the timer
                timer?.start()

                // Show the stop button
                view?.findViewById<ImageButton>(R.id.stopsleep_btn)?.visibility = View.VISIBLE
            }

            R.id.stopsleep_btn -> {
                // Stop the timer
                timer?.cancel()

                // Hide the stop button
                view?.findViewById<ImageButton>(R.id.stopsleep_btn)?.visibility = View.GONE

                // After updating the wake-up record time, you can add the sleep record to the list
                val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
                val currentDate = dateFormat.format(Date())
                val sleepTime = view?.findViewById<TextView>(R.id.sleep_time)?.text.toString()


                // Create a new sleep record
                val sleepRecord = SleepRecord(currentDate, sleepTime)

                // Add the record to the list
                records.add(sleepRecord)

                // Update the wakeup record time
                updateWakeUpRecordTime(view)

                // Update the RecyclerView and notify the adapter of the data change
                recordAdapter.notifyItemInserted(records.size - 1)
                recyclerView.scrollToPosition(records.size - 1)

                // Display the record list
                showRecordList()

                // Save Record to Firebase
                saveRecordToFirebase(currentDate, sleepTime)
            }

            R.id.sleep_dailytarget_btn -> {
                changeDailyTarget()
            }

        }
    }

    private fun saveRecordToFirebase(currentDate: String, sleepTime: String) {
        val sleepRecord = SleepRecord(currentDate, sleepTime)
        val recordRef = sleepRecordsRef.push()
        val recordKey = recordRef.key
        sleepRecord.key = recordKey // Assign the generated key to the SleepRecord
        recordRef.setValue(sleepRecord).addOnSuccessListener {
            // Record saved successfully
            Log.d("Firebase", "Record saved successfully with ID: ${recordRef.key}")
        }.addOnFailureListener { e ->
            // Error occurred while saving the record
            Log.e("Firebase", "Error saving record", e)
        }
    }

    private fun getRecordFromFirebase() {
        val sleepRecordsRef =
            FirebaseDatabase.getInstance().getReference("users").child(currentUser)
                .child("sleepRecords")

        // Retrieve sleep record data from Firebase
        sleepRecordsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the existing recordList
                recordList.clear()

                for (recordSnapshot in dataSnapshot.children) {
                    val sleepRecord = recordSnapshot.getValue(SleepRecord::class.java)
                    sleepRecord?.let {
                        recordList.add(it)
                    }
                }

                // Update the RecyclerView adapter with the new data
                recordAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Log.e("Firebase", "Error retrieving sleep record data: ${error.message}")
            }
        })
    }


    private fun updateBedRecordTime(view: View?) {
        // Get the current time in milliseconds
        val currentTime = System.currentTimeMillis()

        // Create a Date object from the current time
        val date = Date(currentTime)

        // Format the date to display as a string
        val format = SimpleDateFormat("h:mm a")
        val timeString = format.format(date)

        // Update the bed_record_time TextView with the current time
        val bedRecordTimeTextView = view?.findViewById<TextView>(R.id.bed_record_time)
        bedRecordTimeTextView?.text = timeString

    }

    private fun updateWakeUpRecordTime(view: View?) {
        // Get the current time in milliseconds
        val currentTime = System.currentTimeMillis()

        // Create a Date object from the current time
        val date = Date(currentTime)

        // Format the date to display as a string
        val format = SimpleDateFormat("h:mm a")
        val timeString = format.format(date)

        // Update the wakeup_record_time TextView with the current time
        val wakeUpRecordTimeTextView = view?.findViewById<TextView>(R.id.wakeup_record_time)
        wakeUpRecordTimeTextView?.text = timeString
    }

    private fun showRecordList() {
        // Clear the current list and add all records
        recordList.clear()
        recordList.addAll(records)
        recordAdapter.notifyDataSetChanged()
    }

    //Set Daily Target & get daily target
    override fun setDailyTarget(newDailyTarget: Int) {
        dailyTarget = newDailyTarget
        val sleepDailyTargetBtn = requireView().findViewById<Button>(R.id.sleep_dailytarget_btn)
        sleepDailyTargetBtn.text = "Daily Target: ${dailyTarget.toString()} hours"

        //Show toast after setting the daily target
        Toast.makeText(
            requireContext(),
            "Daily target set to ${dailyTarget.toString()} hours",
            Toast.LENGTH_SHORT
        ).show()

    }


    override fun getCurrentDailyTarget(): Int {
        // Retrieve the current daily target from your application or class
        return dailyTarget
    }

    private fun changeDailyTarget() {
        val editSleepDailyTargetFragment = EditSleepDailyTargetFragment()
        editSleepDailyTargetFragment.setDailyTargetListener = this // Set the listener
        editSleepDailyTargetFragment.show(
            requireActivity().supportFragmentManager, "editSleepDailyTarget"
        )
    }


}