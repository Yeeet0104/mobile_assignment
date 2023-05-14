package com.example.mobile_assignment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment



class ConfirmFoodFragment : DialogFragment() {

    //text view
    private lateinit var tvFoodName: TextView
    private lateinit var tvFoodCalories: TextView
    private lateinit var tvFoodTime: TextView
    private lateinit var closeBtn: ImageButton
    private lateinit var confirmBtn: ImageButton

    private var foodConfirmedListener: OnFoodConfirmedListener? = null


    companion object {
        const val FOOD_NAME = "foodName"
        const val FOOD_CALORIES = "foodCalories"
        const val FOOD_TIME = "foodTime"
    }

    interface OnFoodConfirmedListener {
        fun onFoodConfirmed(foodName: String, calories: String, time: String)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_confirm_food_pop_up, container, false)
        initView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValuesToViews()
    }

    private fun initView(view: View) {
        // Initialize your views here using the view parameter as the root view

        closeBtn = view.findViewById(R.id.close_confirmfood_btn)
        confirmBtn = view.findViewById(R.id.food_confirm_button)
        tvFoodName = view.findViewById(R.id.food_confirm_name)
        tvFoodCalories = view.findViewById(R.id.food_confirm_calories)
        tvFoodTime = view.findViewById(R.id.food_confirm_time)

        confirmBtn.setOnClickListener {
            sendDataToNutritionTrackerFragment()
        }

        closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun sendDataToNutritionTrackerFragment() {
        // Get the data that was passed to the dialog from the second activity
        val foodName = arguments?.getString(FOOD_NAME) !!
        val foodCalories = arguments?.getString(FOOD_CALORIES)!!
        val foodTime = arguments?.getString(FOOD_TIME)!!

        // Call the listener method to send the data to the other fragment
        foodConfirmedListener?.onFoodConfirmed(foodName, foodCalories, foodTime)

        //Dismiss the dialog
        dismiss()
    }



    private fun setValuesToViews() {
        // Get the arguments passed to the dialog fragment
        val foodName = arguments?.getString(FOOD_NAME)
        val foodCalories = arguments?.getString(FOOD_CALORIES)
        val foodTime = arguments?.getString(FOOD_TIME)

        // Set the values to the text views
        tvFoodName.text = foodName
        tvFoodCalories.text = foodCalories
        tvFoodTime.text = foodTime

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host fragment implements the callback interface
        try {
            foodConfirmedListener = context as OnFoodConfirmedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnFoodConfirmedListener")
        }
    }
}




