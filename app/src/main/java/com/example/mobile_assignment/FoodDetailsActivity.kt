package com.example.mobile_assignment


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class FoodDetailsActivity : AppCompatActivity(){

    private lateinit var tvFoodId: TextView
    private lateinit var tvFoodName: TextView
    private lateinit var tvFoodCalories: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)

        initView()
        setValuesToViews()

        btnUpdate.setOnClickListener {
            openUpdateDialog(
                intent.getStringExtra("foodId").toString(),
                intent.getStringExtra("foodName").toString()
            )
        }

        btnDelete.setOnClickListener {
            deleteRecord(
                intent.getStringExtra("foodId").toString()
            )
        }
    }


    private fun openUpdateDialog(foodId: String, foodName: String) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_dialog, null)

        mDialog.setView(mDialogView)

        val etFoodName = mDialogView.findViewById<EditText>(R.id.etFoodName)
        val etFoodCalories = mDialogView.findViewById<EditText>(R.id.etFoodCalories)

        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        etFoodName.setText(intent.getStringExtra("foodName").toString())
        etFoodCalories.setText(intent.getStringExtra("foodCalories").toString())


        mDialog.setTitle("Updating $foodName Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            updateFoodData(
                foodId,
                etFoodName.text.toString(),
                etFoodCalories.text.toString(),
            )

            Toast.makeText(applicationContext, "Food Data Updated", Toast.LENGTH_LONG).show()

            //we are setting updated data to our textviews
            tvFoodName.text = etFoodName.text.toString()
            tvFoodCalories.text = etFoodCalories.text.toString()

            alertDialog.dismiss()
            finish()
        }
    }

    private fun updateFoodData(id: String, name: String, calories: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("FoodList").child(id)
        val foodInfo = FoodModel(id, name, calories)
        dbRef.setValue(foodInfo)
    }

    private fun deleteRecord(
        id: String
    ){
        val dbRef = FirebaseDatabase.getInstance().getReference("FoodList").child(id)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, "Food data deleted", Toast.LENGTH_LONG).show()

            val intent = Intent(this, UpdateFoodSearchActivity::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener{ error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initView() {
        tvFoodId = findViewById(R.id.tvFoodId)
        tvFoodName = findViewById(R.id.tvFoodName)
        tvFoodCalories = findViewById(R.id.tvFoodCalories)

        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
    }

    private fun setValuesToViews() {
        tvFoodId.text = intent.getStringExtra("foodId")
        tvFoodName.text = intent.getStringExtra("foodName")
        tvFoodCalories.text = intent.getStringExtra("foodCalories")

    }
}