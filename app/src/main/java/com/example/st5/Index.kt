package com.example.st5

import android.content.Intent
import android.os.Bundle
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
            replace<indexmain>(R.id.ContainerIndex, "indexmain")
            setReorderingAllowed(true)
            addToBackStack(null)

            binding = ActivityIndexBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.PerfilButton.setOnClickListener {
                val intent = Intent(this@Index, Perfil::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromleft, R.anim.toright)
            }

            binding.HistorialButton.setOnClickListener{
                val intent = Intent(this@Index, Historial::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromleft, R.anim.toright)
            }
            binding.IndexButton.setOnClickListener{
            }
            binding.PlanesDeAhorroButton.setOnClickListener{
                val intent = Intent(this@Index, PlanesDeAhorro::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromright, R.anim.toleft)
            }
            binding.FinanzasButton.setOnClickListener{
                val intent = Intent(this@Index, Finanzas::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fromright, R.anim.toleft)
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