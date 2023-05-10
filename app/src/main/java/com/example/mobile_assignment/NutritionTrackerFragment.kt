package com.example.mobile_assignment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton


/**
 * A simple [Fragment] subclass.
 * Use the [NutritionTrackerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NutritionTrackerFragment : Fragment() {

    private lateinit var navCaloriesBtn: ImageButton
    private lateinit var editCaloriesPop: ImageButton
    private lateinit var navHistoryBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_nutrition_tracker, container, false)

        //Navigation to add food into the data
        navCaloriesBtn = view.findViewById(R.id.add_calories_btn)

        navCaloriesBtn.setOnClickListener {
            val intent = Intent(activity, NutritionOptionActivity::class.java)
            startActivity(intent)
        }

        //Navigate into history fragment
        navHistoryBtn = view.findViewById(R.id.calories_history_btn)

        navHistoryBtn.setOnClickListener {
            val intent = Intent(activity, NutritionHistoryActivity::class.java)
            startActivity(intent)
        }

        //Clickable ImageButton to edit the daily calories amount
        editCaloriesPop = view.findViewById(R.id.edit_calories_btn)

        editCaloriesPop.setOnClickListener {
            val showPopUp = EditCaloriesFragment()
            showPopUp.show((activity as AppCompatActivity).supportFragmentManager,"showPopUp")
        }

        return view

    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NutritionTrackerFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}