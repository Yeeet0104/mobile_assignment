package com.example.mobile_assignment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class WorkoutFragment : Fragment(),fragment_add_workout_pop_up.DataListener{
    private lateinit var recv:RecyclerView
    private lateinit var userList:ArrayList<WorkoutPlanName>
    private lateinit var userAdapter:WorkoutPlabNameAdapter
    private var numberOfPlan = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_workout, container, false)
        val addWorkoutBtn = view.findViewById<ImageView>(R.id.addworkout_btn)

        userList = ArrayList()
        recv = view.findViewById(R.id.mRecycler)

        userAdapter = WorkoutPlabNameAdapter(view.context,userList,this)
        recv.layoutManager = LinearLayoutManager(view.context)
        recv.adapter = userAdapter
        addWorkoutBtn.setOnClickListener{
            val dialogFragment = fragment_add_workout_pop_up()
            dialogFragment.dataListener = this
            dialogFragment.show(parentFragmentManager, "myDialog")
        }

        return view
    }
    private fun addInfo(view: View?,data: String) {
        val inflter = LayoutInflater.from(view?.context)
        val v = inflter.inflate(R.layout.fragment_add_workout_pop_up,null)
        val planName = data
        val description = "0/4sets"

        numberOfPlan++
        userList.add(WorkoutPlanName("$planName","$description"))
        userAdapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setFragmentResultListener("myRequestKey") { _, bundle ->
            val receivedData = bundle.getString("myDataKey")
            // Handle the received data
        }
    }

    override fun onDataReceived(data: String) {
        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            // Update UI elements or perform UI-related operations here
            addInfo(view,data)
            val routine = view?.findViewById<TextView>(R.id.routine)
            routine?.text = getString(R.string.Amount_routine_string,numberOfPlan)
            Toast.makeText(context,data,Toast.LENGTH_LONG).show()
        }
    }
    fun navigateToAddWorkout(){
        val intent = Intent (view?.context, add_workout_activity::class.java)
        startActivity(intent)
    }

}