package com.example.mobile_assignment.workout
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.mobile_assignment.R

class fragment_add_duration_exercise_pop_up : DialogFragment() {

    interface DataListener {
        fun onDataReceived(data: String,view: EditText,oneNumber:Boolean)
    }
    var dataListener: DataListener? = null
    var editTextId : EditText? = null
    var oneNumber = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_duration_exercise_pop_up, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //add water pop up modal close button
        val closeButton = view.findViewById<ImageButton>(R.id.close_workout_pop_up)
        closeButton.setOnClickListener {
            dismiss()
        }
        val mint = view.findViewById<NumberPicker>(R.id.exeDurationMin)
        if(!oneNumber){
            mint.visibility = View.VISIBLE
            mint.maxValue = 10
            mint.minValue = 0
            mint.value = 0
        }else{
            mint.visibility = View.GONE
        }
        val sec = view.findViewById<NumberPicker>(R.id.exeDurationSec)
        sec.maxValue = 12
        sec.minValue = 1
        sec.value = 1
        sec.setFormatter { value ->
            (value * 5).toString()
        }
        val submit = view.findViewById<Button>(R.id.addRoutineName_btn)
        submit.setOnClickListener{
            var text = ""
            var second = sec.value * 5
            if(!oneNumber){
                text = mint.value.toString() + ":" + second.toString()
            }else{
                text = second.toString()
            }

            dataListener?.onDataReceived(text,editTextId!!,oneNumber)
            dismiss()
        }

    }


}