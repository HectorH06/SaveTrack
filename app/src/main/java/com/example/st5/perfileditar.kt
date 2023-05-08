package com.example.st5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.st5.databinding.FragmentPerfileditarBinding

class perfileditar : Fragment() {
    private lateinit var binding: FragmentPerfileditarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = perfilmain()
                    parentFragmentManager.beginTransaction().replace(R.id.ContainerPerfil, prev)
                        .addToBackStack(null).commit()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfileditarBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gobackeditarpefilbtn.setOnClickListener {
            val back= perfilmain()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.ContainerPerfil, back).addToBackStack(null).commit()
        }
    }
    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = context?.let {
            Room.databaseBuilder(
                it.applicationContext,
                Stlite::class.java, "Stlite"
            ).build()
        }

// Llamar a un método de consulta en la instancia de la base de datos para obtener los datos que deseas mostrar
        val nombre = db.UsuarioDao(requireContext()).getUserById(id)
        super.onViewCreated(view, savedInstanceState)
        binding.UsernameTV.setText(Usuario.nombre)
    }
    */
}