package com.example.st5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPerfilmainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class perfilmain : Fragment() {
    private lateinit var binding: FragmentPerfilmainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilmainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        suspend fun bajarfoto(link: String) {
            withContext(Dispatchers.IO) {
                binding.ProfilePicture.load(link) {
                    crossfade(true)
                    placeholder(R.drawable.ic_add_24)
                    transformations(CircleCropTransformation())
                    scale(Scale.FILL)
                }
            }
        }
        binding.cerrarsesionperfilmainbtn.setOnClickListener {
            suspend fun cerrarSesion() {
                //SUBIR TODAS LAS TABLAS A LA BD, PERO ANTES HACER TABLA EXCLUSIVA PARA RESPALDOS y el campo para la foto
                // CIFRADOS de contraseñas aprovechando que voy a tocar los php
                withContext(Dispatchers.IO) {
                    val usuarioDao = Stlite.getInstance(
                        requireContext()
                    ).getUsuarioDao()

                    usuarioDao.clean()

                    val selected = usuarioDao.getUserData()
                    Log.v(
                        "SELECTED USERS", selected.toString()
                    )
                }
            }
            lifecycleScope.launch {
                cerrarSesion()
            }
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.EditProfileButton.setOnClickListener {
            val edit = perfileditar()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.ViewContainer, edit).addToBackStack(null).commit()
        }

        val linkfoto = "http://savetrack.com.mx/images/Bojji.jpg"
        lifecycleScope.launch {
            bajarfoto(linkfoto)
        }

        suspend fun mostrarDatos() {
            withContext(Dispatchers.IO) {
                val usuarioDao = Stlite.getInstance(
                    requireContext()
                ).getUsuarioDao()

                Log.v(
                    "id", id.toString()
                )
                val nombre = usuarioDao.checkName()
                val edad = usuarioDao.checkAge()
                val lachamba = usuarioDao.checkChamba()
                val diasaho = usuarioDao.checkDiasaho()
                val balance = usuarioDao.checkBalance()

                var chamba = ""
                val c = String.format("%06d", lachamba).toCharArray()
                if (c[0] == '1') {
                    chamba += "asalariado "
                }
                if (c[1] == '2') {
                    chamba += "vendedor "
                }
                if (c[2] == '3') {
                    chamba += "pensionado "
                }
                if (c[3] == '4') {
                    chamba += "becado "
                }
                if (c[4] == '5') {
                    chamba += "mantenido "
                }
                if (c[5] == '6') {
                    chamba += "inversionista "
                }

                chamba = chamba.replaceFirstChar { it.uppercaseChar() }

                Log.v("Name", nombre)
                Log.v("Age", edad.toString())
                Log.v("Código de Chamba", lachamba.toString())
                Log.v("Descripción de Chamba ", chamba)
                Log.v("Diasaho", diasaho.toString())
                Log.v("Balance", balance.toString())

                binding.UsernameTV.text = nombre
                binding.AgeTV.text = buildString {
                    append(edad.toString())
                    append(" años")
                }
                binding.OcupationTV.text = buildString {
                    append(chamba) // HACER EL CONVERTIDOR SEGÚN EL ÁRBOL ESE
                }
                binding.DaysSavingButton.text = buildString {
                    append("¡")
                    append(diasaho.toString())
                    append(" días ahorrando!")
                }
                binding.BalanceTV.text = buildString {
                    append("Balance: ")
                    append(balance.toString())
                }
            }
        }
        lifecycleScope.launch {
            mostrarDatos()
        }

    }

}