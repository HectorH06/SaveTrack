package com.example.st5

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.st5.calendar.HistorialPagerAdapter
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentHistorialmainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class historialmain : Fragment() {
    private lateinit var binding: FragmentHistorialmainBinding
    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_historial2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_historial)
            }

            Log.i("MODO", isDarkMode.toString())

            val hoy = LocalDate.now()
            val day = hoy.dayOfMonth
            val fDay = String.format("%02d", day)
            val month = hoy.monthValue - 1
            val fMonth = String.format("%02d", month)
            val year = hoy.year
            val fecha = "$year$fMonth$fDay"
            val today: Int = fecha.replace("-", "").toInt()
            Log.v("today", "$today")

            val startYear = 1998

            val monthsPagerAdapter = HistorialPagerAdapter(requireContext(), isDarkMode, day)
            val viewPager: ViewPager = binding.calendarView
            viewPager.adapter = monthsPagerAdapter

            viewPager.currentItem = month + (year - startYear) * 12
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
    }

    private suspend fun isDarkModeEnabled(context: Context): Boolean {
        var komodo: Boolean

        withContext(Dispatchers.IO) {
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
        binding = FragmentHistorialmainBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var fechaSeleccionada: String

        binding.calendarPick.setOnClickListener {
            val day = binding.calendarPick.dayOfMonth
            val fDay = String.format("%02d", day)
            val month = binding.calendarPick.month + 1
            val fMonth = String.format("%02d", month)
            val year = binding.calendarPick.year
            fechaSeleccionada = "$year$fMonth$fDay"
            val fsi: Int = fechaSeleccionada.replace("-", "").toInt()
            Toast.makeText(requireContext(), "$fsi", Toast.LENGTH_SHORT).show()
            val montosF = historialMontosList.fechaSearch(fsi)

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.historial_container, montosF).addToBackStack(null).commit()
        }

        binding.calendarView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                val startYear = LocalDate.now().year - 25
                val year = position / 12 + startYear
                val mesesito = when (position % 12) {
                    0 -> "Enero"
                    1 -> "Febrero"
                    2 -> "Marzo"
                    3 -> "Abril"
                    4 -> "Mayo"
                    5 -> "Junio"
                    6 -> "Julio"
                    7 -> "Agosto"
                    8 -> "Septiembre"
                    9 -> "Octubre"
                    10 -> "Noviembre"
                    11 -> "Diciembre"
                    else -> "cualquier mes"
                }


            }
        })

        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.historial_container, Configuracion()).addToBackStack(null).commit()
        }

        binding.TagsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.historial_container, historialEtiquetas()).addToBackStack(null)
                .commit()
        }

        binding.PapeleraButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.historial_container, historialPapelera()).addToBackStack(null)
                .commit()
        }

        binding.GuardadosButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.historial_container, historialFavoritos()).addToBackStack(null)
                .commit()
        }
    }
}