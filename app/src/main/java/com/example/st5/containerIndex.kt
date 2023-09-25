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
import com.example.st5.databinding.ContainerIndexBinding
import com.example.st5.ui.main.PageViewModel

class containerIndex : Fragment() {
    private lateinit var binding: ContainerIndexBinding
    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pageViewModel = ViewModelProvider(this)[PageViewModel::class.java].apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 2)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ContainerIndexBinding.inflate(inflater, container, false)
        parentFragmentManager.commit {
            val fragToGo = arguments?.getInt(fragTo) ?: 0
            Log.v("fragToGo", "$fragToGo")
            when (fragToGo) {
                0 -> replace<indexmain>(R.id.index_container, "indexmain")
                1 -> replace<indexmandados>(R.id.index_container, "indexmandados")
                2 -> replace<indexPorPagar>(R.id.index_container, "indexPorPagar")
                3 -> replace<indexmontoupdate>(R.id.index_container, "indexmontoupdate")
                4 -> replace<indexadd>(R.id.index_container, "indexadd")
                5 -> replace<indexIngresosList>(R.id.index_container, "indexIngresosList")
                6 -> replace<indexGastosList>(R.id.index_container, "indexGastosList")
                else -> replace<indexmain>(R.id.index_container, "indexmain")
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
        fun newInstance(sectionNumber: Int, fragToGo: Int): containerIndex {
            return containerIndex().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putInt(fragTo, fragToGo)
                }
            }
        }
    }
}