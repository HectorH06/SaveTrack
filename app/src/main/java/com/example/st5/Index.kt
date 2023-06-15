package com.example.st5

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.st5.database.Stlite
import com.example.st5.databinding.ActivityIndexBinding
import com.example.st5.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Index : AppCompatActivity() {

    lateinit var binding: ActivityIndexBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.ViewContainer
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        viewPager.currentItem = 2

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(this@Index)

            if (isDarkMode) {
                setTheme(R.style.Theme_AppTheme)
            } else {
                setTheme(R.style.Theme_AppTheme_Light)
            }

            Log.i("MODO", isDarkMode.toString())
        }
    }

    private suspend fun isDarkModeEnabled(context: Context): Boolean {
        var komodo: Boolean

        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(context).getAssetsDao()

            val mode = assetsDao.getTheme()
            komodo = mode != 0
        }

        return komodo
    }
}