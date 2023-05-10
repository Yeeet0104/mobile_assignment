package com.example.mobile_assignment

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils.substring
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

interface OnWaterAmountAddedListener {
    fun onWaterAmountAdded(amount: Int)
}

class AddWaterFragment : DialogFragment() {

    var onWaterAmountAddedListener: OnWaterAmountAddedListener? = null
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

        // Find the button with ID R.id.addwater_200 and set it as selected by default
        val defaultSelectedButton = view.findViewById<Button>(R.id.addwater_200)
        defaultSelectedButton.apply {
            setBackgroundColor(waterMediumBlue)
            setTextColor(Color.WHITE)
        }
        addWaterAmount.text = defaultSelectedButton.text.substring(1) + "ml"
        selectedButton = defaultSelectedButton

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

        //pass amount to water tracker fragment to update the amount consumed
        val addAmtBtn = view.findViewById<Button>(R.id.addwaterAmount_btn)
        addAmtBtn.setOnClickListener {
            val amount = addWaterAmount.text.toString().removeSuffix("ml").toInt()
            onWaterAmountAddedListener?.onWaterAmountAdded(amount)
            dismiss()
        }

    }


}