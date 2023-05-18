package com.example.st5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.st5.databinding.FragmentIndexmandadosBinding

class indexmandados : Fragment() {
    private lateinit var binding: FragmentIndexmandadosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIndexmandadosBinding.inflate(inflater, container, false)
        return binding.root

    }
}