package com.example.st5

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.st5.databinding.ContainerPerfilBinding
import com.example.st5.ui.main.PageViewModel

class containerPerfil : Fragment() {
    private lateinit var binding: ContainerPerfilBinding
    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pageViewModel = ViewModelProvider(this)[PageViewModel::class.java].apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 0)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ContainerPerfilBinding.inflate(inflater, container, false)
        parentFragmentManager.commit {
            val fragToGo = arguments?.getInt(fragTo) ?: 0
            Log.v("fragToGo", "$fragToGo")
            when (fragToGo) {
                0 -> replace<perfilmain>(R.id.perfil_container, "perfilmain")
                1 -> replace<perfileditar>(R.id.perfil_container, "perfileditar")
                2 -> replace<Configuracion>(R.id.perfil_container, "Configuracion")
                else -> replace<perfilmain>(R.id.perfil_container, "perfilmain")
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
        fun newInstance(sectionNumber: Int, fragToGo: Int): containerPerfil {
            return containerPerfil().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putInt(fragTo, fragToGo)
                }
            }
        }
    }
}