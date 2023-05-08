package com.example.st5

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.st5.databinding.ActivityFinanzasBinding


class Finanzas : AppCompatActivity() {

    private lateinit var binding: ActivityFinanzasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finanzas)

        supportFragmentManager.commit {
            replace<finanzasmain>(R.id.ContainerFinanzas, "finanzasmain")
            setReorderingAllowed(true)
            addToBackStack(null)

            binding = ActivityFinanzasBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.PerfilButton.setOnClickListener {
                val intent = Intent(this@Finanzas, Perfil::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromleft, R.anim.toright)
            }

            binding.HistorialButton.setOnClickListener{
                val intent = Intent(this@Finanzas, Historial::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromleft, R.anim.toright)
            }
            binding.IndexButton.setOnClickListener{
                val intent = Intent(this@Finanzas, Index::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromleft, R.anim.toright)
            }
            binding.PlanesDeAhorroButton.setOnClickListener{
                val intent = Intent(this@Finanzas, PlanesDeAhorro::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromleft, R.anim.toright)
            }
            binding.FinanzasButton.setOnClickListener{
            }
        }
        val fragment: Finanzas? = supportFragmentManager.findFragmentByTag("finanzasmain") as Finanzas?

    }
    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FragContainer,fragment)
        fragmentTransaction.commit()
    }
}