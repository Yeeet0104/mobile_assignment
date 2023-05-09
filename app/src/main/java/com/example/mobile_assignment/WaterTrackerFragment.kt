package com.example.mobile_assignment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mobile_assignment.databinding.FragmentWaterTrackerBinding

class WaterTrackerFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentWaterTrackerBinding? = null
    private val binding get() = _binding!!

    //temp value: 60
    private var progressBar = 20

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWaterTrackerBinding.inflate(inflater, container, false)
        val view = binding.root

//        binding.btnDecr.setOnClickListener(this)
//        binding.btnIncr.setOnClickListener(this)

        binding.addwaterBtn.setOnClickListener(this)
        binding.waterDailytargetBtn.setOnClickListener(this)
        binding.editReminderBtn.setOnClickListener(this)
        binding.historyBtn.setOnClickListener(this)
        updateProgressBar()
        return view
    }

    override fun onClick(v:View?){
        when(v) {
//            binding.btnDecr -> {
//                if (progressBar >=10){
//                    progressBar -= 10
//                    updateProgressBar()
//                }
//            }
//            binding.btnIncr -> {
//                if (progressBar <=90){
//                    progressBar += 10
//                    updateProgressBar()
//                }
//            }
            binding.addwaterBtn -> {
                val showAddWaterPopUp = AddWaterFragment()
                showAddWaterPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showAddWaterPopUp")
            }

            binding.waterDailytargetBtn -> {
                val editWaterDailyTarget = EditWaterDailyTargetFragment()
                editWaterDailyTarget.show((activity as AppCompatActivity).supportFragmentManager, "editWaterDailyTarget")
            }

            binding.editReminderBtn -> {
                val reminderIntent = Intent(activity, WaterReminderSettingsActivity::class.java)
                startActivity(reminderIntent)
            }

            binding.historyBtn -> {
                val historyIntent = Intent(activity, WaterHistoryActivity::class.java)
                startActivity(historyIntent)
            }

        }
    }
    private fun updateProgressBar(){
        binding.watertrackerCpb.progress = progressBar
        binding.watertrackerTextprogress.text = progressBar.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}