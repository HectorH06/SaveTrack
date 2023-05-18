package com.example.st5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.st5.databinding.FragmentIndexingresosBinding

class indexingresos : Fragment() {
    private lateinit var binding: FragmentIndexingresosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIndexingresosBinding.inflate(inflater, container, false)
        return binding.root

    }
}