package com.example.st5

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentIndexmainBinding
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

    private var switchVal = false
    private var lista: Fragment = indexGastosList()

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
        binding = FragmentIndexmainBinding.inflate(inflater, container, false)
        binding.SultanOfSwing.checked = IconSwitch.Checked.RIGHT
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

    //region PIECHARTS
    private suspend fun setupPieChartG() {
        setupColors()
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

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

            if (isNotZero(totalAlimentos)) {
                percentAlimento = (totalAlimentos.toFloat() / totalGastos.toFloat()) * 100
            }
            if (isNotZero(totalHogar)) {
                percentHogar = (totalHogar.toFloat() / totalGastos.toFloat()) * 100
            }
            if (isNotZero(totalBienestar)) {
                percentBienestar = (totalBienestar.toFloat() / totalGastos.toFloat()) * 100
            }
            if (isNotZero(totalNecesidades)) {
                percentNecesidades = (totalNecesidades.toFloat() / totalGastos.toFloat()) * 100
            }
            if (isNotZero(totalHormiga)) {
                percentHormiga = (totalHormiga.toFloat() / totalGastos.toFloat()) * 100
            }
            if (isNotZero(totalOcio)) {
                percentOcio = (totalOcio.toFloat() / totalGastos.toFloat()) * 100
            }
            if (isNotZero(totalObsequios)) {
                percentObsequios = (totalObsequios.toFloat() / totalGastos.toFloat()) * 100
            }
            if (isNotZero(totalDeudas)) {
                percentDeudas = (totalDeudas.toFloat() / totalGastos.toFloat()) * 100
            }

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

            val idus = usuarioDao.checkId()
            usuarioDao.updateBalance(idus, totalisimo)
        }
    }

    private suspend fun setupPieChartI() {
        setupColors()
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

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

            if (isNotZero(totalSalarios)) {
                val percentSalarios = (totalSalarios.toFloat() / totalIngresos.toFloat()) * 100
            }
            if (isNotZero(totalIrregulares)) {
                percentIrregulares = (totalIrregulares.toFloat() / totalIngresos.toFloat()) * 100
            }
            if (isNotZero(totalBecas)) {
                percentBecas = (totalBecas.toFloat() / totalIngresos.toFloat()) * 100
            }
            if (isNotZero(totalPensiones)) {
                percentPensiones = (totalPensiones.toFloat() / totalIngresos.toFloat()) * 100
            }
            if (isNotZero(totalManutencion)) {
                percentManutencion = (totalManutencion.toFloat() / totalIngresos.toFloat()) * 100
            }
            if (isNotZero(totalPasivos)) {
                percentPasivos = (totalPasivos.toFloat() / totalIngresos.toFloat()) * 100
            }
            if (isNotZero(totalRegalos)) {
                percentRegalos = (totalRegalos.toFloat() / totalIngresos.toFloat()) * 100
            }
            if (isNotZero(totalPrestamos)) {
                percentPrestamos = (totalPrestamos.toFloat() / totalIngresos.toFloat()) * 100
            }

            val entries = listOf(
                totalSalarios.toFloat()?.let { PieEntry(it) },
                totalIrregulares.toFloat()?.let { PieEntry(it) },
                totalBecas.toFloat()?.let { PieEntry(it) },
                totalPensiones.toFloat()?.let { PieEntry(it) },
                totalManutencion.toFloat()?.let { PieEntry(it) },
                totalPasivos.toFloat()?.let { PieEntry(it) },
                totalRegalos.toFloat()?.let { PieEntry(it) },
                totalPrestamos.toFloat()?.let { PieEntry(it) }
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

            val idus = usuarioDao.checkId()
            usuarioDao.updateBalance(idus, totalisimo)
        }
    }

    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addWithSwitchOn = indexadd.newInstance(true)
        val addWithSwitchOff = indexadd.newInstance(false)


        lifecycleScope.launch {
            delay(3000)
            procesarMontos()
        }
        lifecycleScope.launch {
            delay(1000)
            gi(switchVal)
            delay(500)
            binding.GraficoPastel.alpha = 0f
        }

        binding.MedidorDeAhorroButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, lista).addToBackStack(null).commit()

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

    private suspend fun limpiar() {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            montoDao.clean()
        }
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
        override fun onValueSelected(e: Entry, h: Highlight) {
            Log.i("VAL SELECTED", "Value: " + e.y + ", index: " + h.x.toInt())

            val chart = binding.PieChart

            chart.highlightValue(h)

            lista = if (!switchVal) {
                binding.MedidorDeAhorroButton.setBackgroundResource(colorsGDraw[h.x.toInt()])
                binding.searchforlabel.text = textG[h.x.toInt()]
                val iglinstance = indexGastosList.labelSearch(h.x.toInt())
                iglinstance
            } else {
                binding.MedidorDeAhorroButton.setBackgroundResource(colorsIDraw[h.x.toInt()])
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

            val fechaActual = LocalDate.now()
            val today = fechaActual.toString()
            val prev = assetsDao.getLastProcess()

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val truefecha = formatoFecha.parse(today)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dm = calendar.get(Calendar.DAY_OF_MONTH)
            val dom = String.format("%02d", dm)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = "Diario"
            when (w) {
                1 -> dow = "Sunday"
                2 -> dow = "Monday"
                3 -> dow = "Tuesday"
                4 -> dow = "Wednesday"
                5 -> dow = "Thursday"
                6 -> dow = "Friday"
                7 -> dow = "Saturday"
            }

            val addd: Int = today.replace("-", "").toInt()

            Log.i("DOM", dom)
            Log.i("DOW", dow)

            Log.i("todayyyy", today)
            Log.i("prevvvvv", prev)

            val montos = montoDao.getMontoXFecha(today, dom, dow, "Diario", addd)

            if (prev != today) {
                for (monto in montos) {
                    val totalIngresos = ingresoGastoDao.checkSummaryI()
                    val totalGastos = ingresoGastoDao.checkSummaryG()

                    Log.i("MONTO PROCESADO", monto.toString())
                    val weekMonto = monto.fecha.uppercase()
                    Log.v("wek", weekMonto)

                    if (monto.valor > 0) {
                        ingresoGastoDao.updateSummaryI(
                            monto.iduser.toInt(),
                            totalIngresos + monto.valor
                        )
                    } else {
                        ingresoGastoDao.updateSummaryG(
                            monto.iduser.toInt(),
                            totalGastos + monto.valor
                        )
                    }

                    monto.veces = monto.veces?.plus(1)
                    montoDao.updateMonto(monto)
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
}