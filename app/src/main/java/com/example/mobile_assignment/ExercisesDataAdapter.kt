package com.example.mobile_assignment


import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ExercisesDataAdapter(
    val c: Context,
    var exerciseList: MutableList<ExerciseData>,
    val isAddedExe: Boolean,
    var isSettings:Boolean,
    var forSettingData : add_workout_activity?
) :
    RecyclerView.Adapter<ExercisesDataAdapter.viewHolder>(){

    private var exerciseCount = 0

    private lateinit var onClickListener: OnClickListener

    interface OnClickListener {
        fun onClick(position: Int, model: ExerciseData, isDelete: Boolean, notExists: Boolean)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    inner class viewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var workoutName: TextView
        var workoutDec: TextView
        var workoutReps: TextView
        var workoutPicture: ImageView
        var status: Int
        var cardViewOfExerciseList: MaterialCardView
        var deleteBtn: ImageView
        var exerciseListBtn: ImageView
        var editBtn : ImageView
        init {
            workoutName = view.findViewById(R.id.exerciseName)
            workoutDec = view.findViewById(R.id.exerciseDesc)
            workoutPicture = view.findViewById(R.id.exerciseImg)
            workoutReps = view.findViewById(R.id.exerciseRep)
            status = 0
            cardViewOfExerciseList = view.findViewById(R.id.cardViewOfExerciseList)
            deleteBtn = view.findViewById(R.id.deleteExercisebtn)
            exerciseListBtn = view.findViewById(R.id.exerciseAddBtn)
            editBtn = view.findViewById(R.id.editExerciseBtn)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExercisesDataAdapter.viewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.workout_item_list, parent, false)
        return viewHolder(v)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }


    override fun onBindViewHolder(holder: ExercisesDataAdapter.viewHolder, position: Int) {
        val newList = exerciseList[position]
        val bytes = android.util.Base64.decode(newList.workoutPicture, android.util.Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        holder.workoutName.text = newList.workoutName
        holder.workoutDec.text = c.getString(R.string.targetBodyPart, newList.workoutDec)
        holder.workoutPicture.setImageBitmap(bitmap)
        holder.workoutReps.text = newList.workoutReps
        holder.deleteBtn.visibility = View.GONE
        holder.editBtn.visibility = View.GONE
        if (!isAddedExe && !isSettings) {
            if (newList.status!! == 1) {
                holder.status = 1
                holder.cardViewOfExerciseList.strokeColor = ContextCompat.getColor(c, R.color.green)
                holder.cardViewOfExerciseList.strokeWidth = 5
                holder.exerciseListBtn.setImageResource(R.drawable.added_icon)
                holder.deleteBtn.visibility = View.VISIBLE
                holder.deleteBtn.setOnClickListener {
                    onClickListener!!.onClick(position, newList, true, false)
                    holder.status = 0
                    holder.cardViewOfExerciseList.strokeWidth = 0
                    holder.deleteBtn.visibility = View.GONE
                    holder.exerciseListBtn.setImageResource(R.drawable.add_workout_icon)
                }
            } else {
                holder.status = 0
                holder.cardViewOfExerciseList.strokeWidth = 0
                holder.deleteBtn.visibility = View.GONE
                holder.exerciseListBtn.setImageResource(R.drawable.add_workout_icon)
            }
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, newList, false, true)
                    holder.status = 1
                    holder.cardViewOfExerciseList.strokeColor =
                        ContextCompat.getColor(c, R.color.green)
                    holder.cardViewOfExerciseList.strokeWidth = 5
                    holder.exerciseListBtn.setImageResource(R.drawable.added_icon)
                    holder.deleteBtn.visibility = View.VISIBLE
                    if (holder.deleteBtn.visibility == View.VISIBLE) {
                        holder.deleteBtn.setOnClickListener {
                            onClickListener!!.onClick(position, newList, true, false)
                            holder.status = 0
                            holder.cardViewOfExerciseList.strokeWidth = 0
                            holder.deleteBtn.visibility = View.GONE
                            holder.exerciseListBtn.setImageResource(R.drawable.add_workout_icon)
                        }

                    }
                }
            }
        }else if(isSettings){
            holder.editBtn.visibility = View.VISIBLE
            holder.deleteBtn.visibility = View.GONE
            holder.exerciseListBtn.visibility = View.GONE
            holder.editBtn.setOnClickListener{
                forSettingData!!.getDataFromAdapter(position,newList)
            }
        }else{
            holder.deleteBtn.visibility = View.VISIBLE
            holder.exerciseListBtn.visibility = View.GONE
            holder.deleteBtn.setOnClickListener{
                onClickListener!!.onClick(position, newList, true, false)
            }
        }

    }

    fun setFilteredList(exerciseList: MutableList<ExerciseData>) {
        this.exerciseList = exerciseList
        notifyDataSetChanged()
    }



}