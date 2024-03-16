package com.example.android_hit

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

    }

    private fun setCurrentFragment(fragment: Fragment, header: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            replace(R.id.header_layout, header)
            commit()
        }

}
