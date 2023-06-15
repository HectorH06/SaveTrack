package com.example.st5

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentFinanzasmainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class finanzasmain : Fragment() {
    private lateinit var binding: FragmentFinanzasmainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
            }

            Log.i("MODO", isDarkMode.toString())
        }
       */
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
    }

    private suspend fun isDarkModeEnabled(context: Context): Boolean {
        var komodo: Boolean

        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(context).getAssetsDao()

            val mode = assetsDao.getTheme()
            komodo = mode != 0
        }

        return komodo
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinanzasmainBinding.inflate(inflater, container, false)
        return binding.root

    }
}