package com.example.st5

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

class indexmain : Fragment(), OnChartValueSelectedListener {
    private lateinit var binding: FragmentIndexmainBinding
    private val colors: MutableList<Int> = mutableListOf()

    private var listamontos = Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
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
        colors.add(ContextCompat.getColor(requireContext(), R.color.Y3))
        colors.add(ContextCompat.getColor(requireContext(), R.color.B2))
        colors.add(ContextCompat.getColor(requireContext(), R.color.G2))
        colors.add(ContextCompat.getColor(requireContext(), R.color.B0))
        colors.add(ContextCompat.getColor(requireContext(), R.color.O1))
        colors.add(ContextCompat.getColor(requireContext(), R.color.R1))
        colors.add(ContextCompat.getColor(requireContext(), R.color.P1))
        colors.add(ContextCompat.getColor(requireContext(), R.color.R0))
    }

    private fun isNotZero(value: Double?): Boolean
    {
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
            for (monto in totalI){
                totalIngresos += monto.valor
            }
            Log.v("INGRESOS", totalI.toString())

            var totalGastos = 0.0
            for (monto in totalG){
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

            if (isNotZero(totalAlimentos)){
                percentAlimento = (totalAlimentos.toFloat() / totalGastos.toFloat())*100
            }
            if (isNotZero(totalHogar)){
                percentHogar = (totalHogar.toFloat() / totalGastos.toFloat())*100
            }
            if (isNotZero(totalBienestar)){
                percentBienestar = (totalBienestar.toFloat() / totalGastos.toFloat())*100
            }
            if (isNotZero(totalNecesidades)){
                percentNecesidades = (totalNecesidades.toFloat() / totalGastos.toFloat())*100
            }
            if (isNotZero(totalHormiga)){
                percentHormiga = (totalHormiga.toFloat() / totalGastos.toFloat())*100
            }
            if (isNotZero(totalOcio)){
                percentOcio = (totalOcio.toFloat() / totalGastos.toFloat())*100
            }
            if (isNotZero(totalObsequios)){
                percentObsequios = (totalObsequios.toFloat() / totalGastos.toFloat())*100
            }
            if (isNotZero(totalDeudas)){
                percentDeudas = (totalDeudas.toFloat() / totalGastos.toFloat())*100
            }

            val entries = listOf(
                percentAlimento?.let { PieEntry(it) },
                percentHogar?.let { PieEntry(it) },
                percentBienestar?.let { PieEntry(it) },
                percentNecesidades?.let { PieEntry(it) },
                percentHormiga?.let { PieEntry(it) },
                percentOcio?.let { PieEntry(it) },
                percentObsequios?.let { PieEntry(it) },
                percentDeudas?.let { PieEntry(it) }
            )

            val dataSet = PieDataSet(entries, "Gastos")
            dataSet.colors = colors

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
            for (monto in totalI){
                totalIngresos += monto.valor
            }
            Log.v("INGRESOS", totalI.toString())

            var totalGastos = 0.0
            for (monto in totalG){
                totalGastos += monto.valor
            }
            Log.v("GASTOS", totalG.toString())

            var totalisimo = 0.0
            for (monto in totalis) {
                totalisimo += monto.valor
            }
            Log.v("GRAN TOTAL", totalisimo.toString())

            var percentSalario: Float? = 0f
            var percentIrregulares: Float? = 0f
            var percentBecas: Float? = 0f
            var percentPensiones: Float? = 0f
            var percentManutencion: Float? = 0f
            var percentPasivos: Float? = 0f
            var percentRegalos: Float? = 0f
            var percentPrestamos: Float? = 0f

            if (isNotZero(totalSalarios)){
                percentSalario = (totalSalarios.toFloat() / totalIngresos.toFloat())*100
            }
            if (isNotZero(totalIrregulares)){
                percentIrregulares = (totalIrregulares.toFloat() / totalIngresos.toFloat())*100
            }
            if (isNotZero(totalBecas)){
                percentBecas = (totalBecas.toFloat() / totalIngresos.toFloat())*100
            }
            if (isNotZero(totalPensiones)){
                percentPensiones = (totalPensiones.toFloat() / totalIngresos.toFloat())*100
            }
            if (isNotZero(totalManutencion)){
                percentManutencion = (totalManutencion.toFloat() / totalIngresos.toFloat())*100
            }
            if (isNotZero(totalPasivos)){
                percentPasivos = (totalPasivos.toFloat() / totalIngresos.toFloat())*100
            }
            if (isNotZero(totalRegalos)){
                percentRegalos = (totalRegalos.toFloat() / totalIngresos.toFloat())*100
            }
            if (isNotZero(totalPrestamos)){
                percentPrestamos = (totalPrestamos.toFloat() / totalIngresos.toFloat())*100
            }

            val entries = listOf(
                percentSalario?.let { PieEntry(it) },
                percentIrregulares?.let { PieEntry(it) },
                percentBecas?.let { PieEntry(it) },
                percentPensiones?.let { PieEntry(it) },
                percentManutencion?.let { PieEntry(it) },
                percentPasivos?.let { PieEntry(it) },
                percentRegalos?.let { PieEntry(it) },
                percentPrestamos?.let { PieEntry(it) }
            )

            val dataSet = PieDataSet(entries, "Gastos")
            dataSet.colors = colors

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

    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addWithSwitchOn = indexadd.newInstance(true)
        val addWithSwitchOff = indexadd.newInstance(false)

        var lista : Fragment
        
        var switchVal = false
        lifecycleScope.launch {
            delay(1500)
            gi(!switchVal)
        }

        binding.MedidorDeAhorroButton.setOnClickListener {
            lista = if (switchVal){
                indexIngresosList()
            } else {
                indexGastosList()
            }
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, lista).addToBackStack(null).commit()

        }

        binding.PieChart.setOnChartValueSelectedListener(pieChartOnChartValueSelectedListener())

        binding.SultanOfSwing.setCheckedChangeListener {
            when (binding.SultanOfSwing.checked) {
                IconSwitch.Checked.LEFT -> {
                    switchVal = true
                }
                IconSwitch.Checked.RIGHT -> {
                    switchVal = false
                }
                else -> {}
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

        /*
        binding.MedidorDeAhorroButton.setOnClickListener {
            lifecycleScope.launch{
                limpiar()
            }
        }
         */

        binding.ConfigButton.setOnClickListener {
            lifecycleScope.launch {
                limpiar()
            }
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
            }
        } else {
            lifecycleScope.launch {
                setupPieChartI()
            }
        }
    }

    inner class pieChartOnChartValueSelectedListener : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry, h: Highlight) {
            if (e == null) return
            Log.i(
                "VAL SELECTED",
                "Value: " + e.y + ", index: " + h.x + ", DataSet index: " + h.dataSetIndex
            )
        }
        override fun onNothingSelected() {
            Log.i("PieChart", "nothing selected")
        }
    }
    override fun onValueSelected(e: Entry, h: Highlight) {
        if (e == null) return
        Log.i(
            "VAL SELECTED",
            "Value: " + e.y + ", index: " + h.x + ", DataSet index: " + h.dataSetIndex
        )
    }
    override fun onNothingSelected() {
        Log.i("PieChart", "nothing selected")
    }
}