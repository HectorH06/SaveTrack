package com.example.st5

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.st5.databinding.ActivityIndexBinding
import com.example.st5.ui.main.SectionsPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class Index : AppCompatActivity() {

    lateinit var binding: ActivityIndexBinding
    private var currentView: Int = 2
    private var currentFrag: Int = 0
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isDarkMode = intent.getBooleanExtra("isDarkMode", false)
        if (isDarkMode) {
            setTheme(R.style.Theme_AppTheme)
            binding.bottomBar.setBackgroundResource(R.drawable.n1bar)
        } else {
            setTheme(R.style.Theme_AppTheme_Light)
            binding.bottomBar.setBackgroundResource(R.drawable.n5bar)
        }

        val themeAttrs = intArrayOf(com.google.android.material.R.attr.colorOnPrimary, com.google.android.material.R.attr.colorPrimaryVariant)

        val typedArray = obtainStyledAttributes(themeAttrs)
        val colorOnPrimary = typedArray.getColor(0, 0)
        val colorPrimaryVariant = typedArray.getColor(1, 0)

        binding.bottomBar.itemIconTintList = ColorStateList.valueOf(colorOnPrimary)
        binding.bottomBar.itemTextColor = ColorStateList.valueOf(colorPrimaryVariant)

        typedArray.recycle()

        Log.i("MODO", isDarkMode.toString())

        val extraContainer = intent.getIntExtra("currentView", 2)
        currentView = extraContainer
        val fragToGo = intent.getIntExtra("fragToGo", 0)
        currentFrag = fragToGo

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, isDarkMode, currentView, currentFrag)
        val viewPager: ViewPager = binding.ViewContainer
        viewPager.adapter = sectionsPagerAdapter

        val bottomNavigationView: BottomNavigationView = binding.bottomBar
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_perfil -> viewPager.currentItem = 0
                R.id.menu_item_historial -> viewPager.currentItem = 1
                R.id.menu_item_index -> viewPager.currentItem = 2
                R.id.menu_item_pda -> viewPager.currentItem = 3
                R.id.menu_item_finanzas -> viewPager.currentItem = 4
            }
            true
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })

        viewPager.currentItem = currentView
    }
}
