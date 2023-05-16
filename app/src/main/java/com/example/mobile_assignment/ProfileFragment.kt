package com.example.mobile_assignment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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

    private lateinit var logout: Button
    private lateinit var userdbRef: DatabaseReference
    private var currentUser = ""
    private lateinit var userEmail: TextView
    private lateinit var userName: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentUser = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        userdbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser)

        var view = inflater.inflate(R.layout.fragment_profile, container, false)

        init()

        userName = view.findViewById(R.id.UserName)
        userEmail = view.findViewById(R.id.userEmail)
        logout = view.findViewById(R.id.logOut)
        logout.setOnClickListener {
            logOutUser()
        }

        return view
    }
    @Suppress( "DEPRECATION")
    private fun init() {
        var progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        userdbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    snapshot.children.forEach {
                        when(it.key.toString()){
                            "email" -> {
                                userEmail.text = it.value.toString()
                            }
                            "userName" -> {
                                userName.text = it.value.toString()
                            }
                            else -> ""
                        }
                    }
                }
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private fun logOutUser() {
        val auth = FirebaseAuth.getInstance()

        // Sign out the user
        auth.signOut()

        // Redirect to the login screen or perform any necessary actions
        var intent = Intent(context, SplashScreen::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}