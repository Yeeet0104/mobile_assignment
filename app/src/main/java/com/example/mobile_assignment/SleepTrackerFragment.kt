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
import java.util.concurrent.TimeUnit

data class SleepRecord(val timeAdded: String, val amountConsumed: String)

@Suppress("NAME_SHADOWING")
class SleepTrackerFragment : Fragment(), View.OnClickListener, SetDailyTargetListener,
    SetDailySleepTargetListener {

    //binding
    private var _binding: FragmentSleepTrackerBinding? = null
    private val binding get() = _binding!!

    //temp value: 0
    private var updatedProgress = 0
    private var isTargetReached = false

    //data
    private var records = mutableListOf<SleepRecord>()
    private var dailyTarget = 0 //1600 in text

    // Define the RecyclerView and its adapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordAdapter: SleepRecordAdapter

    // Add a variable to hold the current time
    private var currentTime: Long = 0

    // Add a variable to hold the timer
    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sleep_tracker, container, false)

        //Set onClickListener
        view.findViewById<Button>(R.id.sleep_dailytarget_btn).setOnClickListener(this)

        //binding.editReminderBtn.setOnClickListener(this)
        view.findViewById<Button>(R.id.history_btn).setOnClickListener(this)

        // Set the onClickListener for the start button
        view.findViewById<ImageButton>(R.id.playsleep_btn).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.stopsleep_btn).setOnClickListener(this)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the RecyclerView in the layout and set its layout manager (in reverse order)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        recyclerView = view.findViewById(R.id.sleep_records_recycler_view)
        recyclerView.layoutManager = linearLayoutManager

    }

    private fun updateProgressBar(amountConsumed: Int) {

        dailyTarget = binding.sleepDailytargetBtn.text.toString().replace(Regex("\\D"), "").toInt()
        updatedProgress = (((amountConsumed.toDouble() / dailyTarget)) * 100).toInt()

        binding.sleeptrackerCpb.progress = updatedProgress

        //Display congratulations msg to user when user hits the daily target
        if (!isTargetReached && (amountConsumed >= dailyTarget)) {
            isTargetReached = true
            Toast.makeText(
                context,
                "Congratulations! You have reached your daily sleep hours intake target.",
                Toast.LENGTH_LONG
            ).show()
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
}