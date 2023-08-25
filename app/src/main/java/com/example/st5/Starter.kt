package com.example.st5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentStarterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Starter : Fragment() {
    private lateinit var binding: FragmentStarterBinding

    private var isDarkMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val actual = Login()
                    parentFragmentManager.beginTransaction().replace(R.id.FragContainer, actual)
                        .addToBackStack(null).commit()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentStarterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        suspend fun start() {
            withContext(Dispatchers.IO) {
                val usuarioDao = Stlite.getInstance(
                    requireContext()
                ).getUsuarioDao()

                val selected = usuarioDao.getUserData()
                Log.v(
                    "SELECTED USERS start", selected.toString()
                )
                if (selected.toString() != "[]") {
                    val intent = Intent(activity, Index::class.java)
                    intent.putExtra("isDarkMode", isDarkMode)
                    startActivity(intent)
                } else {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.FragContainer, Login()).addToBackStack(null).commit()
                }
            }
        }
        lifecycleScope.launch {
            isDarkMode = isDarkModeEnabled()
            start()
        }
    }

    private suspend fun isDarkModeEnabled(): Boolean {
        var komodo: Boolean

        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(requireContext()).getAssetsDao()

            val mode = assetsDao.getTheme()
            komodo = mode != 0
        }

        return komodo
    }
}