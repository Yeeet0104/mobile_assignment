package com.example.mobile_assignment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream
import java.util.*


class add_custom_exercise: AppCompatActivity() , AdapterView.OnItemSelectedListener ,fragment_add_duration_exercise_pop_up.DataListener{

    private lateinit var newexercise: ExerciseData
    var sImage:String? = ""
    var durationType = 0 // 0 indicate that it is base on reps
    private lateinit var dbRef: DatabaseReference
    private lateinit var userExerciseDbRef: DatabaseReference
    private lateinit var dataToBeEdit : ExerciseData
    private var positionToBeEdit = 0
    private var isSettings = false
    private lateinit var spinner: Spinner


    private lateinit var btn : Button
    private lateinit var newExeName : EditText
    private lateinit var newExerTargetBodyPart : EditText
    private lateinit var newDuration : EditText
    private lateinit var inserImageBtn : Button
    private lateinit var imageView : ImageView
    private lateinit var deleteBtn : Button

    private var getPlanNameKey = ""
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_custom_exercise)

        dbRef = FirebaseDatabase.getInstance().getReference("ExerciseList")
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("checkUser",currentUser!!.uid.toString())

        isSettings = intent.getBooleanExtra("isSettings",false)
        Log.d("isSetting",isSettings.toString())
        getPlanNameKey = intent.getStringExtra("planNameKey")!!

        userExerciseDbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid).child("workoutPlans").child(getPlanNameKey).child("userExerciseList")
        if(isSettings){
            dataToBeEdit = intent.getSerializableExtra("dataToBeEdit") as ExerciseData
            positionToBeEdit = intent.getIntExtra("position",0)
        }
        var toolbar = findViewById<Toolbar>(R.id.toolbarHeader)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        spinner = findViewById(R.id.spinner)

        spinner.onItemSelectedListener = this

        spinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.arrForCustomWorkOut,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        init()
    }

    private fun init(){
        btn = findViewById(R.id.sendResultBackToExercise)
        newExeName = findViewById(R.id.newExerName)
        newExerTargetBodyPart = findViewById(R.id.newExerTargetBodyPart)
        newDuration = findViewById(R.id.durationExe)
        inserImageBtn = findViewById(R.id.pickImgBtn)
        deleteBtn = findViewById(R.id.deleteExerciseFromDb)
        imageView = findViewById(R.id.pickImage)
        inserImageBtn.setOnClickListener {
            inset_Img()
        }
        if(isSettings){
            deleteBtn.visibility = View.VISIBLE
            spinner.setSelection(0)
            val bytes = android.util.Base64.decode(dataToBeEdit.workoutPicture, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            newExeName.setText(dataToBeEdit.workoutName.toString())
            newExerTargetBodyPart.setText(dataToBeEdit.workoutDec.toString())
            var value = dataToBeEdit.workoutReps.toString()

            if(dataToBeEdit.workoutReps!!.lowercase(Locale.ROOT).contains(":")){
                spinner.setSelection(1)
            }
            imageView.setImageBitmap(bitmap)
            newDuration.setText(value)
            btn.text = "Update"
            btn.setOnClickListener {
                comfirmDialog(false)
            }
            deleteBtn.setOnClickListener {
                comfirmDialog(true)
            }

        }else{
            deleteBtn.visibility = View.GONE
            btn.setOnClickListener {
                if(sImage == null){
                    newexercise = ExerciseData(newExeName.text.toString(),newExerTargetBodyPart.text.toString(),newDuration.text.toString(),dataToBeEdit.workoutPicture,0,durationType)
                }else{
                    newexercise = ExerciseData(newExeName.text.toString(),newExerTargetBodyPart.text.toString(),newDuration.text.toString(),sImage,0,durationType)
                }
                if (newExeName.text.toString() !== "" && newExerTargetBodyPart.text.toString() !=="" && newDuration.text.toString() !== ""){
                    val intent = Intent()
                    intent.putExtra("customExeValue", newexercise)
                    setResult(RESULT_OK, intent)
                    finish()
                }else{
                    Toast.makeText(this,"Please fill in ll the blanks!",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val typeOfDuration = findViewById<EditText>(R.id.durationExe)
        if(!isSettings){
            typeOfDuration.hint = p0?.getItemAtPosition(p2).toString()
        }
        if(p0?.getItemAtPosition(p2).toString() != "Reps"){
            closeKeyboard()
            typeOfDuration.isFocusableInTouchMode = false
            if (!typeOfDuration.isFocusableInTouchMode){

                typeOfDuration.setOnClickListener{
                    getValudFromNumberPicker(typeOfDuration)
                }
            }
            durationType = 1
        }else{
            if(!isSettings){
                typeOfDuration.setOnClickListener(null)
                typeOfDuration.setText("")
                typeOfDuration.isEnabled = true
                typeOfDuration.isFocusableInTouchMode = true
                durationType = 0

            }
        }

//        Toast.makeText(this, "Type Selected is ${p0?.getItemAtPosition(p2).toString()}", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun inset_Img(){
        var myfileIntent = Intent(Intent.ACTION_GET_CONTENT)
        myfileIntent.setType("image/*")
        ActivityResultLauncher.launch(myfileIntent)
    }


    private val ActivityResultLauncher = registerForActivityResult<Intent,ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){result:ActivityResult ->
        if (result.resultCode == RESULT_OK){
            val uri = result.data!!.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val myBitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
                val bytes = stream.toByteArray()
                sImage = Base64.encodeToString(bytes,Base64.DEFAULT)
                var imageView = findViewById<ImageView>(R.id.pickImage)
                imageView.setImageBitmap(myBitmap)
                inputStream!!.close()
                Toast.makeText(this,"Image Selected",Toast.LENGTH_SHORT).show()
            }catch (ex:Exception){
                Toast.makeText(this,ex.message.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDataReceived(data: String,view: EditText,oneNumber : Boolean) {
        view.setText(data)
    }
    private fun getValudFromNumberPicker(view: EditText){
        val dialogFragment = fragment_add_duration_exercise_pop_up()
        dialogFragment.dataListener = this
        dialogFragment.editTextId = view
        dialogFragment.oneNumber = false
        dialogFragment.show(supportFragmentManager , "myDialog")
    }
    private fun closeKeyboard(){
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    private fun updateExerciseDB(exercise:ExerciseData,key : String){
        Log.d("checkIfExist",key)
        userExerciseDbRef.child(key).setValue(exercise)

    }

//    private fun updateChanges(original:ExerciseData,updated :ExerciseData){
//        dbRef.orderByChild("workoutName").equalTo(original.workoutName.toString()).addListenerForSingleValueEvent(object :
//            ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.children.forEach {
//                    Log.d("TESTUPDATEKEY",it.key.toString())
//                    updateExerciseDB(updated,it.key.toString())
//
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//    }

    private fun getKeyThenProceed(original:ExerciseData,updated :ExerciseData,isDelete: Boolean){
        Log.d("TESTUPDATEKEY",original.workoutName!!)
        Log.d("TESTUPDATEKEY",updated.workoutName!!)
        dbRef.orderByChild("workoutName").equalTo(original.workoutName).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    Log.d("TESTUPDATEKEY",it.key.toString())
                    Log.d("TESTUPDATEKEY",it.value.toString())

                    if(!isDelete){
                        Log.d("finding the right keys",it.key.toString())
                        dbRef.child(it.key.toString()).setValue(updated)
                        updateExerciseDB(updated,it.key.toString())
                    }else{
                        deleteExercise(it.key.toString())
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun deleteExercise(key:String) {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    if (it.key == key){
                        dbRef.child(it.key.toString()).removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        userExerciseDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    if (it.key == key){
                        userExerciseDbRef.child(it.key.toString()).removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun comfirmDialog(isDelete:Boolean) {
        val builder  = AlertDialog.Builder(this)
        if(!isDelete){
            builder.setTitle("Update Exercise Info")
            builder.setMessage("Are You Sure?")
        }else{
            builder.setTitle("Delete Exercise Info")
            builder.setMessage("Are You Sure? There is no undo once deleted!")
        }

        builder.setPositiveButton("Confirm") { dialog, which ->
            if(!isDelete){
                if(sImage == null){
                    newexercise = ExerciseData(newExeName.text.toString(),newExerTargetBodyPart.text.toString(),newDuration.text.toString(),dataToBeEdit.workoutPicture,0,durationType)
                }else{
                    newexercise = ExerciseData(newExeName.text.toString(),newExerTargetBodyPart.text.toString(),newDuration.text.toString(),sImage,0,durationType)
                }
                if (newExeName.text.toString() !== "" && newExerTargetBodyPart.text.toString() !=="" && newDuration.text.toString() !== ""){
                    getKeyThenProceed(dataToBeEdit,newexercise,false)
                    val intent = Intent()
                    intent.putExtra("customExeValue", newexercise)
                    intent.putExtra("isSettings", isSettings)
                    setResult(RESULT_OK, intent)
                    finish()
                }else{
                    Toast.makeText(this,"Please fill in ll the blanks!",Toast.LENGTH_LONG).show()
                }
            }else{
                newexercise = ExerciseData(newExeName.text.toString(),newExerTargetBodyPart.text.toString(),newDuration.text.toString(),sImage,0,durationType)
                getKeyThenProceed(dataToBeEdit,newexercise,true)
                val intent = Intent()
                intent.putExtra("customExeValue", newexercise)
                intent.putExtra("isDeleted", true)
                intent.putExtra("isSettings", isSettings)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            init()
        }
        builder.show()
    }
    override fun onBackPressed() {
        finish()
    }

}