package com.example.mobile_assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment

interface SetSleepDailyTargetListener {
    fun setDailyTarget(newDailyTarget: Int)
    fun getCurrentDailyTarget(): Int
}

class EditSleepDailyTargetFragment : DialogFragment() {
    var setDailyTargetListener: SetDailyTargetListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_sleep_daily_target, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val targetSleepAmtPicker = view.findViewById<NumberPicker>(R.id.targetSleepAmtPicker)
        val values = (1..24).map { it.toString() }.toTypedArray()
        targetSleepAmtPicker.minValue = 0
        targetSleepAmtPicker.maxValue = values.size - 1
        targetSleepAmtPicker.displayedValues = values
        targetSleepAmtPicker.wrapSelectorWheel = false

        // Set the default value of the NumberPicker to the current daily target
        val currentTarget = setDailyTargetListener?.getCurrentDailyTarget()
        val defaultIndex = values.indexOf(currentTarget?.toString()) // Add null check
        targetSleepAmtPicker.value =
            defaultIndex.takeIf { it != -1 } ?: 0 // Use default value if not found

        val changeTargetBtn = view.findViewById<Button>(R.id.changeTargetBtn)
        changeTargetBtn.setOnClickListener {
            val actualValue = values[targetSleepAmtPicker.value].toInt()
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
