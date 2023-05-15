package com.example.mobile_assignment


import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_assignment.databinding.FragmentSleepTrackerBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class SleepRecord(
    val date: String, val sleepTime: String, val totalHours: Double
)

@Suppress("NAME_SHADOWING")
class SleepTrackerFragment : Fragment(), View.OnClickListener, SetDailyTargetListener,
    SetDailySleepTargetListener {

    //binding
    private var _binding: FragmentSleepTrackerBinding? = null
    private val binding get() = _binding!!

    //temp value: 0
    private var isTargetReached = false

    //data
    private var records = mutableListOf<SleepRecord>()
    private val recordList: MutableList<SleepRecord> = mutableListOf()
    private var dailyTarget = 0 //1600 in text

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sleep_tracker, container, false)

        // Set onClickListener
        view.findViewById<Button>(R.id.sleep_dailytarget_btn).setOnClickListener(this)

        // binding.editReminderBtn.setOnClickListener(this)
        view.findViewById<Button>(R.id.history_btn).setOnClickListener(this)

        // Set the onClickListener for the start button
        view.findViewById<ImageButton>(R.id.playsleep_btn).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.stopsleep_btn).setOnClickListener(this)

        // Initialize the RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.sleep_records_recycler_view)
        recordAdapter = SleepRecordAdapter(recordList, onRecordDeleted)

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
        // Handle the deletion of the sleep record
        // You can implement your logic here, such as removing the record from the list and updating the UI
        records.remove(record)
        recordAdapter.notifyDataSetChanged()

        updateSleepTrackerUI()
    }


    private fun updateSleepTrackerUI() {
        // Calculate the total hours of all sleep records
        var totalHours = 0.0
        for (record in records) {
            totalHours += record.totalHours
        }

    }

    private fun updateSleepTextVisibility() {
        val sleepMoreTextView = view?.findViewById<TextView>(R.id.sleep_more)
        if (records.isEmpty()) {
            sleepMoreTextView?.visibility = View.VISIBLE
        } else {
            sleepMoreTextView?.visibility = View.GONE
        }
    }


    //Set Daily Target & get daily target
    override fun setDailyTarget(newDailyTarget: Int) {
        dailyTarget = newDailyTarget
        binding.sleepDailytargetBtn.text = "Daily Target: ${dailyTarget.toString()}hr"

        //Show toast after set daily target
        Toast.makeText(
            context, "Daily target set to ${dailyTarget.toString()}hr", Toast.LENGTH_SHORT
        ).show()
    }

    override fun getCurrentDailyTarget(): Int {
        // Retrieve the current daily target from your application or class
        return dailyTarget
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
                val currentDate = DateFormat.getDateInstance().format(Date())
                val sleepTime = view?.findViewById<TextView>(R.id.sleep_time)?.text.toString()

                // Calculate the total hours of sleep (you can adjust this calculation based on your requirements)
                val totalHours = calculateTotalHours(sleepTime)

                // Create a new sleep record
                val sleepRecord = SleepRecord(currentDate, sleepTime, totalHours)

                // Add the record to the list
                records.add(sleepRecord)

                // Update the wakeup record time
                updateWakeUpRecordTime(view)

                // Update the RecyclerView and notify the adapter of the data change
                recordAdapter.notifyItemInserted(records.size - 1)
                recyclerView.scrollToPosition(records.size - 1)

                // Display the record list
                showRecordList()
            }

            R.id.sleep_dailytarget_btn -> {
                val editsleepDailyTarget = EditSleepDailyTargetFragment()
                editsleepDailyTarget.setDailyTargetListener = this
                editsleepDailyTarget.show(
                    (activity as AppCompatActivity).supportFragmentManager, "editSleepDailyTarget"
                )
            }

            R.id.history_btn -> {
                val historyIntent = Intent(activity, SleepHistory::class.java)
                startActivity(historyIntent)
            }

        }
    }

    private fun calculateTotalHours(sleepTime: String): Double {
        // Perform the necessary calculation to determine the total hours of sleep
        // You can adjust this calculation based on the sleep time format and requirements of your app
        // Here's an example implementation assuming the sleep time is in HH:mm format
        val sleepTimeParts = sleepTime.split(":")
        val hours = sleepTimeParts[0].toInt()
        val minutes = sleepTimeParts[1].toInt()
        val totalHours = hours + (minutes / 60.0)
        return totalHours
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
        updateSleepTextVisibility()
    }

}