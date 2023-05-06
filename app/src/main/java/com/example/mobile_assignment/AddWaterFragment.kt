package com.example.mobile_assignment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

class AddWaterFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_water_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //add water pop up modal close button
        val closeButton = view.findViewById<ImageButton>(R.id.close_addwater_btn)
        closeButton.setOnClickListener {
            dismiss()
        }

        //change pop up modal amount to add
        val addWaterAmount = view.findViewById<TextView>(R.id.addwater_amt)
        val waterAmtBtnIds = listOf(R.id.addwater_150, R.id.addwater_200, R.id.addwater_250,
            R.id.addwater_300, R.id.addwater_350, R.id.addwater_400, R.id.addwater_450, R.id.addwater_500)
        val waterMediumBlue = ContextCompat.getColor(requireContext(), R.color.waterMediumBlue)
        var selectedButton: Button? = null

        waterAmtBtnIds.forEach { buttonId ->
            val waterAmtBtn = view.findViewById<Button>(buttonId)
            waterAmtBtn.setOnClickListener {
                selectedButton?.apply {
                    setBackgroundColor(Color.WHITE)
                    setTextColor(Color.BLACK)
                }
                waterAmtBtn.apply {
                    setBackgroundColor(waterMediumBlue)
                    setTextColor(Color.WHITE)
                }
                addWaterAmount.text = waterAmtBtn.text.substring(1) + "ml"
                selectedButton = waterAmtBtn
            }
        }

        //pass amount to watertrackerfragment to update the amount consumed
        //val addAmtBtn = view.findViewById<Button>(R.id.addwaterAmount_btn)

    }


}