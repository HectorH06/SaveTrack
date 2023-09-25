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
import com.example.st5.databinding.ContainerPdaBinding
import com.example.st5.ui.main.PageViewModel

class containerPlanesDeAhorro : Fragment() {
    private lateinit var binding: ContainerPdaBinding
    private lateinit var pageViewModel: PageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this)[PageViewModel::class.java].apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 3)
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
        binding = ContainerPdaBinding.inflate(inflater, container, false)
        parentFragmentManager.commit {
            val fragToGo = arguments?.getInt(fragTo) ?: 0
            Log.v("fragToGo", "$fragToGo")
            when (fragToGo) {
                0 -> replace<planesdeahorromain>(R.id.pda_container, "planesdeahorromain")
                1 -> replace<pdaDeudasList>(R.id.pda_container, "pdaDeudasList")
                else -> replace<planesdeahorromain>(R.id.pda_container, "planesdeahorromain")
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
        fun newInstance(sectionNumber: Int, fragToGo: Int): containerPlanesDeAhorro {
            return containerPlanesDeAhorro().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putInt(fragTo, fragToGo)
                }
            }
        }
    }
}