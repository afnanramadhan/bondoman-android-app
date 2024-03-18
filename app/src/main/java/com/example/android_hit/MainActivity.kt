package com.example.android_hit

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.android_hit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentFragment(Transaction(), HeaderTransaction())
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_transaction -> {
                    setCurrentFragment(Transaction(), HeaderTransaction())
                }
                R.id.nav_scan -> {
                    setCurrentFragment(Scan(),  HeaderScan())
                }
                R.id.nav_graphs -> {
                    setCurrentFragment(Graphs(), HeaderGraphs())
                }
                R.id.nav_settings -> {
                    setCurrentFragment(Settings(), HeaderSettings())
                }
            }
            true
        }

        //Keluarin pop up kalo token dah abis, sama matiin background service
        if (intent.getBooleanExtra("show_confirmation", false)) {
            val serviceIntent = Intent(this, CheckJWTBackground::class.java)
            this.stopService(serviceIntent)
            showConfirmationDialog()
        }

    }

    private fun setCurrentFragment(fragment: Fragment, header: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            replace(R.id.header_layout, header)
            commit()
        }
    private fun showConfirmationDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Token Expired")
        alertDialogBuilder.setMessage("Your token has expired. Do you want to renew it?")
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, _ ->
            goToStart()
            finish()
        })

        alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ ->
            goToStart()
            dialog.dismiss()
        })

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun goToStart(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
