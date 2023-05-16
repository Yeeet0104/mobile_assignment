package com.example.mobile_assignment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

interface SetDailyCaloriesTargetListener {
    fun setDailyCaloriesTarget(newTarget : Int)
    fun getCurrentDailyTarget(): Int
}

class EditCaloriesFragment : DialogFragment() {

    var setDailyTargetListener : SetDailyCaloriesTargetListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_calories_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmBtn: Button = view.findViewById(R.id.con_btn)
        val closeBtn: ImageButton = view.findViewById(R.id.close_editcalories_btn)
        val editCalories: EditText = view.findViewById(R.id.editCaloriesAmount)



        confirmBtn.setOnClickListener {
            val caloriesAmount = editCalories.text.toString()

            if (caloriesAmount.isEmpty()) {
                Toast.makeText(context, "Please fill in the amount you want!", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    // Convert caloriesAmount to integer
                    val calories = caloriesAmount.toInt()

                    // Return the calories amount to the fragment
                    setDailyTargetListener?.setDailyCaloriesTarget(calories)

                    dismiss()
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Invalid number format!", Toast.LENGTH_SHORT).show()
                }
            }

        }

        closeBtn.setOnClickListener {
            dismiss()
        }

    }


}


