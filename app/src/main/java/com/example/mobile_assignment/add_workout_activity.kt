package com.example.mobile_assignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.io.Serializable
import java.util.*

class add_workout_activity: AppCompatActivity() {

    private lateinit var recyclerView:RecyclerView
    private lateinit var exerciseList:ArrayList<ExerciseData>
    private lateinit var userAdapter:ExercisesDataAdapter
    private lateinit var searchExeriseView : SearchView
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_workout_activity)

        exerciseList = ArrayList()
        dbRef = FirebaseDatabase.getInstance().getReference("ExerciseList")

        var toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
//        recyclerView = findViewById<RecyclerView>(R.id.recyclerAddWorkout)
//        exerciseList = ArrayList()
////        userAdapter = ExercisesDataAdapter(this,exerciseList)
//        recyclerView.layoutManager = LinearLayoutManager(this)
        initForRecycleView()
//        recyclerView.adapter = userAdapter
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
            val intent = Intent (this, add_custom_exercise::class.java)
            getDataFromCustomExe.launch(intent)
        }


    }
    @Suppress("DEPRECATION")
    private val getDataFromCustomExe = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == RESULT_OK){
            val data:Intent? = result.data
            val value = data?.getSerializableExtra("customExeValue")
            val hashValue = convertToHashMap(value!!)
            Toast.makeText(this,"${hashValue["workoutName"].toString()}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun initForRecycleView() {
        //before this need to check if theres  any db in the particular user !
        // then init all the projects



        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                exerciseList.clear()
                if(snapshot.exists()){
                    for (exeSnap in snapshot.children){
                        exerciseList.add(exeSnap.getValue(ExerciseData::class.java)!!)
                    }
                }
                recyclerView = findViewById<RecyclerView>(R.id.recyclerAddWorkout)
                recyclerView.layoutManager = LinearLayoutManager(application)
                userAdapter = ExercisesDataAdapter(application,exerciseList)
                recyclerView.adapter = userAdapter



            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


    private fun addinfo(){
        val exerciseID = dbRef.push().key!!
        var exercise = ExerciseData("woi1","woi","woi",R.drawable.profile,R.drawable.add_workout_icon)
        dbRef.child(exerciseID).setValue(exercise)
            .addOnCompleteListener {
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()

            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun filterList(query : String?){
        if (query != null) {
            val filteredList = ArrayList<ExerciseData>()
            for (i in exerciseList) {
                if (i.workoutName!!.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                userAdapter.setFilteredList(filteredList)
            }
        }
    }


}