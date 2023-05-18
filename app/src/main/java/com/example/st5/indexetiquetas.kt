package com.example.st5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.st5.databinding.FragmentIndexetiquetasBinding

class indexetiquetas : Fragment() {
    private lateinit var binding: FragmentIndexetiquetasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIndexetiquetasBinding.inflate(inflater, container, false)
        return binding.root

    }
}