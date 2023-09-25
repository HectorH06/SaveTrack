package com.example.st5

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.st5.databinding.ContainerFinanzasBinding
import com.example.st5.ui.main.PageViewModel

class containerFinanzas : Fragment() {
    private lateinit var binding: ContainerFinanzasBinding
    private lateinit var pageViewModel: PageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this)[PageViewModel::class.java].apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 4)
        }
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
        binding = ContainerFinanzasBinding.inflate(inflater, container, false)
        parentFragmentManager.commit {
            val fragToGo = arguments?.getInt(fragTo) ?: 0
            Log.v("fragToGo", "$fragToGo")
            when (fragToGo) {
                0 -> replace<finanzasmain>(R.id.finanzas_container, "finanzasmain")
                1 -> replace<finanzasEventos>(R.id.finanzas_container, "finanzasEventos")
                2 -> replace<finanzasEventosAdd>(R.id.finanzas_container, "finanzasEventosAdd")
                3 -> replace<finanzasEventosList>(R.id.finanzas_container, "finanzasEventosList")
                4 -> replace<finanzasEventosUpdate>(R.id.finanzas_container, "finanzasEventosUpdate")
                5 -> replace<finanzasstatsahorro>(R.id.finanzas_container, "finanzasstatsahorro")
                6 -> replace<finanzasConySug>(R.id.finanzas_container, "finanzasConySug")
                else -> replace<finanzasmain>(R.id.finanzas_container, "finanzasmain")
            }
            setReorderingAllowed(true)
            addToBackStack(null)
        }
        return binding.root

    }
    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        private const val fragTo = "frag_to_go"

        @JvmStatic
        fun newInstance(sectionNumber: Int, fragToGo: Int): containerFinanzas {
            return containerFinanzas().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putInt(fragTo, fragToGo)
                }
            }
        }
    }
}