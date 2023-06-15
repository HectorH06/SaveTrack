package com.example.st5

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentConfiguracionBinding
import com.polyak.iconswitch.IconSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Configuracion : Fragment() {
    private lateinit var binding: FragmentConfiguracionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
                binding.claroscuro.checked = IconSwitch.Checked.RIGHT
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
            }

            Log.i("MODO", isDarkMode.toString())
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = indexmain()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.index_container, prev)
                        .addToBackStack(null).commit()
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

    private suspend fun updateTheme(context: Context, komodo: Long){

        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(context).getAssetsDao()

            assetsDao.updateTheme(komodo)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfiguracionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = perfilmain()

        val adapterF = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.frecuenciaoptions,
            android.R.layout.simple_spinner_item
        )
        val adapterI = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipooptions,
            android.R.layout.simple_spinner_item
        )
        val adapterG = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.etiquetaoptions,
            android.R.layout.simple_spinner_item
        )
        adapterF.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterG.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterI.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.perfil_container, back).addToBackStack(null).commit()
        }

        binding.claroscuro.setCheckedChangeListener {

            // TODO AÑADIR DESTROYER DE VIEWS PARA QUE SE VUELVAN A CONSTRUIR TODAS Y NO HAYA ERRORES VISUALES
            when (binding.claroscuro.checked) {
                IconSwitch.Checked.LEFT -> {
                    binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
                    lifecycleScope.launch{
                        updateTheme(requireContext(), 0)
                    }
                }
                IconSwitch.Checked.RIGHT -> {
                    binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
                    lifecycleScope.launch{
                        updateTheme(requireContext(), 1)
                    }
                }
                else -> {}
            }
        }
    }
}