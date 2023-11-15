package com.example.st5

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

class historialmain : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentHistorialmainBinding
    private var isDarkMode = false

    private var mutableEtiquetas: MutableList<String> = mutableListOf()
    private var mutableIds: MutableList<Long> = mutableListOf()
    private var mutableColores: MutableList<Int> = mutableListOf()

    private var selectedLabel = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        lifecycleScope.launch {
            isDarkMode = isDarkModeEnabled(requireContext())
            getLabels()

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
            viewPager.offscreenPageLimit = 1

            viewPager.currentItem = month + (year - startYear) * 12

            val arrayEtiquetas = mutableEtiquetas
            val adapterG = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                arrayEtiquetas
            )
            adapterG.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.SpinnerFilter.adapter = adapterG
            binding.SpinnerFilter.onItemSelectedListener = this@historialmain
        }
        return binding.root
    }

    private suspend fun getLabels() {
        withContext(Dispatchers.IO) {
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val max = labelsDao.getMaxLabel()
            val themeAttrs = intArrayOf(com.google.android.material.R.attr.colorPrimaryVariant)

            val typedArray = requireContext().obtainStyledAttributes(themeAttrs)
            val colorPrimaryVariant = typedArray.getColor(0, 0)

            mutableIds.add(0)
            mutableEtiquetas.add("Seleccionar")
            mutableColores.add(colorPrimaryVariant)
            for (i in 1..max) {
                if (labelsDao.getPlabel(i) != ""){
                    mutableIds.add(labelsDao.getIdLabel(i))
                    mutableEtiquetas.add(labelsDao.getPlabel(i))
                    mutableColores.add(labelsDao.getColor(i))
                }
            }
            Log.v("idl", "$mutableIds")
            Log.v("plabel", "$mutableEtiquetas")
            Log.v("color", "$mutableColores")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var fechaSeleccionada: String

        binding.verMontos.setOnClickListener {
            lifecycleScope.launch {
                val day = binding.spinnerPick.dayOfMonth
                val fDay = String.format("%02d", day)
                val month = binding.spinnerPick.month + 1
                val fMonth = String.format("%02d", month)
                val year = binding.spinnerPick.year
                fechaSeleccionada = "$year$fMonth$fDay"
                val fsi: Int = fechaSeleccionada.replace("-", "").toInt()
                Toast.makeText(requireContext(), "$fsi", Toast.LENGTH_SHORT).show()
                val montosF: historialMontosList = if (selectedLabel != 0) {
                    historialMontosList.fechaSearch(fsi, selectedLabel)
                } else {
                    historialMontosList.fechaSearch(fsi)
                }

                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.historial_container, montosF).addToBackStack(null).commit()
            }
        }

        val hoy = LocalDate.now()
        val day = hoy.dayOfMonth
        val month = hoy.monthValue - 1
        val year = hoy.year
        binding.spinnerPick.init(year, month, day)
        { _, nYear, nMonth, _ ->
            //val monthsPagerAdapter = HistorialPagerAdapter(requireContext(), isDarkMode, nDay)
            //binding.calendarView.adapter = monthsPagerAdapter
            binding.calendarView.currentItem = nMonth + (nYear - 1998) * 12
        }

        binding.calendarView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {}
        })

        binding.Options.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.barrita)
        }

        binding.barrita.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.etiquetas -> {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.historial_container, historialEtiquetas()).addToBackStack(null).commit()

                    true
                }
                R.id.favoritos -> {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.historial_container, historialFavoritos()).addToBackStack(null).commit()

                    true
                }
                R.id.papelera -> {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.historial_container, historialPapelera()).addToBackStack(null).commit()

                    true
                }

                else -> false
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val etiqueta = binding.SpinnerFilter.selectedItem?.toString()

        if (etiqueta != null) {
            Log.v("ETIQUETA SELECCIONADA", etiqueta.toString())
        }

        for (i in 0 until mutableEtiquetas.size) {
            if (etiqueta == mutableEtiquetas[i]) {
                selectedLabel = mutableIds[i].toInt()
                binding.Filter.setColorFilter(mutableColores[selectedLabel])
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) { selectedLabel = 0 }
}