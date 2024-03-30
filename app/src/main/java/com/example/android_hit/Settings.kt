package com.example.android_hit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android_hit.utils.TokenManager
import com.example.android_hit.utils.UserManager
import kotlin.random.Random


class Settings : Fragment() {

    private lateinit var sharedPref : TokenManager
    private lateinit var logoutButton : Button
    private lateinit var randomize : Button
    private lateinit var emailTextView : TextView
    private lateinit var user: UserManager
    private val RANDOMIZE_ACTION = "com.example.android_hit.RANDOMIZE_ACTION"


    @SuppressLint("UseRequireInsteadOfGet", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        // Inflate the layout for this fragment
        sharedPref = this.context?.let { TokenManager(it) }!!
        user = this.context?.let { UserManager(it) }!!
        logoutButton = view.findViewById(R.id.logoutButton)
        emailTextView = view.findViewById(R.id.emailTextView)
        randomize = view.findViewById(R.id.randomizeTransactionButton)

        emailTextView.text = user.getEmail("EMAIL")

        logoutButton.setOnClickListener {
            showConfirmationDialog()
        }

        randomize.setOnClickListener {
            goToTransaction()
            Log.e("BROD","clicked randomize")
            val randomIntInRange = Random.nextInt(1000, 100000)
            val intent = Intent(RANDOMIZE_ACTION)
            intent.putExtra("hargaRandom",randomIntInRange.toString())
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)

        }

        return view
    }
    private fun showConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this.context)
        alertDialogBuilder.apply {
            setTitle("Confirmation")
            setMessage("Are you sure you want to logout?")
            setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                Log.e("SET","masuk sini 2")
                sharedPref.deleteToken()
                val serviceIntent = Intent(context, CheckJWTBackground::class.java)
                context.stopService(serviceIntent)
                goToStart()
                dialogInterface.dismiss()
            }
            setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            setCancelable(false)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun goToStart(){
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun goToTransaction(){
        val intent = Intent(activity, DetailTransactionActivity::class.java)
        startActivity(intent)
    }

}