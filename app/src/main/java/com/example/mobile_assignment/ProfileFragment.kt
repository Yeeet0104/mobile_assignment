package com.example.mobile_assignment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var logout : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View ? {
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        // Inflate the layout for this fragment
        logout = view.findViewById(R.id.logOut)
        logout.setOnClickListener {
            logOutUser()
        }

        return view
    }
    private fun logOutUser() {
        val auth = FirebaseAuth.getInstance()

        // Sign out the user
        auth.signOut()

        // Redirect to the login screen or perform any necessary actions
        var intent = Intent(context, splashScreen::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}