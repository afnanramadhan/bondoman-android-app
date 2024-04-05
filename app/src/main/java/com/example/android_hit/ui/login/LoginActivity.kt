package com.example.android_hit.ui.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.android_hit.utils.CheckJWTBackground
import com.example.android_hit.MainActivity
import com.example.android_hit.R
import com.example.android_hit.utils.TokenManager

class LoginActivity : AppCompatActivity() {

    private lateinit var fragmentManager : FragmentManager
    private lateinit var toLoginPageButton : Button
    private lateinit var sharedPref : TokenManager

    private fun initComponent(){
        toLoginPageButton = findViewById(R.id.toLoginPageButton)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initComponent()
        sharedPref = TokenManager(this)


        //Cek kalo user dah login
        val isLogin = sharedPref.isLogin("IS_LOGIN")
        if(isLogin){
            //nyalain lagi background service trus dilempar ke page home
            val serviceIntent = Intent(this, CheckJWTBackground::class.java)
            this.startService(serviceIntent)
            goToHome()
        }

        toLoginPageButton.setOnClickListener {
                it.visibility = View.GONE
                fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.loginActivity, LoginFragment()).commit()

        }

    }
    private fun goToHome(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun showNetworkDialog(): AlertDialog {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("No Internet Connection")
        alertDialogBuilder.setMessage("Please check your internet connection")
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        return alertDialog
    }
}