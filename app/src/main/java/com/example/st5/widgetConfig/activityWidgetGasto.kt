package com.example.st5.widgetConfig

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.st5.R
import com.example.st5.databinding.WidgetContainerBinding

class activityWidgetGasto : AppCompatActivity() {
    lateinit var binding: WidgetContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            replace<addGasto>(R.id.FragContainer, "addGasto")
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}