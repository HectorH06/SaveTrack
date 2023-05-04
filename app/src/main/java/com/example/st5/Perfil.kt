package com.example.st5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.st5.databinding.ActivityPerfilBinding

class Perfil : AppCompatActivity() {
    lateinit var binding: ActivityPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        supportFragmentManager.commit {
            replace<perfilmain>(R.id.ContainerPerfil, "perfilmain")
            setReorderingAllowed(true)
            addToBackStack(null)

            val fragment: perfilmain? = supportFragmentManager.findFragmentByTag("perfilmain") as perfilmain?
        }
    }
}