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
import com.example.mobile_assignment.databinding.FragmentWaterTrackerBinding
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Record(val timeAdded: String, val amountConsumed: String)

@Suppress("NAME_SHADOWING")
class WaterTrackerFragment : Fragment(), View.OnClickListener, OnWaterAmountAddedListener,
    SetDailyTargetListener {

    //binding
    private var _binding: FragmentWaterTrackerBinding? = null
    private val binding get() = _binding!!

    //temp value: 0
    private var updatedProgress = 0
    private var isTargetReached = false

    //data
    private var records = mutableListOf<Record>()
    private var dailyTarget = 0 //1600 in text

    // Define the RecyclerView and its adapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordAdapter: WaterRecordAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaterTrackerBinding.inflate(inflater, container, false)
        val view = binding.root

        //Set onClickListener
        binding.addwaterBtn.setOnClickListener(this)
        binding.waterDailytargetBtn.setOnClickListener(this)
        binding.editReminderBtn.setOnClickListener(this)
        binding.historyBtn.setOnClickListener(this)

        // Update the water tracker UI
        updateWaterConsumptionUI()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the RecyclerView in the layout and set its layout manager (in reverse order)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        recyclerView = view.findViewById(R.id.water_records_recycler_view)
        recyclerView.layoutManager = linearLayoutManager

        // Create a list of records and set up the adapter
        val recordList = records
        recordAdapter = WaterRecordAdapter(recordList, ::onRecordDeleted)
        recyclerView.adapter = recordAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    //Add Water
    override fun onWaterAmountAdded(amount: Int) {
        //Add Records
        val currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        val formattedTime = currentDateTime.format(formatter)
        val amount = "$amount ml"
        val record = Record(formattedTime, amount)
        records.add(record)
        recordAdapter.notifyDataSetChanged()

        //Show toast after add water
        Toast.makeText(context, "$amount of water added.", Toast.LENGTH_SHORT).show()

        // Update the water tracker UI
        updateWaterConsumptionUI()
    }

    private fun onRecordDeleted() {
        // Update the water tracker UI
        updateWaterConsumptionUI()
    }

    private fun updateWaterConsumptionUI() {
        // Update the amount consumed
        var amountConsumed = 0
        for (record in records) {
            amountConsumed += record.amountConsumed.replace(Regex("\\D"), "").toInt()
        }
        binding.consumedwaterAmt.text = "$amountConsumed ML"

        // Update the progress bar
        updateProgressBar(amountConsumed)

        // Update the visibility of the "Drink some water" text
        updateDrinkSomeWaterTextVisibility()

    }

    private fun updateProgressBar(amountConsumed: Int) {

        dailyTarget = binding.waterDailytargetBtn.text.toString().replace(Regex("\\D"), "").toInt()
        updatedProgress = (((amountConsumed.toDouble() / dailyTarget)) * 100).toInt()

        binding.watertrackerCpb.progress = updatedProgress

        //Display congratulations msg to user when user hits the daily target
        if (!isTargetReached && (amountConsumed >= dailyTarget)) {
            isTargetReached = true
            Toast.makeText(
                context,
                "Congratulations! You have reached your daily water intake target.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateDrinkSomeWaterTextVisibility() {
        view?.findViewById<TextView>(R.id.drink_some_water)?.apply {
            visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    //Set Daily Target & get daily target
    override fun setDailyTarget(newDailyTarget: Int) {
        dailyTarget = newDailyTarget
        binding.waterDailytargetBtn.text = "Daily Target: ${dailyTarget.toString()}ml"

        //Show toast after set daily target
        Toast.makeText(
            context, "Daily target set to ${dailyTarget.toString()}ml", Toast.LENGTH_SHORT
        ).show()
        updateWaterConsumptionUI()
    }

    override fun getCurrentDailyTarget(): Int {
        // Retrieve the current daily target from your application or class
        return dailyTarget
    }

    //OnClick Listeners
    override fun onClick(v: View?) {
        when (v) {
            binding.addwaterBtn -> {
                val showAddWaterPopUp = AddWaterFragment()
                showAddWaterPopUp.onWaterAmountAddedListener = this
                showAddWaterPopUp.show(
                    (activity as AppCompatActivity).supportFragmentManager, "showAddWaterPopUp"
                )
            }

            binding.waterDailytargetBtn -> {
                val editWaterDailyTarget = EditWaterDailyTargetFragment()
                editWaterDailyTarget.setDailyTargetListener = this
                editWaterDailyTarget.show(
                    (activity as AppCompatActivity).supportFragmentManager, "editWaterDailyTarget"
                )
            }

            binding.editReminderBtn -> {
                val reminderIntent = Intent(activity, WaterReminderSettingsActivity::class.java)
                startActivity(reminderIntent)
            }

            binding.historyBtn -> {
                val historyIntent = Intent(activity, WaterHistoryActivity::class.java)
                startActivity(historyIntent)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        // Update the water tracker UI
        updateWaterConsumptionUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}