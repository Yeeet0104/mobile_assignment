package com.example.mobile_assignment.workout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.example.mobile_assignment.R

interface SetDailyRoutineTargetListener {
    fun setDailyTarget(newDailyTarget: Int,maxDailyTarget: Int)
    fun getCurrentDailyTarget(): Int
    fun getMaxDailyTarget(): Int
}

class EditRoutineDailyTarget : DialogFragment() {
    var setDailyTargetListener : SetDailyRoutineTargetListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_routine_daily_target, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentTarget = setDailyTargetListener?.getCurrentDailyTarget()
        val defaultIndex = currentTarget.toString()
        val maxTarget = setDailyTargetListener?.getMaxDailyTarget()
        Log.d("checkforCounter",maxTarget.toString())
        val targetAmtPicker = view.findViewById<NumberPicker>(R.id.targetAmtPicker)
        targetAmtPicker.minValue = 1
        targetAmtPicker.maxValue = maxTarget!!
        targetAmtPicker.value = 1
        targetAmtPicker.wrapSelectorWheel = false
        val changeTargetBtn = view.findViewById<Button>(R.id.changeTargetBtn)
        changeTargetBtn.setOnClickListener {
            setDailyTargetListener?.setDailyTarget(targetAmtPicker.value,maxTarget)
            dismiss()
        }

        //change daily target cancel button
        val cancelButton = view.findViewById<Button>(R.id.cancelBtn)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}