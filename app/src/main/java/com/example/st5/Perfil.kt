package com.example.st5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class Perfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        supportFragmentManager.commit {
            replace<perfilmain>(R.id.ContainerPerfil, "perfilmain")
            setReorderingAllowed(true)
            addToBackStack(null)
        }
        val fragment: Perfil? = supportFragmentManager.findFragmentByTag("perfilmain") as Perfil?
    }
}