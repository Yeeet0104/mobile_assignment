package com.example.mobile_assignment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExercisesDataAdapter(val c: Context, var exerciseList:ArrayList<ExerciseData>) :
    RecyclerView.Adapter<ExercisesDataAdapter.viewHolder>(){
    private var onClickListener: OnClickListener? = null
    interface OnClickListener {
        fun onClick(position: Int, model: ExerciseData)
    }
    inner class viewHolder(val view: View):RecyclerView.ViewHolder(view){
        var workoutName:TextView
        var workoutDec:TextView
        var workoutReps:TextView
        var workoutPicture:ImageView
        var status: ImageView

        init {
            workoutName = view.findViewById<TextView>(R.id.exerciseName)
            workoutDec = view.findViewById<TextView>(R.id.exerciseDesc)
            workoutPicture = view.findViewById<ImageView>(R.id.exerciseImg)
            workoutReps = view.findViewById<TextView>(R.id.exerciseRep)
            status = view.findViewById<ImageView>(R.id.exerciseAddBtn)
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercisesDataAdapter.viewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.workout_item_list,parent,false)
        return viewHolder(v)
    }

    override fun getItemCount(): Int {
        return  exerciseList.size
    }

    override fun onBindViewHolder(holder: ExercisesDataAdapter.viewHolder, position: Int) {
        val newList = exerciseList[position]
        holder.workoutName.text = newList.workoutName
        holder.workoutDec.text = newList.workoutDec
        holder.workoutPicture.setImageResource(newList.workoutPicture!!)
        holder.workoutReps.text= newList.workoutReps
        holder.status.setImageResource(newList.status!!)
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, newList )
            }
        }
    }
    fun setFilteredList(exerciseList: ArrayList<ExerciseData>){
        this.exerciseList = exerciseList
        notifyDataSetChanged()
    }

}