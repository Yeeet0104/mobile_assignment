package com.example.mobile_assignment

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodRecordFirebase {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid.toString()
    private val foodDbRef: DatabaseReference = database.getReference("users").child(currentUser).child("NutritionTracker").child("FoodRecord")

    fun addFoodRecord(foodRecord: FoodHistoryModel) {
        val key = foodDbRef.push().key
        if (key != null) {
            foodDbRef.child(key).setValue(foodRecord)
        }
    }

    fun removeFoodRecord(foodRecord: FoodHistoryModel) {
        val query = foodDbRef.orderByChild("dateHistory").equalTo(foodRecord.dateHistory)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (recordSnapshot in snapshot.children) {
                    val record = recordSnapshot.getValue(FoodHistoryModel::class.java)
                    if (record != null && record.dateHistory == foodRecord.dateHistory && record.dailyCalories == foodRecord.dailyCalories) {
                        recordSnapshot.ref.removeValue()
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDailyCaloriesTarget(newDailyTarget: Int){
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val dailyTargetRef = database.getReference("users").child(currentUser).child("NutritionTracker").child("CaloriesTarget").child(currentDate)
        val dailyTarget = CaloriesDailyTarget(currentDate, newDailyTarget)
        dailyTargetRef.setValue(dailyTarget)
    }





}