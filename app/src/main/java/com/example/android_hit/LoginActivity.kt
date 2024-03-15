package com.example.android_hit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.android_hit.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var fragmentManager : FragmentManager
    private lateinit var toLoginPageButton : Button

    private fun initComponent(){
        toLoginPageButton = findViewById(R.id.toLoginPageButton)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initComponent()

        toLoginPageButton.setOnClickListener {

                it.visibility = View.GONE
                fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.loginActivity, LoginFragment()).commit()

        }

    }
}