package com.example.st5

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.st5.databinding.FragmentHistorialmainBinding

class historialmain : Fragment() {
    private lateinit var binding: FragmentHistorialmainBinding

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistorialmainBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var fechaSeleccionada: String

        binding.calendarView.setOnClickListener() {
                val day = binding.calendarView.dayOfMonth
                val month = binding.calendarView.month + 1
                val year = binding.calendarView.year
                fechaSeleccionada = "$year-$month-$day"

                Toast.makeText(requireContext(), "$fechaSeleccionada", Toast.LENGTH_SHORT).show()

                val montosF = historialMontosList.fechaSearch(fechaSeleccionada)

                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.historial_container, montosF).addToBackStack(null).commit()
        }
    }
}