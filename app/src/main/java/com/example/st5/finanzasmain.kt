package com.example.st5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.st5.databinding.FragmentFinanzasmainBinding

class finanzasmain : Fragment() {
    private lateinit var binding: FragmentFinanzasmainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinanzasmainBinding.inflate(inflater, container, false)
        return binding.root

    }
}