package com.example.mobile_assignment.workout

import android.widget.ImageView
import java.io.Serializable
import java.util.HashMap

data class ExerciseData  (
    var workoutName:String? = null,
    var workoutDec:String? = null,
    var workoutReps:String? = null,
    var workoutPicture:String? = null,
    var status: Int? = null,
    var durationType :Int? = null
    ) : java.io.Serializable {
}

fun convertToHashMap(value: Serializable): HashMap<String, Any> {
    val hashMap = HashMap<String, Any>()

    // Assuming value is a serializable object with properties you want to include in the HashMap
    // Extract the relevant properties and add them to the HashMap
    if (value is ExerciseData) {
        hashMap["workoutName"] = value.workoutName!!
        hashMap["workoutDec"] = value.workoutDec!!
        hashMap["workoutReps"] = value.workoutReps!!
        hashMap["workoutPicture"] = value.workoutPicture!!
        hashMap["status"] = value.status!!
        hashMap["durationType"] = value.durationType!!
        // Add more properties as needed
    }

    return hashMap
}

