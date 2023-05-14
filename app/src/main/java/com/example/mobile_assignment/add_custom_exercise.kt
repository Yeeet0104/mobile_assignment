package com.example.mobile_assignment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mobile_assignment.databinding.FragmentWaterTrackerBinding
import java.util.ArrayList

class add_custom_exercise: AppCompatActivity() , AdapterView.OnItemSelectedListener {

    private lateinit var newexercise: ExerciseData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_custom_exercise)

        var toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        var spinner : Spinner = findViewById<Spinner>(R.id.spinner)

        spinner.onItemSelectedListener = this

        spinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.arrForCustomWorkOut,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }


        val btn = findViewById<Button>(R.id.sendResultBackToExercise)
        btn.setOnClickListener {

            val newExeName = findViewById<EditText>(R.id.newExerName).text.toString()
            val newExerTargetBodyPart = findViewById<EditText>(R.id.newExerTargetBodyPart).text.toString()
            val newDuration = findViewById<EditText>(R.id.durationExe).text.toString()
            newexercise = ExerciseData(newExeName,newExerTargetBodyPart,newDuration,1,1)

            if (newExeName !== "" && newExerTargetBodyPart!=="" && newDuration !== ""){
                val intent = Intent()
                intent.putExtra("customExeValue", newexercise)
                setResult(RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this,"Please fill in ll the blanks!",Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val typeOfDuration = findViewById<EditText>(R.id.durationExe)
        typeOfDuration.hint = p0?.getItemAtPosition(p2).toString()
//        Toast.makeText(this, "Type Selected is ${p0?.getItemAtPosition(p2).toString()}", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}