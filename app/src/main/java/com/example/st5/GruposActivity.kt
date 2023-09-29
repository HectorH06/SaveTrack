package com.example.st5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.st5.databinding.ActivityMainBinding

class GruposActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupos)

        val isDarkMode = intent.getBooleanExtra("isDarkMode", false)
        if (isDarkMode) {
            setTheme(R.style.Theme_AppTheme)
        } else {
            setTheme(R.style.Theme_AppTheme_Light)
        }
        Log.i("MODO", isDarkMode.toString())

        supportFragmentManager.commit {
            replace<gruposList>(R.id.GruposContainer, "gruposList")
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}