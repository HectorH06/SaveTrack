package com.example.st5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.st5.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            replace<Starter>(R.id.FragContainer, "Starter")
            setReorderingAllowed(true)
            addToBackStack(null)

            val fragment: Starter? = supportFragmentManager.findFragmentByTag("Starter") as Starter?
        }
    }
}