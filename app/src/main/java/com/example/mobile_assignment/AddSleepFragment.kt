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

class AddSleepFragment : DialogFragment() {
    interface OnSleepAmountAddedListener {
        fun onSleepAmountAdded(amount: Int)
    }

    var onSleepAmountAddedListener: OnSleepAmountAddedListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_sleep_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //add sleep pop up modal close button
        //add sleep pop up modal close button
        val closeButton = view.findViewById<ImageButton>(R.id.close_addsleep_btn)
        closeButton.setOnClickListener {
            dismiss()
        }
        //change pop up modal amount to add
        val addSleepAmount = view.findViewById<TextView>(R.id.addsleep_amt)
        val sleepAmtBtnIds = listOf(
            R.id.addsleep_1,
            R.id.addsleep_2,
            R.id.addsleep_3,
            R.id.addsleep_4,
            R.id.addsleep_5,
            R.id.addsleep_6,
            R.id.addsleep_7,
            R.id.addsleep_8
        )
        val sleepPurple = ContextCompat.getColor(requireContext(), R.color.sleepPurple)
        var selectedButton: Button? = null

        val defaultSelectedButton = view.findViewById<Button>(R.id.addsleep_1)
        defaultSelectedButton.apply {
            setBackgroundColor(sleepPurple)
            setTextColor(Color.WHITE)
        }
        addSleepAmount.text = defaultSelectedButton.text.substring(1) + "hr"
        selectedButton = defaultSelectedButton

        sleepAmtBtnIds.forEach { buttonId ->
            val sleepAmtBtn = view.findViewById<Button>(buttonId)
            sleepAmtBtn.setOnClickListener {
                selectedButton?.apply {
                    setBackgroundColor(Color.WHITE)
                    setTextColor(Color.BLACK)
                }
                sleepAmtBtn.apply {
                    setBackgroundColor(sleepPurple)
                    setTextColor(Color.WHITE)
                }
                addSleepAmount.text = sleepAmtBtn.text.substring(1) + "hr"
                selectedButton = sleepAmtBtn
            }
        }

        val addAmtBtn = view.findViewById<Button>(R.id.addsleepAmount_btn)
        addAmtBtn.setOnClickListener {
            val amount = addSleepAmount.text.toString().removeSuffix("hr").toInt()
            onSleepAmountAddedListener?.onSleepAmountAdded(amount)
            dismiss()
        }

    }

}