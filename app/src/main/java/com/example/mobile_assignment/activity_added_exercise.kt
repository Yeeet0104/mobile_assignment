package com.example.mobile_assignment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.gson.Gson

class activity_added_exercise : AppCompatActivity(),fragment_add_duration_exercise_pop_up.DataListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseList: MutableList<ExerciseData>
    private lateinit var userExerciseList: MutableList<ExerciseData>
    private lateinit var userAdapter: ExercisesDataAdapter
    private var getPlanNameKey = ""
    private lateinit var value: String
    private lateinit var userExerciseDbRef: DatabaseReference
    private lateinit var workoutPlanSettings: DatabaseReference
    private lateinit var restDuration : EditText
    private lateinit var totalSets : EditText
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_added_exercise)
        var toolbar = findViewById<Toolbar>(R.id.addedExerciseActivityHeader)
        setSupportActionBar(toolbar)
        val retrieverList = intent.getSerializableExtra("userExerciseList") as? MutableList<ExerciseData>

        restDuration = findViewById(R.id.restDuration)
        totalSets = findViewById(R.id.totalSets)

        getPlanNameKey = intent.getStringExtra("planNameKey")!!
        userExerciseDbRef = FirebaseDatabase.getInstance().getReference("workoutPlans").child(getPlanNameKey).child("userExerciseList")
        workoutPlanSettings = FirebaseDatabase.getInstance().getReference("workoutPlans").child(getPlanNameKey)


        userExerciseList = mutableListOf()
        for (test in retrieverList!!){
            userExerciseList.add(test)
        }

        val restDuration = findViewById<EditText>(R.id.restDuration)
        restDuration.isFocusableInTouchMode = false
        if (!restDuration.isFocusableInTouchMode){
            restDuration.setOnClickListener{
                getValudFromNumberPicker(restDuration)
            }
        }
        var doneBtn = findViewById<Button>(R.id.doneBtn)
        doneBtn.setOnClickListener {
            complete()
        }

        recyclerView = findViewById(R.id.recyclerUserWorkoutList)
        recyclerView.layoutManager = LinearLayoutManager(application)
        userAdapter = ExercisesDataAdapter(application, userExerciseList,true,false,null)
        recyclerView.adapter = userAdapter
        init()
        userAdapter.setOnClickListener(object :
            ExercisesDataAdapter.OnClickListener {
            override fun onClick(
                position: Int,
                model: ExerciseData,
                isDelete: Boolean,
                notExists: Boolean
            ) {
                if (userExerciseList.find { it.workoutName.toString() == model.workoutName.toString() } != null && isDelete) {
                    loadingForInsertData()
                    deleteInfo(model.workoutName.toString())
                    userExerciseList.remove(model)
                }
            }


        })

    }


    private fun init(){
        workoutPlanSettings.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    snapshot.children.forEach{
                        if (it.key.toString() == "progress"){
                            var totalset = it.value.toString().split("/")
                            totalSets.setText(totalset[1])
                        }else if (it.key.toString() == "restDuration"){
                            restDuration.setText(it.value.toString())
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    override fun onDataReceived(data: String,view: EditText,oneNumber:Boolean) {
        view.setText(data)
    }

    private fun getValudFromNumberPicker(view: EditText){
        val dialogFragment = fragment_add_duration_exercise_pop_up()
        dialogFragment.dataListener = this
        dialogFragment.editTextId = view
        dialogFragment.oneNumber = true
        dialogFragment.show(supportFragmentManager , "myDialog")
    }

    private fun deleteInfo(workoutName: String) {
        userExerciseDbRef.orderByChild("workoutName").equalTo(workoutName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    Log.d("checkName", datasnapshot.value.toString())
                    for (snaphot in datasnapshot.children) {
                        Log.d("checkName", snaphot.key.toString())
                        userExerciseDbRef.child(snaphot.key.toString()).removeValue()
                    }
                    userAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    @Suppress("DEPRECATION")
    fun loadingForInsertData() {
        val handler = Handler()
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading Data")
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        val runnable = Runnable {
            progressDialog.dismiss()
        }
        handler.postDelayed(runnable, 2500)
    }


    private fun complete(){
        workoutPlanSettings.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    snapshot.children.forEach{
                        Log.d("CHECKDBLA",it.key.toString())
                        if (it.key.toString() == "progress"){
                            var totalSetText = ""
                            if(!totalSets.text.contains("/")){
                                totalSetText = "0/" + totalSets.text.toString()
                            }else{
                                totalSetText = totalSets.text.toString()
                            }
                            workoutPlanSettings.child("progress").setValue(totalSetText)
                        }else if (it.key.toString() == "restDuration"){
                            workoutPlanSettings.child("restDuration").setValue(restDuration.text.toString())
                        }
                    }
                    val intent = Intent(application ,MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
