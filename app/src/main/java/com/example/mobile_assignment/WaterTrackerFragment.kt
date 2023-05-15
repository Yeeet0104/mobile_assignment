@file:Suppress("DEPRECATION")

package com.example.mobile_assignment


import android.app.ProgressDialog
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Suppress("NAME_SHADOWING", "DEPRECATION")
class WaterTrackerFragment : Fragment(), View.OnClickListener, OnWaterAmountAddedListener,  SetDailyTargetListener{

    //binding
    private var _binding: FragmentWaterTrackerBinding? = null
    private val binding get() = _binding!!

    //temp value: 0
    private var updatedProgress = 0
    private var isTargetReached = false

    //data
    private var records = mutableListOf<WaterRecordData>()
    private  var dailyTarget = 0 //1600 in text
    @RequiresApi(Build.VERSION_CODES.O)
    private val zoneID = ZoneId.of("Asia/Kuala_Lumpur")

    // Define the RecyclerView and its adapter
    private lateinit var waterRecyclerView: RecyclerView
    private lateinit var waterRecordAdapter: WaterRecordAdapter

    //firebase
    private val waterRecordFirebase = WaterRecordFirebase()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaterTrackerBinding.inflate(inflater, container, false)
        val view = binding.root

        // Find the RecyclerView in the layout and set its layout manager (in reverse order)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        waterRecyclerView = view.findViewById(R.id.water_records_recycler_view)
        waterRecyclerView.layoutManager = linearLayoutManager

        // Create a list of records and set up the adapter
        val recordList = records
        waterRecordAdapter = WaterRecordAdapter(recordList, ::onRecordDeleted)
        waterRecyclerView.adapter = waterRecordAdapter

        // Retrieve data from Firebase
        loadWaterDataFromFirebase()
        loadDailyTargetFromFirebase()
        updateWaterConsumptionUI()

