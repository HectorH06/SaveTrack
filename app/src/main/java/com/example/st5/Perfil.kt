package com.example.st5

import android.content.Intent
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

            binding = ActivityPerfilBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.PerfilButton.setOnClickListener {
            }

            binding.HistorialButton.setOnClickListener{
                val intent = Intent(this@Perfil, Historial::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromright, R.anim.toleft)
            }
            binding.IndexButton.setOnClickListener{
                val intent = Intent(this@Perfil, Index::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromright, R.anim.toleft)
            }
            binding.PlanesDeAhorroButton.setOnClickListener{
                val intent = Intent(this@Perfil, PlanesDeAhorro::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromright, R.anim.toleft)
            }
            binding.FinanzasButton.setOnClickListener{
                val intent = Intent(this@Perfil, Finanzas::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromright, R.anim.toleft)
            }
        }
            val fragment: perfilmain? = supportFragmentManager.findFragmentByTag("perfilmain") as perfilmain?
        }
    }
