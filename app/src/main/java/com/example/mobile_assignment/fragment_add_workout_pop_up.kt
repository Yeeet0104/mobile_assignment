package com.example.mobile_assignment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController

class fragment_add_workout_pop_up : DialogFragment() {

    interface DataListener {
        fun onDataReceived(data: String)
    }
    var dataListener: DataListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_workout_pop_up, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //add water pop up modal close button
        val closeButton = view.findViewById<ImageButton>(R.id.close_workout_pop_up)
        closeButton.setOnClickListener {
            dismiss()
        }

        val editTextAddBtn = view.findViewById<Button>(R.id.addRoutineName_btn)
        editTextAddBtn.setOnClickListener{
            val planName = view.findViewById<EditText>(R.id.RoutineNameText).text.toString()
            val dataToSend = "Hello, Fragment!"
            dataListener?.onDataReceived(planName)
            dismiss()
        }

    }


}