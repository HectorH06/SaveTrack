package com.example.st5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class Perfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        supportFragmentManager.commit {
            replace(R.id.ContainerPerfil, perfilmain())
            setReorderingAllowed(true)
            addToBackStack(null)
        }
        val fragment: perfilmain? = supportFragmentManager.findFragmentByTag("perfilmain") as perfilmain?
    }
}