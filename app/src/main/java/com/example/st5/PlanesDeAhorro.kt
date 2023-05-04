package com.example.st5

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.st5.databinding.ActivityFinanzasBinding
import com.example.st5.databinding.ActivityPlanesdeahorroBinding

class PlanesDeAhorro : AppCompatActivity() {

    lateinit var binding: ActivityPlanesdeahorroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planesdeahorro)

        supportFragmentManager.commit {
            replace<planesdeahorromain>(R.id.ContainerPlanesDeAhorro, "planesdeahorromain")
            setReorderingAllowed(true)
            addToBackStack(null)

            binding = ActivityPlanesdeahorroBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.PerfilButton.setOnClickListener {
                val intent = Intent(this@PlanesDeAhorro, Perfil::class.java)
                startActivity(intent)
            }

            binding.HistorialButton.setOnClickListener{
                val intent = Intent(this@PlanesDeAhorro, Historial::class.java)
                startActivity(intent)
            }
            binding.IndexButton.setOnClickListener{
                val intent = Intent(this@PlanesDeAhorro, Index::class.java)
                startActivity(intent)
            }
            binding.PlanesDeAhorroButton.setOnClickListener{
                val intent = Intent(this@PlanesDeAhorro, PlanesDeAhorro::class.java)
                startActivity(intent)
            }
            binding.FinanzasButton.setOnClickListener{
                val intent = Intent(this@PlanesDeAhorro, Finanzas::class.java)
                startActivity(intent)
            }
        }
        val fragment: PlanesDeAhorro? = supportFragmentManager.findFragmentByTag("planesdeahorromain") as PlanesDeAhorro?

    }
    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FragContainer,fragment)
        fragmentTransaction.commit()
    }
}