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
import com.example.st5.databinding.ContainerHistorialBinding
import com.example.st5.ui.main.PageViewModel

class containerHistorial : Fragment() {
    private lateinit var binding: ContainerHistorialBinding
    private lateinit var pageViewModel: PageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this)[PageViewModel::class.java].apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
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
        binding = ContainerHistorialBinding.inflate(inflater, container, false)
        parentFragmentManager.commit {
            val fragToGo = arguments?.getInt(fragTo) ?: 0
            Log.v("fragToGo", "$fragToGo")
            when (fragToGo) {
                0 -> replace<historialmain>(R.id.historial_container, "historialmain")
                1 -> replace<historialAdd>(R.id.historial_container, "historialAdd")
                2 -> replace<historialEtiquetas>(R.id.historial_container, "historialEtiquetas")
                3 -> replace<historialUpdate>(R.id.historial_container, "historialUpdate")
                4 -> replace<historialFavoritos>(R.id.historial_container, "historialFavoritos")
                5 -> replace<historialMontosList>(R.id.historial_container, "historialMontosList")
                else -> replace<historialmain>(R.id.historial_container, "historialmain")
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
        fun newInstance(sectionNumber: Int, fragToGo: Int): containerHistorial {
            return containerHistorial().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putInt(fragTo, fragToGo)
                }
            }
        }
    }
}