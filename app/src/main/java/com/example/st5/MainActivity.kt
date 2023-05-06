package com.example.st5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.st5.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() { //redirigir activities
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            replace<Login>(R.id.FragContainer, "Login")
            setReorderingAllowed(true)
            addToBackStack(null)

            val fragment: Login? = supportFragmentManager.findFragmentByTag("Login") as Login?
        }
    }
}
// METER IF PARA REDIRIGIR A INDEX SI LA BASE DE DATOS NO ESTÁ VACÍA
// SI SE CIERRA SESIÓN, QUE SE HAGA BACKUP DE TODAS LAS TABLAS Y SE VACÍE LA BD