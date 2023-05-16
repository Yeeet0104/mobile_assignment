package com.example.mobile_assignment.workout

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mobile_assignment.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class start_workout : AppCompatActivity() {
    private lateinit var userExerciseList: MutableList<ExerciseData>
    private lateinit var userExerciseDbRef: DatabaseReference

    private lateinit var exerciseName: TextView
    private lateinit var exerciseLeft: TextView

    private lateinit var countdownProgressBar: ProgressBar
    private lateinit var timerText: TextView
    private lateinit var showWorkoutImage: ImageView
    private lateinit var showReps: TextView
    private lateinit var nextbtn : Button
    private lateinit var skip : Button

    private var exerCount = 0
    private var myCountDownTimer: CountDownTimer? = null
    private var totalExerciseCount = 0
    private var getPlanNameKey =""
    private var getPlanName =""
    private var restDuration = 10
    private var currentUser =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_workout)

        countdownProgressBar = findViewById(R.id.workoutProgessBar)
        timerText = findViewById(R.id.numberCountdown)
        showWorkoutImage = findViewById(R.id.showWorkoutImage)
        showReps = findViewById(R.id.showReps)
        exerciseName = findViewById(R.id.displayWorkoutName)
        exerciseLeft = findViewById(R.id.exerciseLeft)

        getPlanNameKey = intent.getStringExtra("planNameKey")!!
        restDuration = 10

        var toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        toolbar.title = getPlanName
        setSupportActionBar(toolbar)

        currentUser = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        userExerciseDbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("workoutPlans").child(getPlanNameKey)

        getUserExerciseData()
        checkForCompletedWorkout()
        setTime()

        nextbtn = findViewById(R.id.doneOneWorkout)
        nextbtn.setOnClickListener {
            myCountDownTimer?.cancel()
            clearDatas()
            exerCount++
            if(exerCount < userExerciseList.size ){
                rest()
            }else{
                completed()
            }
        }
        skip = findViewById<Button>(R.id.skipOneWorkout)
        skip.setOnClickListener {
            myCountDownTimer?.cancel()
            clearDatas()
            if(exerCount < userExerciseList.size ){
                nextExercise(exerCount,false)
            }else{
                completed()
            }
        }
    }


    private fun getUserExerciseData() {
        userExerciseDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    snapshot.children.forEach {
                        if(it.key.toString() == "restDuration"){
                            restDuration = it.value.toString().toInt()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



        userExerciseList = mutableListOf()
        userExerciseDbRef.child("userExerciseList").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        userExerciseList.add(snap.getValue(ExerciseData::class.java)!!)
                    }
                    totalExerciseCount = userExerciseList.size
                    nextExercise(0,false)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun setTime(){
        val calender = Calendar.getInstance().time
        val formatterDate = SimpleDateFormat("yyyy-MM-dd")
        val date = formatterDate.format(calender)
        val formatterTimer = SimpleDateFormat("yyyy-MM-dd")
        val time = formatterTimer.format(calender)
        FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("workoutPlans").child("lastWorkoutDate").setValue(date)
    }

    private fun startTimer(inputDuration: Int,isRest:Boolean) {
        var longDuration = inputDuration * 1000
        var maxInt = longDuration.toLong()

        timerText.visibility = View.VISIBLE
        countdownProgressBar.visibility = View.VISIBLE
        countdownProgressBar.max = longDuration

        myCountDownTimer = object : CountDownTimer(maxInt, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownProgressBar.progress = millisUntilFinished.toInt()
                timerText.text = getString(R.string.workoutTimer, (millisUntilFinished / 1000))
            }

            override fun onFinish() {
                timerText.text = "done!"
                countdownProgressBar.progress = 0
                if(isRest){
                    nextExercise(exerCount,false)
                }
            }
        }
        Log.d("WOITIME", inputDuration.toString())
        myCountDownTimer?.start()
    }



    private fun nextExercise(position: Int,isSkip:Boolean) {
        exerciseLeft.visibility = View.VISIBLE
        showWorkoutImage.visibility = View.VISIBLE
        nextbtn.visibility = View.VISIBLE
        skip.visibility = View.GONE
        if (totalExerciseCount > position && !isSkip) {
            updateExerciseLeft()
            var newList = userExerciseList[position]
            var bytes = android.util.Base64.decode(newList.workoutPicture, android.util.Base64.DEFAULT)
            var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            exerciseName.text = userExerciseList[position].workoutName
            if (!userExerciseList[position].workoutReps!!.lowercase(Locale.ROOT).contains(":")) {
                timerText.visibility = View.GONE
                countdownProgressBar.visibility = View.GONE
                showReps.visibility = View.VISIBLE
                showReps.text = userExerciseList[position].workoutReps + "Reps"

            } else {
                showReps.visibility = View.GONE
                var totalTime = 0
                val duration: List<String> = userExerciseList[position].workoutReps!!.split(":")
                if (duration[0].toInt() != 0) {
                    totalTime += (duration[0].toInt() * 60) + duration[1].toInt()
                } else {
                    totalTime = duration[1].toInt()
                }

                startTimer(totalTime,false)
                Log.d("WOITIME", totalTime.toString())
            }
            showWorkoutImage.setImageBitmap(bitmap)
        } else {
            finish()
        }


    }

    private fun clearDatas() {
        countdownProgressBar.progress = 0

    }

    private fun updateExerciseLeft(){
        exerciseLeft.text =  getString(R.string.workoutLeft,(exerCount+1),totalExerciseCount)
    }


    private fun rest(){
        exerciseLeft.visibility = View.GONE
        showWorkoutImage.visibility = View.GONE
        showReps.visibility = View.GONE
        exerciseName.text = "Rest !"
        nextbtn.visibility = View.GONE
        showReps.visibility = View.GONE
        skip.visibility = View.VISIBLE
        startTimer(restDuration,true)
    }

    private fun completed(){
        showReps.visibility = View.GONE
        showWorkoutImage.visibility = View.VISIBLE
        showWorkoutImage.setImageResource(R.drawable.added_icon)
        countdownProgressBar.visibility = View.GONE
        timerText.visibility = View.GONE
        exerciseName.text = "Workout Completed !"
        nextbtn.visibility = View.VISIBLE
        skip.visibility = View.GONE
        exerciseLeft.visibility = View.GONE


        userExerciseDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach{
                        if(it.key.toString() == "progress"){
                            var oriValues = it.value.toString().split("/")
                            var newProgress = oriValues[0].toInt()
                            var progress = newProgress + 1
                            var combinedString = progress.toString() + "/" + oriValues[1]
                            if(progress <= oriValues[1].toInt()){
                                userExerciseDbRef.child("progress").setValue(combinedString)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        nextbtn.setOnClickListener {
            finish()
        }
    }

    private fun checkForCompletedWorkout(){
        userExerciseDbRef.child("userExerciseList").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        userExerciseDbRef.child("userExerciseList").child(snap.key.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                snapshot2.children.forEach{
                                    Log.d("CHECK VALUES",it.value.toString())
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}