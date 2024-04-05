package com.example.android_hit.ui.transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.android_hit.R
import com.example.android_hit.databinding.ActivityDetailTransactionBinding

class DetailTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentFragment(DetailTransaction(), HeaderDetailTransaction())
    }

    private fun setCurrentFragment(fragment: Fragment, header: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            replace(R.id.header_layout, header)
            commit()
        }
}