package com.example.mobile_assignment

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WaterRecordFirebase {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val waterRecordsRef: DatabaseReference = database.getReference("waterRecords")

    fun addWaterRecord(waterRecord: WaterRecordData) {
        val key = waterRecordsRef.push().key
        if (key != null) {
            waterRecordsRef.child(key).setValue(waterRecord)
        }
    }

    fun removeWaterRecord(waterRecord: WaterRecordData) {
        val query = waterRecordsRef.orderByChild("dayAdded").equalTo(waterRecord.dayAdded)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (recordSnapshot in snapshot.children) {
                    val record = recordSnapshot.getValue(WaterRecordData::class.java)
                    if (record != null && record.timeAdded == waterRecord.timeAdded && record.amountConsumed == waterRecord.amountConsumed) {
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
    fun setDailyTarget(newDailyTarget: Int){
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val dailyTargetRef = database.reference.child("waterDailyTargets").child(currentDate)
        val dailyTarget = WaterDailyTarget(currentDate, newDailyTarget)
        dailyTargetRef.setValue(dailyTarget)
    }





}