        //Set onClickListener
        binding.addwaterBtn.setOnClickListener(this)
        binding.waterDailytargetBtn.setOnClickListener(this)
        binding.editReminderBtn.setOnClickListener(this)
        binding.historyBtn.setOnClickListener(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loadWaterDataFromFirebase() {
        //Show loading when the data is loaded from firebase
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val waterRecordsRef = FirebaseDatabase.getInstance().getReference("waterRecords")

        // Attach a listener to the database reference
        waterRecordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val waterRecordsDB = mutableListOf<WaterRecordData>()
                val currentDate = LocalDate.now(zoneID).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                // Loop through the children of the snapshot and create a WaterRecordData object for each
                for (recordSnapshot in snapshot.children) {
                    val record = recordSnapshot.getValue(WaterRecordData::class.java)
                    record?.let {
                        // Filter the records by today's date
                        Log.d("Current Date", currentDate)
                        if (it.dayAdded == currentDate) {
                            waterRecordsDB.add(it)
                        }
                    }
                }

                // Update the adapter with the retrieved data
                waterRecordAdapter.updateRecords(waterRecordsDB)
                waterRecordAdapter.notifyDataSetChanged()

                // Assign the retrieved data to the global variable
                this@WaterTrackerFragment.records.clear()
                this@WaterTrackerFragment.records.addAll(waterRecordsDB)
                Log.d("WaterTrackerFragment", "Updated records: $waterRecordsDB")

                // Update the water tracker UI
                updateWaterConsumptionUI()

                //Dismiss the progress dialog after the data is loaded
                progressDialog.dismiss()

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                progressDialog.dismiss()
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadDailyTargetFromFirebase(){
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val dailyTargetRef = FirebaseDatabase.getInstance().reference.child("waterDailyTargets").child(currentDate)
        dailyTargetRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var dailyTargetDB = snapshot.getValue(WaterDailyTarget::class.java)

                if (dailyTargetDB != null) {
                    dailyTargetRef.setValue(dailyTargetDB.target)
                    binding.waterDailytargetBtn.text = "Daily Target: ${dailyTargetDB.target}ml"
                } else {
                    // Set default daily target if it doesn't exist in Firebase
                    var defaultDailyTarget = 1600
                    setDailyTarget(defaultDailyTarget)
                    dailyTargetRef.setValue(defaultDailyTarget)
                    binding.waterDailytargetBtn.text = "Daily Target: $defaultDailyTarget ml"
                }


            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    //Add Water
    override fun onWaterAmountAdded(amount: Int) {
        //Add Records
        val currentDateTime = LocalDateTime.now(zoneID)
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        val formattedTime = currentDateTime.format(timeFormatter)
        val dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDay = currentDateTime.format(dayFormatter)

        val amount = "$amount ml"
        val newWaterRecord = WaterRecordData(formattedDay, formattedTime, amount)
        records.add(newWaterRecord)
        waterRecordAdapter.notifyDataSetChanged()


        // Add data to firebase
        waterRecordFirebase.addWaterRecord(newWaterRecord)

        //Show toast after add water
        Toast.makeText(context, "$amount of water added.", Toast.LENGTH_SHORT).show()

        // Update the water tracker UI
        updateWaterConsumptionUI()
    }
    private fun onRecordDeleted(){
        // Update the water tracker UI
        updateWaterConsumptionUI()
    }
    private fun updateWaterConsumptionUI() {
        // Update the amount consumed
        var amountConsumed = 0
        for (record in records) {
            amountConsumed += record.amountConsumed
                .replace(Regex("\\D"), "")
                .toInt()
        }
        binding.consumedwaterAmt.text = "$amountConsumed ML"

        // Update the progress bar
        updateProgressBar(amountConsumed)

        // Update the visibility of the "Drink some water" text
        updateDrinkSomeWaterTextVisibility()

    }
    private fun updateProgressBar(amountConsumed: Int) {

        dailyTarget = binding.waterDailytargetBtn.text.toString()
            .replace(Regex("\\D"), "")
            .toInt()
        updatedProgress = (((amountConsumed.toDouble()/dailyTarget))*100).toInt()

        binding.watertrackerCpb.progress = updatedProgress

        //Display congratulations msg to user when user hits the daily target
        if (!isTargetReached && (amountConsumed >= dailyTarget)) {
            isTargetReached = true
            Toast.makeText(context, "Congratulations! You have reached your daily water intake target.", Toast.LENGTH_LONG).show()
        }
    }
    private fun updateDrinkSomeWaterTextVisibility() {
        view?.findViewById<TextView>(R.id.drink_some_water)?.apply {
            visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    //Set Daily Target & get daily target
    @RequiresApi(Build.VERSION_CODES.O)
    override fun setDailyTarget(newDailyTarget: Int){
        dailyTarget = newDailyTarget
        binding.waterDailytargetBtn.text = "Daily Target: ${dailyTarget.toString()}ml"

        // set new daily target to DB
        waterRecordFirebase.setDailyTarget(dailyTarget)

        //Show toast after set daily target
        Toast.makeText(context, "Daily target set to ${dailyTarget.toString()}ml", Toast.LENGTH_SHORT).show()
        updateWaterConsumptionUI()
    }
    override fun getCurrentDailyTarget(): Int {
        // Retrieve the current daily target from your application or class
        return dailyTarget
    }

    //OnClick Listeners
    override fun onClick(v:View?){
        when(v) {
            binding.addwaterBtn -> {
                val showAddWaterPopUp = AddWaterFragment()
                showAddWaterPopUp.onWaterAmountAddedListener = this
                showAddWaterPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showAddWaterPopUp")
            }

            binding.waterDailytargetBtn -> {
                val editWaterDailyTarget = EditWaterDailyTargetFragment()
                editWaterDailyTarget.setDailyTargetListener = this
                editWaterDailyTarget.show((activity as AppCompatActivity).supportFragmentManager, "editWaterDailyTarget")
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