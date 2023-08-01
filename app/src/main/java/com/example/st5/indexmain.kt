package com.example.st5

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentIndexmainBinding
import com.example.st5.models.Monto
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.polyak.iconswitch.IconSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*

class indexmain : Fragment(), OnChartValueSelectedListener {
    private lateinit var binding: FragmentIndexmainBinding
    private val colorsG: MutableList<Int> = mutableListOf()
    private val colorsI: MutableList<Int> = mutableListOf()

    private val colorsGDraw: MutableList<Int> = mutableListOf()
    private val colorsIDraw: MutableList<Int> = mutableListOf()

    private val textG: MutableList<String> = mutableListOf()
    private val textI: MutableList<String> = mutableListOf()

    private val numI: MutableList<Float?> = mutableListOf()
    private val numG: MutableList<Float?> = mutableListOf()

    private var medidaT: Double = 0.0

    private var switchVal = false
    private var lista: Fragment = indexGastosList()

    private lateinit var fastable: List<Monto>

    private var mutableEtiquetas: MutableList<String> = mutableListOf()
    private var mutableIds: MutableList<Long> = mutableListOf()
    private var mutableColores: MutableList<Int> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAlarm()
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_index2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_index)
            }

            Log.i("MODO", isDarkMode.toString())
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
        binding = FragmentIndexmainBinding.inflate(inflater, container, false)
        binding.SultanOfSwing.checked = IconSwitch.Checked.RIGHT
        binding.displayCheck.animate()
            .alpha(0f)
            .translationX(-60f)
            .start()
        return binding.root
    }

    private fun setupColors() {
        colorsG.add(ContextCompat.getColor(requireContext(), R.color.V1))
        colorsG.add(ContextCompat.getColor(requireContext(), R.color.V2))
        colorsG.add(ContextCompat.getColor(requireContext(), R.color.V3))
        colorsG.add(ContextCompat.getColor(requireContext(), R.color.V4))
        colorsG.add(ContextCompat.getColor(requireContext(), R.color.V5))
        colorsG.add(ContextCompat.getColor(requireContext(), R.color.V6))
        colorsG.add(ContextCompat.getColor(requireContext(), R.color.V7))
        colorsG.add(ContextCompat.getColor(requireContext(), R.color.V8))

        colorsI.add(ContextCompat.getColor(requireContext(), R.color.V9))
        colorsI.add(ContextCompat.getColor(requireContext(), R.color.V10))
        colorsI.add(ContextCompat.getColor(requireContext(), R.color.V11))
        colorsI.add(ContextCompat.getColor(requireContext(), R.color.V12))
        colorsI.add(ContextCompat.getColor(requireContext(), R.color.V13))
        colorsI.add(ContextCompat.getColor(requireContext(), R.color.V14))
        colorsI.add(ContextCompat.getColor(requireContext(), R.color.V7))
        colorsI.add(ContextCompat.getColor(requireContext(), R.color.V8))

        colorsGDraw.add(R.drawable.tty3)
        colorsGDraw.add(R.drawable.ttb2)
        colorsGDraw.add(R.drawable.ttg2)
        colorsGDraw.add(R.drawable.ttb0)
        colorsGDraw.add(R.drawable.tto1)
        colorsGDraw.add(R.drawable.ttr1)
        colorsGDraw.add(R.drawable.ttp1)
        colorsGDraw.add(R.drawable.ttr0)

        colorsIDraw.add(R.drawable.ttg4)
        colorsIDraw.add(R.drawable.ttb4)
        colorsIDraw.add(R.drawable.ttr2)
        colorsIDraw.add(R.drawable.tto4)
        colorsIDraw.add(R.drawable.tty2)
        colorsIDraw.add(R.drawable.ttp2)
        colorsIDraw.add(R.drawable.ttp1)
        colorsIDraw.add(R.drawable.ttr0)

        textG.add("Alimentos")
        textG.add("Hogar")
        textG.add("Bienestar")
        textG.add("Necesidades")
        textG.add("Gastos Hormiga")
        textG.add("Ocio y demás")
        textG.add("Obsequio")
        textG.add("Deuda")

        textI.add("Salarios")
        textI.add("Ventas")
        textI.add("Becas")
        textI.add("Pensiones")
        textI.add("Manutención")
        textI.add("Ingresos Pasivos")
        textI.add("Regalos")
        textI.add("Préstamos")
    }

    private fun isNotZero(value: Double?): Boolean {
        return ((value != 0.0) || (value != -0.0))
    }

    private suspend fun getLabels() {
        withContext(Dispatchers.IO) {
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val max = labelsDao.getMaxLabel()

            for (i in 1..max) {
                mutableIds.add(labelsDao.getIdLabel(i))
                mutableEtiquetas.add(labelsDao.getPlabel(i))
                mutableColores.add(labelsDao.getColor(i))
                Log.v("leibels", "${labelsDao.getIdLabel(i)}, ${labelsDao.getPlabel(i)}, $max")
            }
            Log.v("idl", "$mutableIds")
            Log.v("plabel", "$mutableEtiquetas")
            Log.v("color", "$mutableColores")
        }
    }

    //region PIECHARTS
    private suspend fun setupPieChartG() {
        setupColors()
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

            //TODO poner todo esto en un foreach label
            val alimentos = montoDao.getAlimentos()
            val hogar = montoDao.getHogar()
            val bienestar = montoDao.getBienestar()
            val necesidades = montoDao.getNecesidades()
            val hormiga = montoDao.getHormiga()
            val ocio = montoDao.getOcio()
            val obsequios = montoDao.getObsequios()
            val deudas = montoDao.getDeudas()

            val totalI = montoDao.getIngresos()
            val totalG = montoDao.getGastos()
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

            var totalObsequios = 0.0
            for (monto in obsequios) {
                totalObsequios += monto.valor
            }
            Log.v("Obsequios", totalObsequios.toString())

            var totalDeudas = 0.0
            for (monto in deudas) {
                totalDeudas += monto.valor
            }
            Log.v("Deudas", totalDeudas.toString())


            var totalIngresos = 0.0
            for (monto in totalI) {
                totalIngresos += monto.valor
            }
            Log.v("INGRESOS", totalI.toString())

            var totalGastos = 0.0
            for (monto in totalG) {
                totalGastos += monto.valor
            }
            Log.v("GASTOS", totalG.toString())

            var totalisimo = 0.0
            for (monto in totalis) {
                totalisimo += monto.valor
            }
            Log.v("GRAN TOTAL", totalisimo.toString())

            var percentAlimento: Float? = 0f
            var percentHogar: Float? = 0f
            var percentBienestar: Float? = 0f
            var percentNecesidades: Float? = 0f
            var percentHormiga: Float? = 0f
            var percentOcio: Float? = 0f
            var percentObsequios: Float? = 0f
            var percentDeudas: Float? = 0f

            val decimalFormat = DecimalFormat("#.##")

            if (isNotZero(totalAlimentos)) {
                percentAlimento = decimalFormat.format((totalAlimentos.toFloat() / totalGastos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalHogar)) {
                percentHogar = decimalFormat.format((totalHogar.toFloat() / totalGastos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalBienestar)) {
                percentBienestar = decimalFormat.format((totalBienestar.toFloat() / totalGastos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalNecesidades)) {
                percentNecesidades = decimalFormat.format((totalNecesidades.toFloat() / totalGastos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalHormiga)) {
                percentHormiga = decimalFormat.format((totalHormiga.toFloat() / totalGastos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalOcio)) {
                percentOcio = decimalFormat.format((totalOcio.toFloat() / totalGastos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalObsequios)) {
                percentObsequios = decimalFormat.format((totalObsequios.toFloat() / totalGastos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalDeudas)) {
                percentDeudas = decimalFormat.format((totalDeudas.toFloat() / totalGastos.toFloat()) * 100).toFloat()
            }

            numG.add(percentAlimento)
            numG.add(percentHogar)
            numG.add(percentBienestar)
            numG.add(percentNecesidades)
            numG.add(percentHormiga)
            numG.add(percentOcio)
            numG.add(percentObsequios)
            numG.add(percentDeudas)

            val chartAlimentos = totalAlimentos.toFloat() * -1
            val chartHogar = totalHogar.toFloat() * -1
            val chartBienestar: Float = totalBienestar.toFloat() * -1
            val chartNecesidades = totalNecesidades.toFloat() * -1
            val chartHormiga = totalHormiga.toFloat() * -1
            val chartOcio = totalOcio.toFloat() * -1
            val chartObsequios = totalObsequios.toFloat() * -1
            val chartDeudas = totalDeudas.toFloat() * -1

            val entries = listOf(
                PieEntry(chartAlimentos),
                PieEntry(chartHogar),
                PieEntry(chartBienestar),
                PieEntry(chartNecesidades),
                PieEntry(chartHormiga),
                PieEntry(chartOcio),
                PieEntry(chartObsequios),
                PieEntry(chartDeudas)
            )

            val dataSet = PieDataSet(entries, "Gastos")
            dataSet.colors = colorsG

            val data = PieData(dataSet)
            binding.PieChart.data = data

            binding.PieChart.centerText = "$totalIngresos$ - $totalGastos$ = $totalisimo$"
            binding.PieChart.setCenterTextSize(24f)
            binding.PieChart.setCenterTextColor(R.color.white)
            binding.PieChart.description.isEnabled = false
            binding.PieChart.legend.isEnabled = false

            medidaT = decimalFormat.format((ingresosGastosDao.checkSummaryG() / (ingresosGastosDao.checkSummaryI() + ingresosGastosDao.checkSummaryG())) * 10).toDouble()
        }
    }

    private suspend fun setupPieChartI() {
        setupColors()
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

            val salarios = montoDao.getSalarios()
            val irregulares = montoDao.getIrregulares()
            val becas = montoDao.getBecas()
            val pensiones = montoDao.getPensiones()
            val manutencion = montoDao.getManutencion()
            val pasivos = montoDao.getPasivos()
            val regalos = montoDao.getRegalos()
            val prestamos = montoDao.getPrestamos()

            val totalI = montoDao.getIngresos()
            val totalG = montoDao.getGastos()
            val totalis = montoDao.getMonto()

            var totalSalarios = 0.0
            for (monto in salarios) {
                totalSalarios += monto.valor
            }
            Log.v("Salarios", totalSalarios.toString())

            var totalIrregulares = 0.0
            for (monto in irregulares) {
                totalIrregulares += monto.valor
            }
            Log.v("Irregulares", totalIrregulares.toString())

            var totalBecas = 0.0
            for (monto in becas) {
                totalBecas += monto.valor
            }
            Log.v("Becas", totalBecas.toString())

            var totalPensiones = 0.0
            for (monto in pensiones) {
                totalPensiones += monto.valor
            }
            Log.v("Pensiones", totalPensiones.toString())

            var totalManutencion = 0.0
            for (monto in manutencion) {
                totalManutencion += monto.valor
            }
            Log.v("Manutencion", totalManutencion.toString())

            var totalPasivos = 0.0
            for (monto in pasivos) {
                totalPasivos += monto.valor
            }
            Log.v("Pasivos", totalPasivos.toString())

            var totalRegalos = 0.0
            for (monto in regalos) {
                totalRegalos += monto.valor
            }
            Log.v("Regalos", totalRegalos.toString())

            var totalPrestamos = 0.0
            for (monto in prestamos) {
                totalPrestamos += monto.valor
            }
            Log.v("Prestamos", totalPrestamos.toString())


            var totalIngresos = 0.0
            for (monto in totalI) {
                totalIngresos += monto.valor
            }
            Log.v("INGRESOS", totalI.toString())

            var totalGastos = 0.0
            for (monto in totalG) {
                totalGastos += monto.valor
            }
            Log.v("GASTOS", totalG.toString())

            var totalisimo = 0.0
            for (monto in totalis) {
                totalisimo += monto.valor
            }
            Log.v("GRAN TOTAL", totalisimo.toString())

            var percentSalarios: Float? = 0f
            var percentIrregulares: Float? = 0f
            var percentBecas: Float? = 0f
            var percentPensiones: Float? = 0f
            var percentManutencion: Float? = 0f
            var percentPasivos: Float? = 0f
            var percentRegalos: Float? = 0f
            var percentPrestamos: Float? = 0f

            val decimalFormat = DecimalFormat("#.##")

            if (isNotZero(totalSalarios)) {
                percentSalarios = decimalFormat.format((totalSalarios.toFloat() / totalIngresos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalIrregulares)) {
                percentIrregulares = decimalFormat.format((totalIrregulares.toFloat() / totalIngresos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalBecas)) {
                percentBecas = decimalFormat.format((totalBecas.toFloat() / totalIngresos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalPensiones)) {
                percentPensiones = decimalFormat.format((totalPensiones.toFloat() / totalIngresos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalManutencion)) {
                percentManutencion = decimalFormat.format((totalManutencion.toFloat() / totalIngresos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalPasivos)) {
                percentPasivos = decimalFormat.format((totalPasivos.toFloat() / totalIngresos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalRegalos)) {
                percentRegalos = decimalFormat.format((totalRegalos.toFloat() / totalIngresos.toFloat()) * 100).toFloat()
            }
            if (isNotZero(totalPrestamos)) {
                percentPrestamos = decimalFormat.format((totalPrestamos.toFloat() / totalIngresos.toFloat()) * 100).toFloat()
            }


            numI.add(percentSalarios)
            numI.add(percentIrregulares)
            numI.add(percentBecas)
            numI.add(percentPensiones)
            numI.add(percentManutencion)
            numI.add(percentPasivos)
            numI.add(percentRegalos)
            numI.add(percentPrestamos)

            val entries = listOf(
                PieEntry(totalSalarios.toFloat()),
                PieEntry(totalIrregulares.toFloat()),
                PieEntry(totalBecas.toFloat()),
                PieEntry(totalPensiones.toFloat()),
                PieEntry(totalManutencion.toFloat()),
                PieEntry(totalPasivos.toFloat()),
                PieEntry(totalRegalos.toFloat()),
                PieEntry(totalPrestamos.toFloat())
            )

            val dataSet = PieDataSet(entries, "Gastos")
            dataSet.colors = colorsI

            val data = PieData(dataSet)
            val chart = binding.PieChart

            chart.centerText = "$totalIngresos$ - $totalGastos$ = $totalisimo$"
            chart.setCenterTextSize(24f)
            chart.holeRadius = 48f
            chart.setCenterTextColor(R.color.white)
            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.isRotationEnabled = true
            chart.absoluteAngles

            chart.data = data

            medidaT = decimalFormat.format((ingresosGastosDao.checkSummaryG() / (ingresosGastosDao.checkSummaryI() + ingresosGastosDao.checkSummaryG())) * 10).toDouble()
        }
    }

    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addWithSwitchOn = indexadd.newInstance(true)
        val addWithSwitchOff = indexadd.newInstance(false)

        lifecycleScope.launch {
            procesarMontos()

            fastable = fastget()
            binding.displayCheck.adapter = MontoAdapter(fastable)
            binding.displayCheck.animate()
                .alpha(1f)
                .translationX(0f)
                .translationZ(0f)
                .setDuration(300)
                .start()
            val mT = "$medidaT%"
            binding.Medidor.text = mT
        }
        lifecycleScope.launch {
            gi(switchVal)
            delay(500)
            binding.GraficoPastel.alpha = 0f
        }

        binding.MedidorDeAhorroButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, lista).addToBackStack(null).commit()

        }

        binding.Calendario.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, indexPorPagar()).addToBackStack(null).commit()
        }

        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, Configuracion()).addToBackStack(null).commit()
        }

        binding.PieChart.setOnChartValueSelectedListener(pieChartOnChartValueSelectedListener())

        binding.SultanOfSwing.setCheckedChangeListener {
            switchVal = when (binding.SultanOfSwing.checked) {
                IconSwitch.Checked.LEFT -> {
                    true
                }
                IconSwitch.Checked.RIGHT -> {
                    false
                }
                else -> {
                    false
                }
            }
            lifecycleScope.launch {
                gi(switchVal)
            }
        }

        binding.AgregarIngresoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, addWithSwitchOn).addToBackStack(null).commit()
        }
        binding.AgregarGastoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, addWithSwitchOff).addToBackStack(null).commit()
        }

        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, Configuracion()).addToBackStack(null).commit()
        }

        /*
                val progressBar = binding.GraficoPastel
                val progressDrawable = progressBar.indeterminateDrawable as AnimationDrawable
                progressDrawable.start()
        */
    }

    @SuppressLint("SetTextI18n")
    private suspend fun fastget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val fechaActual = LocalDate.now().toString()
            val today: Int = fechaActual.replace("-", "").toInt()

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val truefecha = formatoFecha.parse(fechaActual)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dom = calendar.get(Calendar.DAY_OF_MONTH)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = 100
            when (w) {
                1 -> dow = 47
                2 -> dow = 41
                3 -> dow = 42
                4 -> dow = 43
                5 -> dow = 44
                6 -> dow = 45
                7 -> dow = 46
            }

            val yyy = calendar.get(Calendar.YEAR)
            val mesesito = when (calendar.get(Calendar.MONTH)) {
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
            val semanita = when (dow) {
                47 -> "Domingo"
                41 -> "Lunes"
                42 -> "Martes"
                43 -> "Miércoles"
                44 -> "Jueves"
                45 -> "Viernes"
                46 -> "Sábado"
                else -> ""
            }

            binding.Calendario.text = "$semanita $dom de $mesesito del $yyy"
            val addd: Int = today

            Log.i("DOM", dom.toString())
            Log.i("DOW", dow.toString())

            Log.i("todayyyy", today.toString())

            fastable = montoDao.getGXFecha(today, dom, dow, 100, addd)
            Log.i("ALL TODOLIST", fastable.toString())
        }
        return fastable
    }

    private fun gi(switchVal: Boolean) {
        Log.v("masomenos", switchVal.toString())
        if (!switchVal) {
            lifecycleScope.launch {
                setupPieChartG()
                lista = indexGastosList()
                binding.PieChart.performClick()
                binding.PieChart.animateY(1200, Easing.EaseInOutQuad)
                binding.MedidorDeAhorroButton.setBackgroundResource(R.drawable.ttg)
                binding.searchforlabel.text = null
                binding.searchforlabel.hint = "Gastos"
            }
        } else {
            lifecycleScope.launch {
                setupPieChartI()
                lista = indexIngresosList()
                binding.PieChart.performClick()
                binding.PieChart.animateY(1200, Easing.EaseInOutQuad)
                binding.MedidorDeAhorroButton.setBackgroundResource(R.drawable.tti)
                binding.searchforlabel.text = null
                binding.searchforlabel.hint = "Ingresos"
            }
        }
    }

    inner class pieChartOnChartValueSelectedListener : OnChartValueSelectedListener {

        @SuppressLint("SetTextI18n")
        override fun onValueSelected(e: Entry, h: Highlight) {
            Log.i("VAL SELECTED", "Value: " + e.y + ", index: " + h.x.toInt())

            val chart = binding.PieChart

            chart.highlightValue(h)

            lista = if (!switchVal) {
                binding.MedidorDeAhorroButton.setBackgroundResource(colorsGDraw[h.x.toInt()])
                binding.Medidor.text = numG[h.x.toInt()].toString() + "%"
                binding.searchforlabel.text = textG[h.x.toInt()]
                val iglinstance = indexGastosList.labelSearch(h.x.toInt())
                iglinstance
            } else {
                binding.MedidorDeAhorroButton.setBackgroundResource(colorsIDraw[h.x.toInt()])
                binding.Medidor.text = numI[h.x.toInt()].toString() + "%"
                binding.searchforlabel.text = textI[h.x.toInt()]
                val iilinstance = indexIngresosList.labelSearch(h.x.toInt())
                iilinstance
            }
        }

        override fun onNothingSelected() {
            Log.i("PieChart", "nothing selected")
            lista = if (!switchVal) {
                binding.MedidorDeAhorroButton.setBackgroundResource(R.drawable.ttg)
                binding.searchforlabel.text = null
                binding.searchforlabel.hint = "Gastos"
                indexGastosList()
            } else {
                binding.MedidorDeAhorroButton.setBackgroundResource(R.drawable.tti)
                binding.searchforlabel.text = null
                binding.searchforlabel.hint = "Ingresos"
                indexIngresosList()
            }
        }
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        Log.i("VAL SELECTED", "Value: " + e.y + ", index: " + h.x)
    }

    override fun onNothingSelected() {
        Log.i("PieChart", "nothing selected")
    }

    /*
    =======================================================================================================================

           ####  #####   ####    ##   ##  ##     ##  ####     #####         #####   ##        ###    ##     ##   #####
          ##     ##     ##       ##   ##  ####   ##  ##  ##  ##   ##        ##  ##  ##       ## ##   ####   ##  ##   ##
           ###   #####  ##  ###  ##   ##  ##  ## ##  ##  ##  ##   ##        #####   ##      ##   ##  ##  ## ##  ##   ##
             ##  ##     ##   ##  ##   ##  ##    ###  ##  ##  ##   ##        ##      ##      #######  ##    ###  ##   ##
          ####   #####   ####     #####   ##     ##  ####     #####         ##      ######  ##   ##  ##     ##   #####

    =======================================================================================================================
    */
    private suspend fun procesarMontos() {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val ingresoGastoDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()
            val assetsDao = Stlite.getInstance(requireContext()).getAssetsDao()

            val fechaActual = LocalDate.now().toString()
            Log.d("HOY", fechaActual)
            val today: Int = fechaActual.replace("-", "").toInt()
            val prev = assetsDao.getLastProcess()

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val truefecha = formatoFecha.parse(fechaActual)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dom = calendar.get(Calendar.DAY_OF_MONTH)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = 100
            when (w) {
                1 -> dow = 47
                2 -> dow = 41
                3 -> dow = 42
                4 -> dow = 43
                5 -> dow = 44
                6 -> dow = 45
                7 -> dow = 46
            }

            val addd: Int = today

            Log.i("DOM", dom.toString())
            Log.i("DOW", dow.toString())

            Log.i("todayyyy", today.toString())
            Log.i("prevvvvv", prev.toString())

            val montos = montoDao.getMontoXFecha(today, dom, dow, 100, addd)

            if (prev != today) {
                for (monto in montos) {
                    val totalIngresos = ingresoGastoDao.checkSummaryI()

                    Log.i("MONTO PROCESADO", monto.toString())
                    val weekMonto = monto.fecha
                    Log.v("wek", weekMonto.toString())

                    if (monto.valor > 0) {
                        ingresoGastoDao.updateSummaryI(
                            monto.iduser.toInt(),
                            totalIngresos + monto.valor
                        )
                        monto.veces = monto.veces?.plus(1)
                        montoDao.updateMonto(monto)
                    } else {
                        val toCheckMonto = Monto(
                            idmonto = monto.idmonto,
                            iduser = monto.iduser,
                            concepto = monto.concepto,
                            valor = monto.valor,
                            fecha = monto.fecha,
                            frecuencia = monto.frecuencia,
                            etiqueta = monto.etiqueta,
                            interes = monto.interes,
                            veces = monto.veces,
                            estado = 0,
                            adddate = monto.adddate
                        )
                        montoDao.updateMonto(toCheckMonto)
                    }
                }
            }
            assetsDao.updateLastprocess(today)
        }
    }

    private fun setupAlarm() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 3)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val intent = Intent(requireContext(), Alarma::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, FLAG_IMMUTABLE)

        val alarmManager = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private inner class MontoAdapter(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val conceptoTextView: TextView,
            val valorTextView: TextView,
            val fechaTextView: TextView,
            val updateM: Button,
            val checkM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_porpagar, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.pNombre)
            val valorTextView = itemView.findViewById<TextView>(R.id.pValor)
            val fechaTextView = itemView.findViewById<TextView>(R.id.pFecha)
            val updateM = itemView.findViewById<Button>(R.id.editP)
            val checkM = itemView.findViewById<Button>(R.id.checkP)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                fechaTextView,
                updateM,
                checkM
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = monto.valor.toString()
            holder.fechaTextView.text = monto.fecha.toString()
            val upup = indexmontoupdate.sendMonto(
                monto.idmonto,
                monto.concepto,
                monto.valor,
                monto.fecha,
                monto.frecuencia,
                monto.etiqueta,
                monto.interes,
                monto.veces,
                monto.adddate
            )
            holder.updateM.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.index_container, upup).addToBackStack(null).commit()
            }
            holder.checkM.setOnClickListener {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres marcar el gasto ${monto.concepto} como pagado?")
                    .setPositiveButton("Eliminar") { dialog, _ ->

                        Log.v("Id del monto actualizado", monto.idmonto.toString())
                        Log.v("Concepto", monto.concepto)
                        Log.v("Valor", monto.valor.toString())
                        Log.v("Fecha", monto.fecha.toString())
                        Log.v("Frecuencia", monto.frecuencia.toString())
                        Log.v("Etiqueta", monto.etiqueta.toString())
                        Log.v("Interes", monto.interes.toString())
                        Log.v("Veces", monto.veces.toString())

                        lifecycleScope.launch {
                            montoCheck(
                                monto.idmonto,
                                monto.concepto,
                                monto.valor,
                                monto.fecha,
                                monto.frecuencia,
                                monto.etiqueta,
                                monto.interes,
                                monto.veces,
                                monto.adddate
                            )
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.index_container, indexmain()).addToBackStack(null).commit()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                confirmDialog.show()
            }
        }

        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }

        private suspend fun montoCheck(
            idmonto: Long,
            concepto: String,
            valor: Double,
            fecha: Int?,
            frecuencia: Int?,
            etiqueta: Int,
            interes: Double?,
            veces: Long?,
            adddate: Int
        ) {
            withContext(Dispatchers.IO) {
                val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                val ingresoGastoDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

                var nv: Long? = 1
                if (veces != null)
                    nv = veces + 1

                val iduser = usuarioDao.checkId().toLong()
                val montoPresionado = Monto(
                    idmonto = idmonto,
                    iduser = iduser,
                    concepto = concepto,
                    valor = valor,
                    fecha = fecha,
                    frecuencia = frecuencia,
                    etiqueta = etiqueta,
                    interes = interes,
                    veces = nv,
                    estado = 1,
                    adddate = adddate
                )

                val totalGastos = ingresoGastoDao.checkSummaryG()

                ingresoGastoDao.updateSummaryG(
                    montoPresionado.iduser.toInt(),
                    totalGastos + montoPresionado.valor
                )

                montoDao.updateMonto(montoPresionado)
                val montos = montoDao.getMonto()
                Log.i("ALL MONTOS", montos.toString())
                parentFragmentManager.beginTransaction()
                    .replace(R.id.index_container, indexmain()).addToBackStack(null).commit()
            }
        }
    }
}