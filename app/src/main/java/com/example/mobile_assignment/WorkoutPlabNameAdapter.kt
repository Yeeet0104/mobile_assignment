package com.example.mobile_assignment

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class WorkoutPlabNameAdapter(val c: Context, val workoutPlanNameList:ArrayList<WorkoutPlanName>,var fragemnt :WorkoutFragment):
    RecyclerView.Adapter<WorkoutPlabNameAdapter.viewHolder>(){
    inner class viewHolder(val view: View):RecyclerView.ViewHolder(view){
        var name: TextView
        var mbNum: TextView
        var addWorkoutBtn :ImageView
        var startWorkout :ImageView
        var settings : ImageView
        init {
            name = view.findViewById<TextView>(R.id.mTitle)
            mbNum = view.findViewById<TextView>(R.id.mSubTitle)
            addWorkoutBtn = view.findViewById<ImageView>(R.id.addworkout_btn)
            startWorkout = view.findViewById(R.id.playWorkout_btn)
            settings = view.findViewById(R.id.settings_workout_btn)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.workout_plan_name_card,parent,false)
        return viewHolder(v)
    }

    override fun getItemCount(): Int {
        return  workoutPlanNameList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val newList = workoutPlanNameList[position]
        holder.name.text = newList.workoutPlanName
        holder.mbNum.text = newList.progress
        Log.d("wtf",newList.progress)
        holder.addWorkoutBtn.setOnClickListener{
            fragemnt.navigateToAddWorkout(false,position)
        }
        holder.startWorkout.setOnClickListener{
            fragemnt.navigateToStartWorkout(position)
        }
        holder.settings.setOnClickListener{
            fragemnt.navigateToAddedActivity(holder.name.text.toString(),position,true)
        }
    }

}