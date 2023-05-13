package com.example.mobile_assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment

interface SetDailyTargetListener {
    fun setDailyTarget(newDailyTarget: Int)
    fun getCurrentDailyTarget(): Int
}


class EditWaterDailyTargetFragment : DialogFragment() {
    var setDailyTargetListener : SetDailyTargetListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_water_daily_target, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val targetAmtPicker = view.findViewById<NumberPicker>(R.id.targetAmtPicker)
        val values = (1000..3000 step 25).map { it.toString() }.toTypedArray()
        targetAmtPicker.minValue = values.indexOf("1000")
        targetAmtPicker.maxValue = values.indexOf("3000")
        targetAmtPicker.displayedValues = values
        targetAmtPicker.wrapSelectorWheel = false

        // Set the default value of the NumberPicker to the current daily target
        val currentTarget = setDailyTargetListener?.getCurrentDailyTarget()
        val defaultIndex = values.indexOf(currentTarget.toString())
        targetAmtPicker.value = defaultIndex

        val changeTargetBtn = view.findViewById<Button>(R.id.changeTargetBtn)
        changeTargetBtn.setOnClickListener {
            val actualValue = (values[targetAmtPicker.value]).toInt()
            setDailyTargetListener?.setDailyTarget(actualValue)
            dismiss()
        }

        //cancel to change daily target button
        val cancelButton = view.findViewById<Button>(R.id.cancelBtn)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

}