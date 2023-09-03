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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
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
import java.time.format.DateTimeFormatter
import java.util.*

class indexmain : Fragment(), OnChartValueSelectedListener {
    private lateinit var binding: FragmentIndexmainBinding
    private val colorsI: MutableList<Int> = mutableListOf()

    private val colorsGDraw: MutableList<Int> = mutableListOf()
    private val colorsIDraw: MutableList<Int> = mutableListOf()

    private val textI: MutableList<String> = mutableListOf()

    private val numI: MutableList<Float?> = mutableListOf()
    private val numG: MutableList<Float?> = mutableListOf()

    private var medidaT: Double = 0.0
    private var rango: Long = 15L

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
        lifecycleScope.launch {
            getLabels()

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

            textI.add("Salarios")
            textI.add("Ventas")
            textI.add("Becas")
            textI.add("Pensiones")
            textI.add("Manutención")
            textI.add("Ingresos Pasivos")
            textI.add("Regalos")
            textI.add("Préstamos")
        }
    }

    private suspend fun getLabels() {
        withContext(Dispatchers.IO) {
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val max = labelsDao.getMaxLabel()

            for (i in 1..max) {
                if (labelsDao.getPlabel(i) != ""){
                    mutableIds.add(labelsDao.getIdLabel(i))
                    mutableEtiquetas.add(labelsDao.getPlabel(i))
                    mutableColores.add(labelsDao.getColor(i))

                    Log.v("leibels", "${labelsDao.getIdLabel(i)}, ${labelsDao.getPlabel(i)}, $max")
                }
            }
            Log.v("idl", "$mutableIds")
            Log.v("plabel", "$mutableEtiquetas")
            Log.v("color", "$mutableColores")
        }
    }

    private fun sequenceGet(s: String, t: Int, f: Int?): Int {
        val numeros = s.split(".").map { it.toIntOrNull() }
        val frec = maxOf(f ?: 1, 1)
        val last = maxOf(t/frec, 1)
        val ultimosXNumeros = numeros.filterNotNull().takeLast(last)
        return ultimosXNumeros.sum()
    }

    //region PIECHARTS
    private suspend fun setupPieChartG(range: Long) {
        setupColors()
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()
            val labelsDao = Stlite.getInstance((requireContext())).getLabelsDao()

            val maxLabels = labelsDao.getMaxLabel()
            val expenses = mutableListOf<List<Monto>>()

            val fechaActual = LocalDate.now().toString()
            val then = LocalDate.now().minusDays(range)
            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val truefecha = formatoFecha.parse(fechaActual)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            for (i in 0 until range) {
                val currentDate = then.plusDays(i)
                val todayInt = currentDate.format(DateTimeFormatter.BASIC_ISO_DATE).toInt()
                val dom = currentDate.dayOfMonth
                val w = currentDate.dayOfWeek.value
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

                Log.i("FECHAAA", "$todayInt")
                for (j in 0..maxLabels) {
                    if (montoDao.getGR(todayInt, dom, dow, 100, j, todayInt).toString() != "[]") {
                        expenses.add(montoDao.getGR(todayInt, dom, dow, 100, j, todayInt))
                    }
                }
            }

            for (j in 1..maxLabels) { //Para que no salte colores
                expenses.add(listOf(Monto(idmonto=0, iduser=0, concepto="", valor=0.0, valorfinal=0.0, fecha=0, frecuencia=0, etiqueta=j, interes=0.0, veces=0L, estado=0, adddate=0, enddate = 0, cooldown = 0, sequence = "0.")))
            }

            Log.v("EXPENSES", expenses.toString())
            val entries = mutableListOf<PieEntry>()

            val totalIngresos = ingresosGastosDao.checkSummaryI()
            Log.v("INGRESOS", totalIngresos.toString())
            val totalGastos = ingresosGastosDao.checkSummaryG()
            Log.v("GASTOS", totalGastos.toString())
            val totalisimo = totalIngresos - totalGastos
            Log.v("GRAN TOTAL", totalisimo.toString())

            val decimalFormat = DecimalFormat("#.##")
            val etiquetaSumMap = mutableMapOf<Long, Double>()
            for (i in 0 until expenses.size) {
                for (monto in expenses[i]) {
                    val etiqueta = monto.etiqueta.toLong()
                    val times = sequenceGet(monto.sequence, range.toInt(), monto.frecuencia)
                    val current = monto.valor * times

                    if (etiquetaSumMap.containsKey(etiqueta)) {
                        val currentSum = etiquetaSumMap[etiqueta] ?: 0.0
                        etiquetaSumMap[etiqueta] = currentSum + current
                    } else {
                        etiquetaSumMap[etiqueta] = current
                    }
                }
            }

            for ((_, sum) in etiquetaSumMap) {
                val percentI = if (totalGastos != 0.0) {
                    decimalFormat.format((sum.toFloat() * 100 / totalGastos.toFloat())).toFloat()
                } else {
                    0F
                }
                numG.add(percentI)
                entries.add(PieEntry(sum.toFloat()))
            }

            val dataSet = PieDataSet(entries, "Gastos")
            dataSet.colors = mutableColores

            val data = PieData(dataSet)
            binding.PieChart.data = data

            binding.PieChart.centerText = "$totalIngresos$ - $totalGastos$ = $totalisimo$"
            binding.PieChart.setCenterTextSize(24f)
            binding.PieChart.setCenterTextColor(R.color.white)
            binding.PieChart.description.isEnabled = false
            binding.PieChart.legend.isEnabled = false
        }
    }

    private suspend fun setupPieChartI(range: Long) {
        setupColors()
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

            val totalI = montoDao.getIngresos()
            val totalG = montoDao.getGastos()
            val incomes = mutableListOf<List<Monto>>()

            val fechaActual = LocalDate.now().toString()
            val then = LocalDate.now().minusDays(range)
            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val truefecha = formatoFecha.parse(fechaActual)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val totalIngresos = ingresosGastosDao.checkSummaryI()
            Log.v("INGRESOS", totalI.toString())
            val totalGastos = ingresosGastosDao.checkSummaryG()
            Log.v("GASTOS", totalG.toString())
            val totalisimo = totalIngresos - totalGastos
            Log.v("GRAN TOTAL", totalisimo.toString())

            val percentI = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
            val decimalFormat = DecimalFormat("#.##")

            for (i in 0 until range) {
                val currentDate = then.plusDays(i)
                val todayInt = currentDate.format(DateTimeFormatter.BASIC_ISO_DATE).toInt()
                val dom = currentDate.dayOfMonth
                val w = currentDate.dayOfWeek.value
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

                for (j in 10000..10007) {
                    if (montoDao.getGR(todayInt, dom, dow, 100, j, todayInt).toString() != "[]") {
                        incomes.add(montoDao.getGR(todayInt, dom, dow, 100, j, todayInt))
                    } else {
                        incomes.add(listOf(Monto(idmonto=0, iduser=0, concepto="", valor=0.0, valorfinal=0.0, fecha=0, frecuencia=0, etiqueta=j, interes=0.0, veces=0L, estado=0, adddate=0, enddate = 0, cooldown = 0)))
                    }
                }
            }

            val tI = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            val entries = mutableListOf(PieEntry(0f), PieEntry(0f), PieEntry(0f), PieEntry(0f), PieEntry(0f), PieEntry(0f), PieEntry(0f), PieEntry(0f), PieEntry(0f))

            for (i in incomes.indices) {
                for (monto in incomes[i]) {
                    tI[i] += monto.valor
                    Log.v("Ingreso $i", tI[i].toString())
                    if (tI[i] != 0.0 && totalIngresos != 0.0) {
                        percentI[i] =
                            decimalFormat.format((tI[i].toFloat() / totalIngresos.toFloat()) * 100)
                                .toFloat()
                    }
                    numI.add(percentI[i])
                    entries[i] = PieEntry(tI[i].toFloat())
                }
            }

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
            gi(switchVal, rango)
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

        binding.Mandados.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, indexmandados()).addToBackStack(null).commit()
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
                gi(switchVal, rango)
            }
        }

        binding.RangoSeekbar.min = 1
        binding.RangoSeekbar.max = 12
        binding.RangoSeekbar.progress = 8
        binding.RangoSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val range = when (progress) {
                    1, 2, 3, 4, 5, 6, 7 -> progress
                    8 -> 15
                    9 -> 30
                    10 -> 60
                    11 -> 180
                    12 -> 365
                    else -> rango
                }
                binding.Rango.setText(range.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.Rango.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty()) {
                    rango = text.toLong()
                    lifecycleScope.launch {
                        gi(switchVal, rango)
                    }
                }
            }
        })

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

    private fun gi(switchVal: Boolean, range: Long) {
        Log.v("masomenos", switchVal.toString())
        if (!switchVal) {
            lifecycleScope.launch {
                setupPieChartG(range)
                lista = indexGastosList()
                binding.PieChart.performClick()
                binding.PieChart.animateY(1200, Easing.EaseInOutQuad)
                binding.MedidorDeAhorroButton.setBackgroundResource(R.drawable.ttg)
                binding.searchforlabel.text = null
                binding.searchforlabel.hint = "Gastos"
            }
        } else {
            lifecycleScope.launch {
                setupPieChartI(range)
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
                binding.searchforlabel.text = mutableEtiquetas[h.x.toInt()]
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
                    if (monto.cooldown == 0) {
                        val totalIngresos = ingresoGastoDao.checkSummaryI()

                        Log.i("MONTO PROCESADO", monto.toString())
                        val weekMonto = monto.fecha
                        Log.v("wek", weekMonto.toString())

                        if (monto.etiqueta > 10000) {
                            ingresoGastoDao.updateSummaryI(monto.iduser.toInt(), totalIngresos + monto.valor)
                            monto.veces = monto.veces?.plus(1)
                            monto.sequence = monto.sequence + "1."
                            montoDao.updateMonto(monto)
                        } else {
                            var status = 0
                            var cooldown = 0
                            val sequence = monto.sequence + "0."
                            when (monto.estado) {
                                1 -> status = 0
                                4 -> status = 3
                                6 -> status = 5
                                9 -> status = 8
                            }
                            when (monto.frecuencia) {
                                14 -> cooldown = 1
                                61 -> cooldown = 1
                                91 -> cooldown = 2
                                122 -> cooldown = 3
                                183 -> cooldown = 5
                                365 -> cooldown = 11
                            }
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
                                estado = status,
                                adddate = monto.adddate,
                                enddate = monto.enddate,
                                cooldown = cooldown,
                                sequence = sequence
                            )
                            montoDao.updateMonto(toCheckMonto)
                        }
                    } else {
                        val newcool = monto.cooldown + 1

                        val toMeltMonto = Monto(
                            idmonto = monto.idmonto,
                            iduser = monto.iduser,
                            concepto = monto.concepto,
                            valor = monto.valor,
                            fecha = monto.fecha,
                            frecuencia = monto.frecuencia,
                            etiqueta = monto.etiqueta,
                            interes = monto.interes,
                            veces = monto.veces,
                            estado = monto.estado,
                            adddate = monto.adddate,
                            enddate = monto.enddate,
                            cooldown = newcool
                        )
                        montoDao.updateMonto(toMeltMonto)
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
            val checkM: Button,
            val delayM: Button,
            val skipM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_porpagar, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.pNombre)
            val valorTextView = itemView.findViewById<TextView>(R.id.pValor)
            val fechaTextView = itemView.findViewById<TextView>(R.id.pFecha)
            val updateM = itemView.findViewById<Button>(R.id.editP)
            val checkM = itemView.findViewById<Button>(R.id.checkP)
            val delayM = itemView.findViewById<Button>(R.id.delayP)
            val skipM = itemView.findViewById<Button>(R.id.skipP)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                fechaTextView,
                updateM,
                checkM,
                delayM,
                skipM
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
                    .setPositiveButton("Confirmar") { dialog, _ ->
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
                            .replace(R.id.index_container, indexmain()).addToBackStack(null)
                            .commit()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                confirmDialog.show()
            }
            holder.delayM.setOnClickListener {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres posponer el gasto ${monto.concepto}?")
                    .setPositiveButton("Posponer") { dialog, _ ->
                        lifecycleScope.launch {
                            delay(
                                monto.idmonto,
                                monto.concepto,
                                monto.valor,
                                monto.fecha,
                                monto.frecuencia,
                                monto.etiqueta,
                                monto.interes,
                                monto.veces,
                                monto.estado,
                                monto.adddate
                            )
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.index_container, indexmain()).addToBackStack(null)
                            .commit()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                confirmDialog.show()
            }
            holder.skipM.setOnClickListener {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres omitir el gasto ${monto.concepto} por hoy?")
                    .setPositiveButton("Confirmar") { dialog, _ ->
                        lifecycleScope.launch {
                            skip(
                                monto.idmonto,
                                monto.concepto,
                                monto.valor,
                                monto.fecha,
                                monto.frecuencia,
                                monto.etiqueta,
                                monto.interes,
                                monto.veces,
                                monto.estado,
                                monto.adddate
                            )
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.index_container, indexmain()).addToBackStack(null)
                            .commit()
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

                val estado = montoDao.getEstado(idmonto.toInt())

                var nv: Long? = 1
                if (veces != null)
                    nv = veces + 1

                var status = estado
                var delay = montoDao.getDelay(idmonto.toInt())
                if (delay != 0) {
                    delay -= 1
                    delay = maxOf(delay, 0)
                } else {
                    when (estado) {
                        0 -> status = 1
                        3 -> status = 4
                        5 -> status = 6
                        8 -> status = 9
                    }
                }
                var cooldown = 0
                when (frecuencia) {
                    14 -> cooldown = 1
                    61 -> cooldown = 1
                    91 -> cooldown = 2
                    122 -> cooldown = 3
                    183 -> cooldown = 5
                    365 -> cooldown = 11
                }

                val sequence = montoDao.getSequence(id)
                val values = sequence.trim('.').split('.').map { it.toInt() }.toMutableList()
                if (values.isNotEmpty()) {
                    val lastIndex = values.size - 1
                    values[lastIndex] += 1
                }
                val updatedString = values.joinToString(".")
                val result = "$updatedString."

                val enddate = montoDao.getEnded(idmonto.toInt())
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
                    estado = status,
                    adddate = adddate,
                    enddate = enddate,
                    cooldown = cooldown,
                    delay = delay,
                    sequence = result
                )

                val totalGastos = ingresoGastoDao.checkSummaryG()

                ingresoGastoDao.updateSummaryG(
                    montoPresionado.iduser.toInt(),
                    totalGastos + montoPresionado.valor
                )
                montoDao.updateMonto(montoPresionado)
                val montos = montoDao.getMonto()
                Log.i("ALL MONTOS", montos.toString())
            }
        }

        suspend fun skip(
            id: Long,
            concepto: String,
            valor: Double,
            fecha: Int?,
            frecuencia: Int?,
            etiqueta: Int,
            interes: Double?,
            veces: Long?,
            estado: Int?,
            adddate: Int
        ) {
            withContext(Dispatchers.IO) {
                val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

                var status = estado
                var delay = montoDao.getDelay(id.toInt())
                if (delay != 0) {
                    delay -= 1
                    delay = maxOf(delay, 0)
                } else {
                    when (estado) {
                        0 -> status = 1
                        3 -> status = 4
                        5 -> status = 6
                        8 -> status = 9
                    }
                }
                var cooldown = 0
                when (frecuencia) {
                    14 -> cooldown = 1
                    61 -> cooldown = 1
                    91 -> cooldown = 2
                    122 -> cooldown = 3
                    183 -> cooldown = 5
                    365 -> cooldown = 11
                }
                val sequence = montoDao.getSequence(id.toInt())
                val tipointeres = montoDao.getTipoInteres(id.toInt())
                val enddate = montoDao.getEnded(id.toInt())
                val iduser = usuarioDao.checkId().toLong()
                val montoPresionado = Monto(
                    idmonto = id,
                    iduser = iduser,
                    concepto = concepto,
                    valor = valor,
                    fecha = fecha,
                    frecuencia = frecuencia,
                    etiqueta = etiqueta,
                    interes = interes,
                    tipointeres = tipointeres,
                    veces = veces,
                    estado = status,
                    adddate = adddate,
                    enddate = enddate,
                    delay = delay,
                    sequence = sequence,
                    cooldown = cooldown
                )

                montoDao.updateMonto(montoPresionado)
                val montos = montoDao.getMonto()
                Log.i("ALL MONTOS", montos.toString())
            }
        }

        suspend fun delay(
            id: Long,
            concepto: String,
            valor: Double,
            fecha: Int?,
            frecuencia: Int?,
            etiqueta: Int,
            interes: Double?,
            veces: Long?,
            estado: Int?,
            adddate: Int
        ) {
            withContext(Dispatchers.IO) {
                val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

                var cooldown = 0
                when (frecuencia) {
                    14 -> cooldown = 1
                    61 -> cooldown = 1
                    91 -> cooldown = 2
                    122 -> cooldown = 3
                    183 -> cooldown = 5
                    365 -> cooldown = 11
                }
                val delay = montoDao.getDelay(id.toInt()) + 1
                val sequence = montoDao.getSequence(id.toInt())
                val tipointeres = montoDao.getTipoInteres(id.toInt())
                val enddate = montoDao.getEnded(id.toInt())
                val iduser = usuarioDao.checkId().toLong()
                val montoPresionado = Monto(
                    idmonto = id,
                    iduser = iduser,
                    concepto = concepto,
                    valor = valor,
                    fecha = fecha,
                    frecuencia = frecuencia,
                    etiqueta = etiqueta,
                    interes = interes,
                    tipointeres = tipointeres,
                    veces = veces,
                    estado = estado,
                    adddate = adddate,
                    enddate = enddate,
                    delay = delay,
                    sequence = sequence,
                    cooldown = cooldown
                )

                montoDao.updateMonto(montoPresionado)
                val montos = montoDao.getMonto()
                Log.i("ALL MONTOS", montos.toString())
            }
        }
    }
}