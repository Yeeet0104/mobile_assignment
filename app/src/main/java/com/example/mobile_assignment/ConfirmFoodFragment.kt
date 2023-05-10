package com.example.mobile_assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class ConfirmFoodFragment : DialogFragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_food_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeBtn: ImageButton = view.findViewById(R.id.close_confirmfood_btn)
        val confirmBtn: ImageButton = view.findViewById(R.id.food_confirm_button)
        val foodName: TextView = view.findViewById(R.id.food_confirm_name)
        val foodCalories: TextView = view.findViewById(R.id.food_confirm_calories)

        confirmBtn.setOnClickListener {
            //return the calories amount to nutrition tracker fragment


        }

        closeBtn.setOnClickListener {
            dismiss()
        }

    }


}


