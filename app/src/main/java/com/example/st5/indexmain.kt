package com.example.st5

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentIndexmainBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class indexmain : Fragment() {
    private lateinit var binding: FragmentIndexmainBinding
    private val colors: MutableList<Int> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupColors()
        binding = FragmentIndexmainBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun setupColors() {
        colors.add(ContextCompat.getColor(requireContext(), R.color.P1))
        colors.add(ContextCompat.getColor(requireContext(), R.color.B2))
        colors.add(ContextCompat.getColor(requireContext(), R.color.G2))
        colors.add(ContextCompat.getColor(requireContext(), R.color.Y3))
        colors.add(ContextCompat.getColor(requireContext(), R.color.O1))
        colors.add(ContextCompat.getColor(requireContext(), R.color.R1))
    }

    private suspend fun getIG() {

    }

    private suspend fun setupPieChart() {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(
                requireContext()
            ).getMontoDao()

            val alimentos = montoDao.getAlimentos()
            val hogar = montoDao.getHogar()
            val bienestar = montoDao.getBienestar()
            val necesidades = montoDao.getNecesidades()
            val hormiga = montoDao.getHormiga()
            val ocio = montoDao.getOcio()
            val totalis = montoDao.getMonto()

            var totalAlimentos = 0.0
            for (monto in alimentos) {
                totalAlimentos += monto.valor
            }
            Log.v("Alimentos", totalAlimentos.toString())

            var totalHogar = 0.0
            for (monto in hogar) {
                totalHogar += monto.valor
            }
            Log.v("Hogar", totalHogar.toString())

            var totalBienestar = 0.0
            for (monto in bienestar) {
                totalBienestar += monto.valor
            }
            Log.v("Bienestar", totalBienestar.toString())

            var totalNecesidades = 0.0
            for (monto in necesidades) {
                totalNecesidades += monto.valor
            }
            Log.v("Necesidades", totalNecesidades.toString())

            var totalHormiga = 0.0
            for (monto in hormiga) {
                totalHormiga += monto.valor
            }
            Log.v("Hormiga", totalHormiga.toString())

            var totalOcio = 0.0
            for (monto in ocio) {
                totalOcio += monto.valor
            }
            Log.v("Ocio", totalOcio.toString())

            var totalisimo = 0.0
            for (monto in totalis) {
                totalisimo += monto.valor
            }
            Log.v("GRAN TOTAL", totalisimo.toString())

            val percentAlimento: Float = totalAlimentos.toFloat() / totalisimo.toFloat()
            val percentHogar: Float = totalHogar.toFloat() / totalisimo.toFloat()
            val percentBienestar: Float = totalBienestar.toFloat() / totalisimo.toFloat()
            val percentNecesidades: Float = totalNecesidades.toFloat() / totalisimo.toFloat()
            val percentHormiga: Float = totalHormiga.toFloat() / totalisimo.toFloat()
            val percentOcio: Float = totalOcio.toFloat() / totalisimo.toFloat()

            val entries = listOf(
                PieEntry(percentAlimento, "Alimentos"),
                PieEntry(percentHogar, "Hogar"),
                PieEntry(percentBienestar, "Bienestar"),
                PieEntry(percentNecesidades, "Otras necesidades"),
                PieEntry(percentHormiga, "Hormiga"),
                PieEntry(percentOcio, "Ocio y demás")
            )

            val dataSet = PieDataSet(entries, "Gastos")
            dataSet.colors = colors

            val data = PieData(dataSet)
            binding.PieChart.data = data

            binding.PieChart.centerText = totalisimo.toString()
            binding.PieChart.setCenterTextSize(24f)
            binding.PieChart.setCenterTextColor(R.color.black)
            binding.PieChart.description.isEnabled = false
            binding.PieChart.legend.isEnabled = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addWithSwitchOn = indexadd.newInstance(true)
        val addWithSwitchOff = indexadd.newInstance(false)
        binding.AgregarIngresoButton.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.ViewContainer, addWithSwitchOn).addToBackStack(null).commit()
        }
        binding.AgregarGastoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.ViewContainer, addWithSwitchOff).addToBackStack(null).commit()
        }

        lifecycleScope.launch() {
            setupPieChart()
        }



        /*
                val progressBar = binding.GraficoPastel
                val progressDrawable = progressBar.indeterminateDrawable as AnimationDrawable
                progressDrawable.start()
        */

        // MOSTRAR DATOS de las tablas ingresosgastos y monto
        // Vincular botones a otras vistas con viewbinding para acceder a los cruds de monto
        // Crear las funciones para autoincremento de ciertos datos como días o ingresos
        // Cruds de monto para la modificación interna de los datos de la tabla ingresosgastos
        // Trabajo SERIO de frontend
        // Traer datos de otras vistas porque es el index xd (fechas del historial o deudas de planes de ahorro)
    }
}