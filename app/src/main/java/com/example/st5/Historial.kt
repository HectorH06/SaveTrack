package com.example.st5

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.st5.databinding.ActivityHistorialBinding

class Historial : AppCompatActivity() {

    lateinit var binding: ActivityHistorialBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        supportFragmentManager.commit {
            replace<historialmain>(R.id.ContainerHistorial, "historialmain")
            setReorderingAllowed(true)
            addToBackStack(null)

            binding = ActivityHistorialBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.PerfilButton.setOnClickListener {
                val intent = Intent(this@Historial, Perfil::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromleft, R.anim.toright)
            }

            binding.HistorialButton.setOnClickListener{
            }
            binding.IndexButton.setOnClickListener{
                val intent = Intent(this@Historial, Index::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromright, R.anim.toleft)
            }
            binding.PlanesDeAhorroButton.setOnClickListener{
                val intent = Intent(this@Historial, PlanesDeAhorro::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromright, R.anim.toleft)
            }
            binding.FinanzasButton.setOnClickListener{
                val intent = Intent(this@Historial, Finanzas::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromright, R.anim.toleft)
            }
        }
        val fragment: Historial? = supportFragmentManager.findFragmentByTag("historialmain") as Historial?

    }
    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FragContainer,fragment)
        fragmentTransaction.commit()
    }
}