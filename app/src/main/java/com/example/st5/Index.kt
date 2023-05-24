package com.example.st5

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.st5.databinding.ActivityIndexBinding

class Index : AppCompatActivity() {

    lateinit var binding: ActivityIndexBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        supportFragmentManager.commit {
            replace<indexmain>(R.id.ViewContainer, "indexmain")
            setReorderingAllowed(true)
            addToBackStack(null)

            binding = ActivityIndexBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.PerfilButton.setOnClickListener {
                val perfil = perfilmain()
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.ViewContainer, perfil).addToBackStack(null).commit()
                }

            binding.HistorialButton.setOnClickListener{
                val historial = historialmain()
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.ViewContainer, historial).addToBackStack(null).commit()
            }
            binding.IndexButton.setOnClickListener{
                val index = indexmain()
                supportFragmentManager.beginTransaction()
                    //.setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.ViewContainer, index).addToBackStack(null).commit()
            }
            binding.PlanesDeAhorroButton.setOnClickListener{
                val pda = planesdeahorromain()
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                    .replace(R.id.ViewContainer, pda).addToBackStack(null).commit()
            }
            binding.FinanzasButton.setOnClickListener{
                val finanzas = finanzasmain()
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                    .replace(R.id.ViewContainer, finanzas).addToBackStack(null).commit()
            }
        }
        val fragment: Index? = supportFragmentManager.findFragmentByTag("indexmain") as Index?

    }
    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FragContainer,fragment)
        fragmentTransaction.commit()
    }
}