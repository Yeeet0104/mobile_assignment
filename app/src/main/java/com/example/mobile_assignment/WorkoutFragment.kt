package com.example.mobile_assignment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import org.w3c.dom.Text
import java.io.Serializable

class WorkoutFragment : Fragment(), fragment_add_workout_pop_up.DataListener, SetDailyTargetListener {
    private lateinit var recv: RecyclerView
    private lateinit var userList: ArrayList<WorkoutPlanName>
    private lateinit var userAdapter: WorkoutPlabNameAdapter
    private var numberOfPlan = 0

    private lateinit var dbPlansRef: DatabaseReference
    private lateinit var dbUserPlanNow: DatabaseReference
    private lateinit var planNameList: ArrayList<String>
    private lateinit var everyPlanProgress: ArrayList<Int>
    private lateinit var restDurations: ArrayList<Int>
    private lateinit var everyPlanTotalProgress: ArrayList<Double>
    private lateinit var userExerciseDbRef: DatabaseReference
    private lateinit var userExerciseList: MutableList<ExerciseData>
    private lateinit var edit_daily_target_routine : ImageView
    private lateinit var workoutProgessBar : ProgressBar
    private lateinit var routine : TextView
    private var dailyTarget = 0
    private var numberOfRoutine = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_workout, container, false)
        val addWorkoutBtn = view.findViewById<ImageView>(R.id.addworkout_btn)
        var settings = view.findViewById<ImageView>(R.id.settingsPage)
        edit_daily_target_routine = view.findViewById(R.id.edit_daily_target_routine)
        workoutProgessBar = view.findViewById(R.id.workoutProgessBar)
        routine = view.findViewById(R.id.routine)
        dbPlansRef = FirebaseDatabase.getInstance().getReference("workoutPlans")
        //init all the lists
        userList = ArrayList()
        planNameList = ArrayList()
        everyPlanProgress = ArrayList()
        everyPlanTotalProgress = ArrayList()
        restDurations = ArrayList()

        init()

        recv = view.findViewById(R.id.mRecycler)
        userAdapter = WorkoutPlabNameAdapter(view.context, userList, this)
        recv.layoutManager = LinearLayoutManager(view.context)
        recv.adapter = userAdapter
        addWorkoutBtn.setOnClickListener {
            val dialogFragment = fragment_add_workout_pop_up()
            dialogFragment.dataListener = this
            dialogFragment.show(parentFragmentManager, "myDialog")
        }
        settings.setOnClickListener {
            navigateToAddWorkout(true, 0)
        }
        edit_daily_target_routine.setOnClickListener{
            editDailyTarget()
        }
        return view
    }

    private fun init() {
        dbPlansRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
            planNameList.clear()
            everyPlanProgress.clear()
            everyPlanTotalProgress.clear()
                restDurations.clear()
            userList.clear()
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        var checkForNotList = false
                        if(it.key.toString() == "dailyTarget"){
                            checkForNotList = true
                        }
                        var newPlanName = ""
                        var newDuration = ""
                        var progress = ""
                        it.children.forEach { values ->
                            when (values.key.toString()) {
                                "workoutPlanName" -> {
                                    newPlanName = values.value.toString()
                                    planNameList.add(newPlanName)
                                }
                                "progress" -> {
                                    progress = values.value.toString()
                                    var val1 = progress.split("/")
                                    everyPlanTotalProgress.add(parseFraction(progress))
                                }
                                "restDuration" -> {
                                    newDuration = values.value.toString()
                                    restDurations.add(newDuration.toInt())
                                }
                                else -> { // Note the block
                                    Log.d("ERROR IN INIT PLANNAME", "ERROR")
                                }
                            }
                        }
                        if(!checkForNotList){
                            userList.add(WorkoutPlanName(newPlanName, progress, newDuration))
                        }
                    }
                    routine.text = getString(R.string.Amount_routine_string,everyPlanTotalProgress.size)
                    checkForProgress()
                    userAdapter.notifyDataSetChanged()
                }
                if (userList.size > 0) {
                    var displayNoPlans = view?.findViewById<ConstraintLayout>(R.id.displayNoPlans)
                    displayNoPlans?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addInfo(view: View?, data: String) {
        val inflter = LayoutInflater.from(view?.context)
        val v = inflter.inflate(R.layout.fragment_add_workout_pop_up, null)
        planNameList.add(data)
        val description = "0/4"
        var displayNoPlans = view?.findViewById<ConstraintLayout>(R.id.displayNoPlans)
        displayNoPlans?.visibility = View.GONE
        numberOfPlan++
        userList.add(
            WorkoutPlanName(
                "${planNameList[planNameList.size - 1]}",
                "$description",
                "10"
            )
        )
        addIntoDb(view, data)
        userAdapter.notifyDataSetChanged()
    }

    private fun addIntoDb(view: View?, data: String) {
        val plansID = dbPlansRef.push().key!!
        val description = "0/4"

        dbPlansRef.child(plansID).setValue(
            WorkoutPlanName(
                "${planNameList[planNameList.size - 1]}",
                "$description",
                "10"
            )
        )
            .addOnCompleteListener {

                Toast.makeText(view?.context, "Data inserted successfully", Toast.LENGTH_LONG)
                    .show()

            }.addOnFailureListener { err ->
                Toast.makeText(view?.context, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setFragmentResultListener("myRequestKey") { _, bundle ->
            val receivedData = bundle.getString("myDataKey")
            // Handle the received data
        }
    }

    override fun onDataReceived(data: String) {
        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            // Update UI elements or perform UI-related operations here
            addInfo(view, data)

            val routine = view?.findViewById<TextView>(R.id.routine)
            routine?.text = getString(R.string.Amount_routine_string, numberOfPlan)
            Toast.makeText(context, data, Toast.LENGTH_LONG).show()
        }
    }
    fun navigateToAddedActivity(planName:String, position: Int) {
        val intent = Intent(context, activity_added_exercise::class.java)
        var planNameKey = ""
        userExerciseList = mutableListOf()
        userExerciseDbRef = FirebaseDatabase.getInstance().getReference("workoutPlans")
        userExerciseDbRef.orderByChild("workoutPlanName").equalTo(planName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach{
                        planNameKey = it.key.toString()
                        userExerciseDbRef.child(it.key.toString()).child("userExerciseList").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                if (snapshot2.exists()) {
                                    snapshot2.children.forEach{ value->
                                        userExerciseList.add(value.getValue(ExerciseData::class.java)!!)
                                        Log.d("checkName",value.value.toString())
                                    }
                                }
                                intent.putExtra("userExerciseList", userExerciseList as Serializable)
                                intent.putExtra("planNameKey", planNameKey)
                                startActivity(intent)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                        Log.d("checkName",it.value.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





//

    }
    private fun parseFraction(value: String): Double {
        val parts = value.split("/")
        val numerator = parts[0].toDouble()
        val denominator = parts[1].toDouble()
        return numerator / denominator
    }
    fun navigateToAddWorkout(isSettings: Boolean, position: Int) {
        val intent = Intent(view?.context, add_workout_activity::class.java)
        intent.putExtra("isSettings", isSettings)
        intent.putExtra("planNameKey",planNameList[position])!!

            dbPlansRef.orderByChild("workoutPlanName").equalTo(planNameList[position])
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach {
                                intent.putExtra("planNameKey", it.key.toString())
                            }
                            startActivity(intent)
                        } else {
                            Log.d("whyNoValue", snapshot.children.toString())
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
    }

    fun navigateToStartWorkout(position: Int) {
        var showOnce = false
        val intent = Intent(view?.context, start_workout::class.java)
        intent.putExtra("planName",planNameList[position])!!
        intent.putExtra("duration",restDurations[position])!!
        dbPlansRef.orderByChild("workoutPlanName").equalTo(planNameList[position])
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            dbPlansRef.child(it.key.toString()).orderByChild("userExerciseList")
                                .addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot2: DataSnapshot) {
                                        if (snapshot2.exists()) {
                                            if(snapshot2.value.toString().contains("userExerciseList")){
                                                Log.d("checkDb",snapshot2.toString())
                                                intent.putExtra(
                                                    "planNameKey",
                                                    it.key.toString()
                                                )
                                                startActivity(intent)
                                            }else {
                                                Toast.makeText(
                                                    context,
                                                    "There is no exercise added!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }


                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                        }
                    } else {
                        Log.d("whyNoValue", snapshot.children.toString())
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    private fun checkForProgress(){
        dbPlansRef.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    snapshot.children.forEach{
                        if(it.key.toString() == "dailyTarget"){
                            dailyTarget = it.value.toString().toInt()
                            setDailyTarget(dailyTarget,dailyTarget)
                        }
                    }
                    updateProgressBar()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun updateProgressBar(){
        workoutProgessBar.progress = 0
        var totalclosestValue  = 0.0
        for (i in 1..dailyTarget){
            var closestValue = everyPlanTotalProgress.fold(0.0) { closest, current ->
                val closestDifference = Math.abs(closest - 1.0)
                val currentDifference = Math.abs(current - 1.0)

                if (currentDifference < closestDifference) current else closest
            }
            everyPlanTotalProgress.remove(closestValue)
            totalclosestValue += closestValue
            Log.d("checkWhichCloser",totalclosestValue.toString())
        }

        workoutProgessBar.progress = ((totalclosestValue /dailyTarget)* 100 ).toInt()

    }

    private fun editDailyTarget(){
        val ediRoutineDailyTarget = EditRoutineDailyTarget()
        ediRoutineDailyTarget.setDailyTargetListener = this
        ediRoutineDailyTarget.show((activity as AppCompatActivity).supportFragmentManager, "editWaterDailyTarget")
    }
    override fun setDailyTarget(newDailyTarget: Int, maxDailyTarget: Int) {
        var daily_routine_target = view?.findViewById<TextView>(R.id.daily_routine_target)
        dailyTarget = newDailyTarget
        daily_routine_target?.text = getString(R.string.daily_routine_target,newDailyTarget)
    }

    override fun getCurrentDailyTarget(): Int {
        return dailyTarget
    }
    override fun getMaxDailyTarget() : Int {
        return 10
    }

}