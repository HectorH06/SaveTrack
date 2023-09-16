package com.example.st5

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.st5.calendar.MonthsPagerAdapter
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentFinanzaseventosBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*

class finanzasEventos : Fragment() {
    private lateinit var binding: FragmentFinanzaseventosBinding
    private var isDarkMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
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

            val monthsPagerAdapter = MonthsPagerAdapter(requireContext(), isDarkMode, day)
            val viewPager: ViewPager = binding.calendarView
            viewPager.adapter = monthsPagerAdapter

            viewPager.currentItem = month + (year - startYear) * 12 // 299 porque es 300 (mitad del viewpager configurado a +-25 años) - 1 (porque monthvalue va del 1 al 12)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.finanzas_container, finanzasmain()).addToBackStack(null).commit()
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

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinanzaseventosBinding.inflate(inflater, container, false)
        val hoy = LocalDate.now()
        val month = hoy.monthValue - 1
        val year = hoy.year
        val mesesito = when (month) {
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

        binding.bar.text = "$mesesito $year"
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, Configuracion()).addToBackStack(null).commit()
        }

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.finanzas_container, finanzasmain()).addToBackStack(null).commit()
        }

        binding.AgregarEventoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, finanzasEventosAdd()).addToBackStack(null).commit()
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

                binding.bar.text = "$mesesito $year"
            }
        })
    }
}
