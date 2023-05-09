package com.example.mobile_assignment

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment


class EditWaterDailyTargetFragment : DialogFragment() {

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
        val changeTargetBtn = view.findViewById<Button>(R.id.changeTargetBtn)
        changeTargetBtn.setOnClickListener {
            val actualValue = values[targetAmtPicker.value]
            Toast.makeText(requireContext(), actualValue, Toast.LENGTH_SHORT).show()
        }

        //change daily target cancel button
        val cancelButton = view.findViewById<Button>(R.id.cancelBtn)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

}