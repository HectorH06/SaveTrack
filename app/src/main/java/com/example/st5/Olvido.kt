package com.example.st5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.st5.databinding.FragmentOlvidoBinding

class Olvido : Fragment() {
    private lateinit var binding: FragmentOlvidoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOlvidoBinding.inflate(inflater, container, false)
        return binding.root

    }

    // UPDATE para la contraseña del usuario
    // SMS para recuperar contraseña
}