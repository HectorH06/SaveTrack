package com.example.st5

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
            replace<indexmain>(R.id.ViewContainer, "indexmain")
            setReorderingAllowed(true)
            addToBackStack(null)

            binding = ActivityIndexBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.PerfilButton.setOnClickListener {
                val perfil = perfilmain()
                val fragment = supportFragmentManager.findFragmentById(R.id.ViewContainer)

                val enterAnimation: Int
                val exitAnimation: Int

                when (fragment) {
                    is historialmain, is indexmain, is planesdeahorromain, is finanzasmain -> {
                        enterAnimation = R.anim.fromright
                        exitAnimation = R.anim.toleft
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(enterAnimation, exitAnimation)
                            .replace(R.id.ViewContainer, perfil)
                            .addToBackStack(null)
                            .commit()
                    }
                    else -> {}
                }
            }

            binding.HistorialButton.setOnClickListener {
                val historial = historialmain()
                val fragment = supportFragmentManager.findFragmentById(R.id.ViewContainer)

                val enterAnimation: Int
                val exitAnimation: Int

                when (fragment) {
                    is indexmain, is planesdeahorromain, is finanzasmain -> {
                        enterAnimation = R.anim.fromright
                        exitAnimation = R.anim.toleft
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(enterAnimation, exitAnimation)
                            .replace(R.id.ViewContainer, historial)
                            .addToBackStack(null)
                            .commit()
                    }
                    is perfilmain -> {
                        enterAnimation = R.anim.fromleft
                        exitAnimation = R.anim.toright
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(enterAnimation, exitAnimation)
                            .replace(R.id.ViewContainer, historial)
                            .addToBackStack(null)
                            .commit()
                    }
                    else -> {}
                }
            }
            binding.IndexButton.setOnClickListener {
                val index = indexmain()
                val fragment = supportFragmentManager.findFragmentById(R.id.ViewContainer)

                val enterAnimation: Int
                val exitAnimation: Int

                when (fragment) {
                    is planesdeahorromain, is finanzasmain -> {
                        enterAnimation = R.anim.fromright
                        exitAnimation = R.anim.toleft
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(enterAnimation, exitAnimation)
                            .replace(R.id.ViewContainer, index)
                            .addToBackStack(null)
                            .commit()
                    }
                    is perfilmain, is historialmain -> {
                        enterAnimation = R.anim.fromleft
                        exitAnimation = R.anim.toright
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(enterAnimation, exitAnimation)
                            .replace(R.id.ViewContainer, index)
                            .addToBackStack(null)
                            .commit()
                    }
                    else -> {}
                }
            }
            binding.PlanesDeAhorroButton.setOnClickListener {
                val pda = planesdeahorromain()
                val fragment = supportFragmentManager.findFragmentById(R.id.ViewContainer)

                val enterAnimation: Int
                val exitAnimation: Int

                when (fragment) {
                    is finanzasmain -> {
                        enterAnimation = R.anim.fromright
                        exitAnimation = R.anim.toleft
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(enterAnimation, exitAnimation)
                            .replace(R.id.ViewContainer, pda)
                            .addToBackStack(null)
                            .commit()
                    }
                    is perfilmain, is historialmain, is indexmain -> {
                        enterAnimation = R.anim.fromleft
                        exitAnimation = R.anim.toright
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(enterAnimation, exitAnimation)
                            .replace(R.id.ViewContainer, pda)
                            .addToBackStack(null)
                            .commit()
                    }
                    else -> {}
                }
            }
            binding.FinanzasButton.setOnClickListener {
                val finanzas = finanzasmain()
                val fragment = supportFragmentManager.findFragmentById(R.id.ViewContainer)

                val enterAnimation: Int
                val exitAnimation: Int

                when (fragment) {
                    is perfilmain, is historialmain, is indexmain, is planesdeahorromain -> {
                        enterAnimation = R.anim.fromleft
                        exitAnimation = R.anim.toright
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(enterAnimation, exitAnimation)
                            .replace(R.id.ViewContainer, finanzas)
                            .addToBackStack(null)
                            .commit()
                    }
                    else -> {}
                }
            }
        }
        val fragment: Index? = supportFragmentManager.findFragmentByTag("indexmain") as Index?

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FragContainer, fragment)
        fragmentTransaction.commit()
    }
}