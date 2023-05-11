package com.example.mobile_assignment


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_assignment.databinding.FragmentSleepTrackerBinding
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Record(val timeAdded: String, val amountConsumed: String)

@Suppress("NAME_SHADOWING")
class SleepTrackerFragment : Fragment(), View.OnClickListener, AddSleepFragment.OnSleepAmountAddedListener,
    SetDailyTargetListener {

    //binding
    private var _binding: FragmentSleepTrackerBinding? = null
    private val binding get() = _binding!!

    //temp value: 0
    private var updatedProgress = 0
    private var isTargetReached = false

    //data
    private var records = mutableListOf<Record>()
    private var dailyTarget = 0 //1600 in text

    // Define the RecyclerView and its adapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordAdapter: SleepRecordAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSleepTrackerBinding.inflate(inflater, container, false)
        val view = binding.root

        //Set onClickListener
        binding.addsleepBtn.setOnClickListener(this)
        binding.sleepDailytargetBtn.setOnClickListener(this)
        //binding.editReminderBtn.setOnClickListener(this)
        binding.historyBtn.setOnClickListener(this)

        // Update the sleep tracker UI
        updateSleepConsumptionUI()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the RecyclerView in the layout and set its layout manager (in reverse order)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        recyclerView = view.findViewById(R.id.sleep_records_recycler_view)
        recyclerView.layoutManager = linearLayoutManager

        // Create a list of records and set up the adapter
        val recordList = records
        recordAdapter = SleepRecordAdapter(recordList, ::onRecordDeleted)
        recyclerView.adapter = recordAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    //Add Sleep
    override fun onSleepAmountAdded(amount: Int) {
        //Add Records
        val currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        val formattedTime = currentDateTime.format(formatter)
        val amount = "$amount hr"
        val record = Record(formattedTime, amount)
        records.add(record)
        recordAdapter.notifyDataSetChanged()

        //Show toast after add sleep
        Toast.makeText(context, "$amount of sleep added.", Toast.LENGTH_SHORT).show()

        // Update the sleep tracker UI
        updateSleepConsumptionUI()
    }

    private fun onRecordDeleted() {
        // Update the sleep tracker UI
        updateSleepConsumptionUI()
    }

    private fun updateSleepConsumptionUI() {
        // Update the amount consumed
        var amountConsumed = 0
        for (record in records) {
            amountConsumed += record.amountConsumed.replace(Regex("\\D"), "").toInt()
        }
        binding.consumedsleepAmt.text = "$amountConsumed Hours"

        // Update the progress bar
        updateProgressBar(amountConsumed)

        // Update the visibility of the "Drink some sleep" text
        updateSomeSleepTextVisibility()

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

    private fun updateSomeSleepTextVisibility() {
        view?.findViewById<TextView>(R.id.sleep_record_time)?.apply {
            visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
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
        updateSleepConsumptionUI()
    }

    override fun getCurrentDailyTarget(): Int {
        // Retrieve the current daily target from your application or class
        return dailyTarget
    }

    //OnClick Listeners
    override fun onClick(v: View?) {
        when (v) {

            binding.addsleepBtn -> {
                val showAddSleepPopUp = AddSleepFragment()
                showAddSleepPopUp.onSleepAmountAddedListener = this
                showAddSleepPopUp.show(
                    (activity as AppCompatActivity).supportFragmentManager, "showAddSleepPopUp"
                )
            }

            binding.sleepDailytargetBtn -> {
                val editsleepDailyTarget = EditSleepDailyTargetFragment()
                editsleepDailyTarget.setDailyTargetListener = this
                editsleepDailyTarget.show(
                    (activity as AppCompatActivity).supportFragmentManager, "editSleepDailyTarget"
                )
            }

//            binding.editReminderBtn -> {
//                val reminderIntent = Intent(activity, SleepReminderSettingsActivity::class.java)
//                startActivity(reminderIntent)
//            }

            binding.historyBtn -> {
                val historyIntent = Intent(activity, SleepHistory::class.java)
                startActivity(historyIntent)
            }

        }
    }


    override fun onResume() {
        super.onResume()
        // Update the sleep tracker UI
        updateSleepConsumptionUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}