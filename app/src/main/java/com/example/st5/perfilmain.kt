package com.example.st5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.st5.databinding.FragmentPerfilmainBinding

class perfilmain : Fragment() {
    private lateinit var binding: FragmentPerfilmainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilmainBinding.inflate(inflater, container, false)
        return binding.root

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