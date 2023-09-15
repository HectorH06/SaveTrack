package com.example.st5

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.DashPathEffect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentFinanzasstatsahorroBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


class finanzasstatsahorro : Fragment() {

    private lateinit var binding: FragmentFinanzasstatsahorroBinding

    private val ahorrosMap = mutableMapOf<LocalDate, Float>()
    private val currencyData = mutableMapOf<String, MutableList<Float>>()
    private val currencies = arrayOf("USD", "CAD", "EUR")
    private var dollarC = 0F
    private var dollarV = 0F
    private var rango: Long = 3L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
            }

            Log.i("MODO", isDarkMode.toString())
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.finanzas_container, finanzasmain()).addToBackStack(null).commit()
                    val fragmentToRemove = parentFragmentManager.findFragmentByTag("finanzasstatsahorro")
                    if (fragmentToRemove != null) {
                        parentFragmentManager.beginTransaction().remove(fragmentToRemove).commit()
                    }
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
        binding = FragmentFinanzasstatsahorroBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            getDivisas()
            getDollar()
            getAhorros()
            delay(3000)
            Log.v("AHORROXDIA", ahorrosMap.toString())
            Log.v("DIVISAS", currencyData.toString())
            binding.displaycharts.adapter = ChartAdapter()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val back = finanzasmain()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.finanzas_container, back).addToBackStack(null).commit()
            val fragmentToRemove = parentFragmentManager.findFragmentByTag("finanzasstatsahorro")
            if (fragmentToRemove != null) {
                parentFragmentManager.beginTransaction().remove(fragmentToRemove).commit()
            }
        }

        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.finanzas_container, Configuracion()).addToBackStack(null).commit()
        }

        binding.RangoSeekbar.min = 4
        binding.RangoSeekbar.max = 6
        binding.RangoSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.RangoTV.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.RangoTV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("NotifyDataSetChanged")
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty()) {
                    rango = text.toLong() - 1
                    lifecycleScope.launch {
                        getAhorros()
                        binding.displaycharts.adapter = ChartAdapter()
                    }
                }
            }
        })
    }

    private fun getAhorros() {
        val today = LocalDate.now()
        val weeksAgo = today.minusWeeks(5)

        val daysInRange = ChronoUnit.DAYS.between(weeksAgo, today)

        for (i in 0 until daysInRange) {
            lifecycleScope.launch {
                val currentDate = weeksAgo.plusDays(i)
                val dayAhorro = getAhorrosXDia(currentDate)
                ahorrosMap[currentDate] = dayAhorro
            }
        }
    }
    private suspend fun getAhorrosXDia(date: LocalDate): Float {
        var ingresos = 0F
        var gastos = 0F
        withContext(Dispatchers.IO) {
            val todayInt = date.format(DateTimeFormatter.BASIC_ISO_DATE).toInt()
            val dom = date.dayOfMonth
            val w = date.dayOfWeek.value
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
            Log.v("FECHA", "$todayInt / $dom / $dow")

            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            ingresos = montoDao.getStatI(todayInt, dom, dow, 100, todayInt).sumOf { it.valor }.toFloat()
            gastos = montoDao.getStatG(todayInt, dom, dow, 100, todayInt).sumOf { it.valor }.toFloat()
        }
        Log.v("INGRESOS & GASTOS", "$ingresos - $gastos = ${ingresos - gastos}")
        return ingresos - gastos
    }
    private suspend fun getDollar() {

        val durl = "http://savetrack.com.mx/dlrvalCompra.php"
        val durl2 = "http://savetrack.com.mx/dlrvalVenta.php"
        val queue: RequestQueue = Volley.newRequestQueue(requireContext())
        val checkDollar = StringRequest(
            Request.Method.GET, durl,
            { response ->
                dollarC = response.toString().toFloat()
                Log.d("DÓLAR COMPRA", dollarC.toString())
            },
            { error ->
                Toast.makeText(
                    requireContext(),
                    "No se ha podido conectar al valor del dólar hoy",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("error => $error", "SIE API ERROR")
            }
        )
        queue.add(checkDollar)
        val checkDollar2 = StringRequest(
            Request.Method.GET, durl2,
            { response ->
                dollarV = response.toString().toFloat()
                Log.d("DÓLAR VENTA", dollarV.toString())
            },
            { error ->
                Toast.makeText(
                    requireContext(),
                    "No se ha podido conectar al valor del dólar hoy",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("error => $error", "SIE API ERROR")
            }
        )
        queue.add(checkDollar2)
        delay(400)
    }
    private fun getDivisas() {
        val baseUrl = "http://savetrack.com.mx/divisas.php?basecurrency=MXN"

        val today = LocalDate.now()
        val ago = today.minusDays(7)
        val daysInRange = ChronoUnit.DAYS.between(ago, today)
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val queue: RequestQueue = Volley.newRequestQueue(requireContext())
        for (i in 0 .. daysInRange) {
            val date = ago.plusDays(i)
            val formattedDate = date.format(dateFormat)
            val apiUrl = "$baseUrl&date=$formattedDate&currencies=${currencies.joinToString(",")}"

            val checkDollar = StringRequest(
                Request.Method.GET, apiUrl,
                { response ->
                    Log.v("RES", response)
                    try {
                        val responseData = JSONObject(response)
                        Log.v("Try", "$responseData")
                        for (currency in currencies) {
                            val value = 1/responseData.optDouble(currency)
                            Log.v("VALUES", "$value")
                            if (!currencyData.containsKey(currency)) {
                                currencyData[currency] = mutableListOf()
                            }
                            currencyData[currency]?.add(value.toFloat())
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                { error ->
                    Toast.makeText(
                        requireContext(),
                        "No se ha podido conectar al valor del dólar hoy",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("error => $error", "SIE API ERROR")
                }
            )
            queue.add(checkDollar)
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setData(count: Int, chart: LineChart, position: Int) {
        val values = Stack<Entry>()
        val values2 = Stack<Entry>()
        val values3 = Stack<Entry>()
        val range = 0F

        Log.v("DIVISAS", currencyData.toString())
        val ahorros = ahorrosMap.toSortedMap().values.toList()
        val size = ahorros.size - 1
        when (position) {
            0 -> {
                var j = 0F
                for (i in size - count .. size) {
                    j++
                    values.add(Entry(j, ahorros[i], R.drawable.ic_bubbles))
                    values2.add(Entry(j, ahorros[i], R.drawable.ic_bubbles))
                }

                val setA: LineDataSet
                val setB: LineDataSet
                if (chart.data != null && chart.data.dataSetCount > 0) {
                    setA = chart.data.getDataSetByIndex(0) as LineDataSet
                    setA.values = values
                    setA.notifyDataSetChanged()
                    setB = chart.data.getDataSetByIndex(0) as LineDataSet
                    setB.values = values2
                    setB.notifyDataSetChanged()

                    chart.data.notifyDataChanged()
                    chart.notifyDataSetChanged()
                } else {
                    setA = LineDataSet(values, "Estático en MXN")
                    setA.setDrawIcons(false)
                    setA.enableDashedLine(16f, 0f, 0f)
                    setA.color = R.color.P1
                    setA.setCircleColor(R.color.G0)
                    setA.lineWidth = 10f
                    setA.circleRadius = 3f
                    setA.setDrawCircleHole(true)
                    setA.formLineWidth = 1f
                    setA.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    setA.formSize = 15f
                    setA.valueTextSize = 9f
                    setA.enableDashedHighlightLine(10f, 5f, 0f)
                    setA.setDrawFilled(false)
                    setA.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
                    setA.fillColor = R.color.B1

                    setB = LineDataSet(values2, "Estático en USD")
                    setB.setDrawIcons(false)
                    setB.enableDashedLine(16f, 0f, 0f)
                    setB.color = R.color.P1
                    setB.setCircleColor(R.color.G0)
                    setB.lineWidth = 10f
                    setB.circleRadius = 3f
                    setB.setDrawCircleHole(true)
                    setB.formLineWidth = 1f
                    setB.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    setB.formSize = 15f
                    setB.valueTextSize = 9f
                    setB.enableDashedHighlightLine(10f, 5f, 0f)
                    setB.setDrawFilled(false)
                    setB.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
                    setB.fillColor = R.color.R0

                    val dataSets: ArrayList<ILineDataSet> = ArrayList()
                    dataSets.add(setA)
                    dataSets.add(setB)
                    val data = LineData(dataSets)

                    chart.data = data
                }
            }
            1 -> {
                val dolaresList = currencyData["USD"]
                var j = 0F
                if (dolaresList != null) {
                    for (i in dolaresList.size - count - 1 until dolaresList.size) {
                        j++
                        val current = dolaresList[i]
                        Log.v("CURRENT COMPRA", dollarC.toString())
                        values.add(Entry(j, current, R.drawable.ic_bubbles))
                    }
                }

                val setA: LineDataSet
                val setB: LineDataSet
                if (chart.data != null && chart.data.dataSetCount > 0) {
                    setA = chart.data.getDataSetByIndex(0) as LineDataSet
                    setA.values = values
                    setA.notifyDataSetChanged()
                    setB = chart.data.getDataSetByIndex(0) as LineDataSet
                    setB.values = values
                    setB.notifyDataSetChanged()

                    chart.data.notifyDataChanged()
                    chart.notifyDataSetChanged()
                } else {
                    setA = LineDataSet(values, "Compra")
                    setA.setDrawIcons(false)
                    setA.enableDashedLine(16f, 0f, 0f)
                    setA.color = R.color.P1
                    setA.setCircleColor(R.color.G0)
                    setA.lineWidth = 10f
                    setA.circleRadius = 3f
                    setA.setDrawCircleHole(true)
                    setA.formLineWidth = 1f
                    setA.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    setA.formSize = 15f
                    setA.valueTextSize = 9f
                    setA.enableDashedHighlightLine(10f, 5f, 0f)
                    setA.setDrawFilled(false)
                    setA.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
                    setA.fillColor = R.color.B1

                    setB = LineDataSet(values, "Venta")
                    setB.setDrawIcons(false)
                    setB.enableDashedLine(16f, 0f, 0f)
                    setB.color = R.color.P1
                    setB.setCircleColor(R.color.G0)
                    setB.lineWidth = 10f
                    setB.circleRadius = 3f
                    setB.setDrawCircleHole(true)
                    setB.formLineWidth = 1f
                    setB.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    setB.formSize = 15f
                    setB.valueTextSize = 9f
                    setB.enableDashedHighlightLine(10f, 5f, 0f)
                    setB.setDrawFilled(false)
                    setB.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
                    setB.fillColor = R.color.R0

                    val dataSets: ArrayList<ILineDataSet> = ArrayList()
                    dataSets.add(setA)
                    dataSets.add(setB)
                    val data = LineData(dataSets)

                    chart.data = data
                }
            }
            2 -> {
                val usd = "USD"
                val eur = "EUR"
                val cad = "CAD"
                val dolaresList = currencyData[usd]
                val eurosList = currencyData[eur]
                val canadaList = currencyData[cad]

                var j = 0F
                if (dolaresList != null) {
                    for (i in dolaresList.size - count - 1 until dolaresList.size) {
                        j++
                        val current1 = dolaresList[i]
                        values.add(Entry(j, current1, R.drawable.ic_dollar))
                        val current2 = eurosList?.get(i)
                        values2.add(current2?.let {
                            Entry(j, it, R.drawable.ic_euro)
                        })
                        val current3 = canadaList?.get(i)
                        values3.add(current3?.let {
                            Entry(j, it, R.drawable.ic_dollar)
                        })
                    }
                }

                val setA: LineDataSet
                val setB: LineDataSet
                val setC: LineDataSet
                if (chart.data != null && chart.data.dataSetCount > 0) {
                    setA = chart.data.getDataSetByIndex(0) as LineDataSet
                    setA.values = values
                    setA.notifyDataSetChanged()
                    setB = chart.data.getDataSetByIndex(0) as LineDataSet
                    setB.values = values2
                    setB.notifyDataSetChanged()
                    setC = chart.data.getDataSetByIndex(0) as LineDataSet
                    setC.values = values3
                    setC.notifyDataSetChanged()

                    chart.data.notifyDataChanged()
                    chart.notifyDataSetChanged()
                } else {
                    setA = LineDataSet(values, usd)
                    setA.setDrawIcons(false)
                    setA.enableDashedLine(16f, 0f, 0f)
                    setA.color = R.color.P1
                    setA.setCircleColor(R.color.G0)
                    setA.lineWidth = 10f
                    setA.circleRadius = 3f
                    setA.setDrawCircleHole(true)
                    setA.formLineWidth = 1f
                    setA.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    setA.formSize = 15f
                    setA.valueTextSize = 9f
                    setA.enableDashedHighlightLine(10f, 5f, 0f)
                    setA.setDrawFilled(false)
                    setA.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
                    setA.fillColor = R.color.B1

                    setB = LineDataSet(values2, eur)
                    setB.setDrawIcons(false)
                    setB.enableDashedLine(16f, 0f, 0f)
                    setB.color = R.color.P1
                    setB.setCircleColor(R.color.G0)
                    setB.lineWidth = 10f
                    setB.circleRadius = 3f
                    setB.setDrawCircleHole(true)
                    setB.formLineWidth = 1f
                    setB.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    setB.formSize = 15f
                    setB.valueTextSize = 9f
                    setB.enableDashedHighlightLine(10f, 5f, 0f)
                    setB.setDrawFilled(false)
                    setB.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
                    setB.fillColor = R.color.R0

                    setC = LineDataSet(values3, cad)
                    setC.setDrawIcons(false)
                    setC.enableDashedLine(16f, 0f, 0f)
                    setC.color = R.color.P1
                    setC.setCircleColor(R.color.G0)
                    setC.lineWidth = 10f
                    setC.circleRadius = 3f
                    setC.setDrawCircleHole(true)
                    setC.formLineWidth = 1f
                    setC.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    setC.formSize = 15f
                    setC.valueTextSize = 9f
                    setC.enableDashedHighlightLine(10f, 5f, 0f)
                    setC.setDrawFilled(false)
                    setC.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
                    setC.fillColor = R.color.G2

                    val dataSets: ArrayList<ILineDataSet> = ArrayList()
                    dataSets.add(setA)
                    dataSets.add(setB)
                    dataSets.add(setC)
                    val data = LineData(dataSets)

                    chart.data = data
                }
            }
            else -> {
                for (i in 0 until count) {
                    values.add(Entry(i.toFloat(), range, R.drawable.ic_bubbles))
                }

                val set1: LineDataSet
                if (chart.data != null && chart.data.dataSetCount > 0) {
                    set1 = chart.data.getDataSetByIndex(0) as LineDataSet
                    set1.values = values
                    set1.notifyDataSetChanged()
                    chart.data.notifyDataChanged()
                    chart.notifyDataSetChanged()
                } else {
                    set1 = LineDataSet(values, "DataSet 1")
                    set1.setDrawIcons(false)
                    set1.enableDashedLine(16f, 0f, 0f)
                    set1.color = R.color.P1
                    set1.setCircleColor(R.color.R0)
                    set1.lineWidth = 10f
                    set1.circleRadius = 3f
                    set1.setDrawCircleHole(true)
                    set1.formLineWidth = 1f
                    set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    set1.formSize = 15f
                    set1.valueTextSize = 9f
                    set1.enableDashedHighlightLine(10f, 5f, 0f)
                    set1.setDrawFilled(false)
                    set1.fillFormatter =
                        IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
                    set1.fillColor = R.color.G2

                    val dataSets: ArrayList<ILineDataSet> = ArrayList()
                    dataSets.add(set1)
                    val data = LineData(dataSets)

                    chart.data = data
                }
            }
        }
    }

    private inner class ChartAdapter : RecyclerView.Adapter<ChartAdapter.ViewHolder>() {
        inner class ViewHolder(
            itemView: View,
            val moneda: TextView,
            val valor: TextView,
            val porcentaje: TextView,
            val chart: LineChart,
            val table: RecyclerView
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_chart, parent, false)
            val moneda = itemView.findViewById<TextView>(R.id.moneda)
            val valor = itemView.findViewById<TextView>(R.id.valor)
            val porcentaje = itemView.findViewById<TextView>(R.id.porcentaje)
            val chart = itemView.findViewById<LineChart>(R.id.thechart)
            val table = itemView.findViewById<RecyclerView>(R.id.displayStats)
            return ViewHolder(
                itemView,
                moneda,
                valor,
                porcentaje,
                chart,
                table
            )
        }

        @SuppressLint("ResourceAsColor")
        override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
            val chart = holder.chart

            chart.setBackgroundColor(R.color.N1)
            chart.description.isEnabled = false
            chart.setTouchEnabled(true)
            chart.setDrawGridBackground(false)
            chart.isDragEnabled = true
            chart.setScaleEnabled(true)

            val moneda: String
            val valor: String
            val porcentaje: String
            val count = rango.toInt()

            val ahorros = ahorrosMap.toSortedMap().values.toList()
            val size = ahorros.size - 1
            var balance = 0F
            for (i in size - count .. size) {
                balance += ahorros[i]
            }
            val percent = ((ahorros[size] - ahorros[size - count]) / maxOf(ahorros[size - count], 1F))*100
            when (position) {
                0 -> {
                    moneda = "Estático"
                    valor = "Balance: $balance$"
                    porcentaje = "Crecimiento: $percent%"

                    setData(count, chart, position)
                }
                1 -> {
                    moneda = "Inversión"
                    valor = "Compra: $dollarC"
                    porcentaje = "Venta: $dollarV"

                    setData(count, chart, position)
                }
                2 -> {
                    moneda = "Moneda"
                    valor = ""
                    porcentaje = ""
                    holder.table.adapter = DivisaAdapter()
                    setData(count, chart, position)
                }
                else -> {
                    moneda = "Moneda"
                    valor = "0.0F"
                    porcentaje = "0.0F"
                    setData(0, chart, 0)
                }
            }

            holder.moneda.text = moneda
            holder.valor.text = valor
            holder.porcentaje.text = porcentaje

        }

        override fun getItemCount(): Int {
            return 3
        }
    }

    private inner class DivisaAdapter : RecyclerView.Adapter<DivisaAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val divisaTextView: TextView,
            val valorTextView: TextView,
            val porcentajeTextView: TextView
        ) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_divisa, parent, false)
            val divisaTextView = itemView.findViewById<TextView>(R.id.DNombre)
            val valorTextView = itemView.findViewById<TextView>(R.id.DValor)
            val porcentajeTextView = itemView.findViewById<TextView>(R.id.DPorcentaje)
            return MontoViewHolder(
                itemView,
                divisaTextView,
                valorTextView,
                porcentajeTextView
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val count = rango.toInt()
            val usd = "USD"
            val eur = "EUR"
            val cad = "CAD"
            val dolaresList: MutableList<Float> = currencyData[usd] ?: ArrayList()
            val eurosList: MutableList<Float> = currencyData[eur] ?: ArrayList()
            val canadaList: MutableList<Float> = currencyData[cad] ?: ArrayList()
            val porcentajeUSD = ((dolaresList[dolaresList.size - 1] - dolaresList[dolaresList.size - 1 - count]) / (maxOf(dolaresList[dolaresList.size - 1 - count], 1F)))*100
            val porcentajeEUR = ((eurosList[eurosList.size - 1] - eurosList[eurosList.size - 1 - count]) / (maxOf(eurosList[eurosList.size - 1 - count], 1F)))*100
            val porcentajeCAD = ((canadaList[canadaList.size - 1] - canadaList[canadaList.size - 1 - count]) / (maxOf(canadaList[canadaList.size - 1 - count], 1F)))*100
            when (position) {
                0 -> {
                    holder.itemView.setBackgroundResource(R.drawable.p1topcell)
                    holder.divisaTextView.text = usd
                    holder.valorTextView.text = "${dolaresList[dolaresList.size - 1]}$"
                    holder.porcentajeTextView.text = "$porcentajeUSD%"
                }
                1 -> {
                    holder.divisaTextView.text = eur
                    holder.valorTextView.text = "${eurosList[eurosList.size - 1]}$"
                    holder.porcentajeTextView.text = "$porcentajeEUR%"
                }
                2 -> {
                    holder.itemView.setBackgroundResource(R.drawable.p1bottomcell)
                    holder.divisaTextView.text = cad
                    holder.valorTextView.text = "${canadaList[canadaList.size - 1]}$"
                    holder.porcentajeTextView.text = "$porcentajeCAD%"
                }
                else -> {
                    holder.divisaTextView.text = "$usd%"
                    holder.valorTextView.text = "$eur%"
                    holder.porcentajeTextView.text = "$cad%"
                }
            }
        }

        override fun getItemCount(): Int {
            return 3
        }
    }
}