package com.example.mobile_assignment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class add_workout_activity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseList: MutableList<ExerciseData>
    private lateinit var userExerciseList: MutableList<ExerciseData>
    private lateinit var userAdapter: ExercisesDataAdapter
    private lateinit var searchExeriseView: SearchView
    private lateinit var dbRef: DatabaseReference
    private lateinit var userExerciseDbRef: DatabaseReference
    private lateinit var typeSelected: String
    private var isSettings = false
    private var getPlanNameKey = ""
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_workout_activity)

        var toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("checkUser",currentUser!!.uid.toString())


        isSettings = intent.getBooleanExtra("isSettings",false)
        Log.d("wtff",isSettings.toString())
        getPlanNameKey = intent.getStringExtra("planNameKey")!!

        dbRef = FirebaseDatabase.getInstance().getReference("ExerciseList")
        userExerciseDbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid).child("workoutPlans").child(getPlanNameKey).child("userExerciseList")

        var spinner: Spinner = findViewById<Spinner>(R.id.filterTypeExercise)

        spinner.onItemSelectedListener = this

        spinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.filterByExercise,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        initForRecycleView()
        setupRecyclerView()
        searchExeriseView = findViewById(R.id.searchViewForExerciseList)
        searchExeriseView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                filterList(p0)
                return true
            }

        })
        var addExercise = findViewById<Button>(R.id.addExercise)
        addExercise.setOnClickListener {
            navigateToAddCustom(false,null,null)
        }
        var next = findViewById<Button>(R.id.next)
        if(!isSettings){
            next.text = "NEXT"
            next.visibility = View.VISIBLE
            next.setOnClickListener {
                navigateToAddedActivity()
            }

        }else{
            next.text = "DONE"
            next.setOnClickListener {
                finish()
            }
        }
    }
    @Suppress("DEPRECATION")
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }
    @Suppress("DEPRECATION")
    private val getDataFromCustomExe =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val value = data?.getSerializableExtra("customExeValue")
                val isDeleted = data?.getBooleanExtra("isDeleted",false)
                val isSettings = data?.getBooleanExtra("isSettings",false)
                if(!isDeleted!!){
                    val hashValue = convertToHashMap(value!!)
                    var newExercise = ExerciseData(
                        hashValue["workoutName"].toString(),
                        hashValue["workoutDec"].toString(),
                        hashValue["workoutReps"].toString(),
                        hashValue["workoutPicture"].toString(),
                        Integer.parseInt(hashValue["status"].toString()),
                        Integer.parseInt(hashValue["durationType"].toString())
                    )
                    if(!isSettings!!){
                        addinfo(newExercise)
                    }
                }
            }
        }

    private fun initForRecycleView() {
        exerciseList = mutableListOf()
        userExerciseList = mutableListOf()
        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                exerciseList.clear()
                userExerciseList.clear()
                if (snapshot.exists()) {
                    for (exeSnap in snapshot.children) {
                        exerciseList.add(exeSnap.getValue(ExerciseData::class.java)!!)
                    }
                }
                userExerciseDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for ((index, exeSnap) in snapshot.children.withIndex()) {
//                                exerciseList.find { it == exeSnap.getValue(ExerciseData::class.java)!! }
//                                exerciseList.contains(exeSnap.getValue(ExerciseData::class.java)!!)
                                Log.d("checkIndex", index.toString())

                                for ((i, duplicate) in exerciseList.withIndex()) {
                                    if (duplicate.workoutName.toString() == exeSnap.getValue(
                                            ExerciseData::class.java
                                        )!!.workoutName.toString()
                                    ) {
                                        exerciseList[i].status = 1
                                        userExerciseList.add(exeSnap.getValue(ExerciseData::class.java)!!)
                                    }
                                }
                            }
                        }

                        userAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    @Suppress("DEPRECATION")
    private fun addinfo(exercise: ExerciseData) {
        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val exerciseID = dbRef.push().key!!
        dbRef.child(exerciseID).setValue(exercise)
            .addOnCompleteListener {
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()
                progressDialog.dismiss()
            }.addOnFailureListener { err ->
                progressDialog.dismiss()
                Log.d("err","Error ${err.message}")
            }
        Log.d("checking", userExerciseList.size.toString())
    }

    private fun addinfoToUser(exercise: ExerciseData) {
        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        dbRef.orderByChild("workoutName").equalTo(exercise.workoutName.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    userExerciseDbRef.child(it.key.toString()).setValue(exercise)
                        .addOnCompleteListener {
                            progressDialog.dismiss()
                        }.addOnFailureListener { err ->
                            Log.d("ERRORDB",err.toString())
                        }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
            }
        })


    }


    @Suppress("DEPRECATION")
    private fun deleteInfo(workoutName: String) {
        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        userExerciseDbRef.orderByChild("workoutName").equalTo(workoutName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    Log.d("checkName", datasnapshot.value.toString())
                    for (snaphot in datasnapshot.children) {
                        Log.d("checkName", snaphot.key.toString())
                        userExerciseDbRef.child(snaphot.key.toString()).removeValue()
                        progressDialog.dismiss()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<ExerciseData>()
            for (i in exerciseList) {
                Log.d("checkfor selected part",typeSelected.toString())
                if (typeSelected.lowercase(Locale.ROOT) != "body part") {
                    if (i.workoutName!!.lowercase(Locale.ROOT).contains(query)) {
                        filteredList.add(i)
                    }
                } else {
                    if (i.workoutDec!!.lowercase(Locale.ROOT).contains(query)) {
                        filteredList.add(i)
                        Log.d("checkfor selected part",i.workoutDec.toString())
                    }
                }
            }

            if (filteredList.isEmpty()) {
                userAdapter.notifyDataSetChanged()
            } else {
                userAdapter.setFilteredList(filteredList)
            }
        }
    }

    private fun setupRecyclerView() {
        if (exerciseList.size <= 0) {

            recyclerView = findViewById(R.id.recyclerAddWorkout)
            recyclerView.layoutManager = LinearLayoutManager(application)
            userAdapter = ExercisesDataAdapter(application, exerciseList, false, isSettings,this)
            recyclerView.adapter = userAdapter
            userAdapter.setOnClickListener(object :
                ExercisesDataAdapter.OnClickListener {
                override fun onClick(
                    position: Int,
                    model: ExerciseData,
                    isDelete: Boolean,
                    notExists: Boolean
                ) {
                    //convert the model into hashmap to make it easier to use
                    var exerciseHasMap = convertToHashMap(model)
                    if (userExerciseList.find { it.workoutName.toString() == model.workoutName.toString() } == null && !isDelete) {
                        userExerciseList.add(model)
                        addinfoToUser(model)
                    } else if (userExerciseList.find { it.workoutName.toString() == model.workoutName.toString() } != null && isDelete) {
                        userExerciseList.removeAll {
                            it.workoutName == model.workoutName.toString()
                        }
                        deleteInfo(exerciseHasMap["workoutName"].toString())
                    } else {
                        Toast.makeText(
                            application,
                            "${exerciseHasMap["workoutName"]} is already added to your list",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    checkUserList()
                }


            })
        }
    }




    private fun checkUserList() {
        for (check in userExerciseList) {
            Log.d("checkUserList", check.workoutName.toString())
        }

    }

    private fun navigateToAddedActivity() {
        val intent = Intent(this, activity_added_exercise::class.java)
        intent.putExtra("isSettings", false)
        if (userExerciseList.size <= 0) {
            Toast.makeText(this, "There is no exercise added!", Toast.LENGTH_SHORT).show()
        } else {
            intent.putExtra("userExerciseList", userExerciseList as Serializable)
            intent.putExtra("planNameKey", getPlanNameKey)
            startActivity(intent)
        }
    }
    fun navigateToAddCustom(isSettings:Boolean,newList :ExerciseData?,position : Int?) {
        val intent = Intent(this, add_custom_exercise::class.java)
        intent.putExtra("isSettings",isSettings)
        intent.putExtra("planNameKey", getPlanNameKey)
        if(isSettings){
            intent.putExtra("dataToBeEdit",newList)
            intent.putExtra("position",position)
        }

        getDataFromCustomExe.launch(intent)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        typeSelected = p0?.getItemAtPosition(p2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        typeSelected = "body part"
    }

    fun getDataFromAdapter(position : Int,newList :ExerciseData){
        navigateToAddCustom(isSettings,newList ,position)
    }
}