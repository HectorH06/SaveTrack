package com.example.st5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.st5.databinding.ActivityIndexBinding
import com.example.st5.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout

class Index : AppCompatActivity() {

    lateinit var binding: ActivityIndexBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isDarkMode = intent.getBooleanExtra("isDarkMode", false)
        if (isDarkMode) {
            setTheme(R.style.Theme_AppTheme)
            binding.tabs.setBackgroundResource(R.drawable.n1bar)
        } else {
            setTheme(R.style.Theme_AppTheme_Light)
            binding.tabs.setBackgroundResource(R.drawable.n5bar)
        }
        Log.i("MODO", isDarkMode.toString())

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, isDarkMode)
        val viewPager: ViewPager = binding.ViewContainer
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        viewPager.currentItem = 2
    }
}