package com.example.st5

import android.content.Context
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class finanzasstatsahorro : Fragment() {

    private lateinit var binding: FragmentFinanzasstatsahorroBinding

    private val ahorros: MutableList<Float> = mutableListOf()
    private var dolarCompra = 0F
    private var dolarVenta = 0F
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
        dolarCompra = getDollarCompra().toFloat()
        dolarVenta = getDollarVenta().toFloat()
        lifecycleScope.launch {
            getAhorros()
            delay(500)
            Log.v("AHORROXDIA", ahorros.toString())
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
        }

        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.finanzas_container, Configuracion()).addToBackStack(null).commit()
        }
    }

    private fun getAhorros() {
        val today = LocalDate.now()
        val twoWeeksAgo = today.minusWeeks(2)

        val daysInRange = ChronoUnit.DAYS.between(twoWeeksAgo, today)

        for (i in 0 until daysInRange) {
            lifecycleScope.launch {
                val currentDate = twoWeeksAgo.plusDays(i)
                val dayAhorro = getAhorrosXDia(currentDate)
                ahorros.add(dayAhorro)
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
            ingresos = montoDao.getStatI(todayInt, dom, dow, 100, todayInt).sumByDouble { it.valor }.toFloat()
            gastos = montoDao.getStatG(todayInt, dom, dow, 100, todayInt).sumByDouble { it.valor }.toFloat()
        }
        Log.v("INGRESOS & GASTOS", "$ingresos - $gastos = ${ingresos - gastos}")
        return ingresos - gastos
    }
    private fun getDollarCompra() : String {
        var dollarValue = "0"

        val durl = "http://savetrack.com.mx/dlrvalCompra.php"
        val queue: RequestQueue = Volley.newRequestQueue(requireContext())
        val checkDollar = StringRequest(
            Request.Method.GET, durl,
            { response ->
                dollarValue = response.toString()
                Log.d("DÓLAR COMPRA", dollarValue)
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
        return dollarValue
    }
    private fun getDollarVenta() : String {
        var dollarValue = "0"

        val durl = "http://savetrack.com.mx/dlrvalVenta.php"
        val queue: RequestQueue = Volley.newRequestQueue(requireContext())
        val checkDollar = StringRequest(
            Request.Method.GET, durl,
            { response ->
                dollarValue = response.toString()
                Log.d("DÓLAR VENTA", dollarValue)
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
        return dollarValue
    }
    private fun setData(count: Int, chart: LineChart, position: Int) {
        val values = ArrayList<Entry>()
        val range = 0F

        when (position) {
            0 -> {
                for (i in 0 until count) {
                    values.add(Entry(i.toFloat(), ahorros[i], resources.getDrawable(R.drawable.ic_bubbles)))
                }

                val set1: LineDataSet
                if (chart.data != null && chart.data.dataSetCount > 0) {
                    set1 = chart.data.getDataSetByIndex(0) as LineDataSet
                    set1.values = values
                    set1.notifyDataSetChanged()
                    chart.data.notifyDataChanged()
                    chart.notifyDataSetChanged()
                } else {
                    set1 = LineDataSet(values, "Ahorros")
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
                        IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }
                    set1.fillColor = R.color.G2

                    val dataSets: ArrayList<ILineDataSet> = ArrayList()
                    dataSets.add(set1)
                    val data = LineData(dataSets)

                    chart.data = data
                }
            } // TODO arreglar los problemas con el acomodo de los valores en la gráfica
            1 -> {
                val dolaresList = ArrayList<Entry>()

                for (i in 0 until count) {
                    val current = ahorros[i]*dolarCompra
                    Log.v("CURRENT COMPRA", dolarCompra.toString())
                    values.add(Entry(i.toFloat(), current, resources.getDrawable(R.drawable.ic_bubbles)))
                }
                for (i in 0 until count) {
                    val current = ahorros[i]*dolarVenta
                    Log.v("CURRENT VENTA", dolarVenta.toString())
                    dolaresList.add(Entry(i.toFloat(), current, resources.getDrawable(R.drawable.ic_bubbles)))
                }

                val setC: LineDataSet
                val setV: LineDataSet
                if (chart.data != null && chart.data.dataSetCount > 0) {
                    setC = chart.data.getDataSetByIndex(0) as LineDataSet
                    setC.values = values
                    setC.notifyDataSetChanged()
                    setV = chart.data.getDataSetByIndex(0) as LineDataSet
                    setV.values = values
                    setV.notifyDataSetChanged()

                    chart.data.notifyDataChanged()
                    chart.notifyDataSetChanged()
                } else {
                    setC = LineDataSet(values, "Compra")
                    setC.setDrawIcons(false)
                    setC.enableDashedLine(16f, 0f, 0f)
                    setC.color = R.color.P1
                    setC.setCircleColor(R.color.R0)
                    setC.lineWidth = 10f
                    setC.circleRadius = 3f
                    setC.setDrawCircleHole(true)
                    setC.formLineWidth = 1f
                    setC.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    setC.formSize = 15f
                    setC.valueTextSize = 9f
                    setC.enableDashedHighlightLine(10f, 5f, 0f)
                    setC.setDrawFilled(false)
                    setC.fillFormatter = IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }
                    setC.fillColor = R.color.G2

                    setV = LineDataSet(dolaresList, "Venta")
                    setV.setDrawIcons(false)
                    setV.enableDashedLine(16f, 0f, 0f)
                    setV.color = R.color.P1
                    setV.setCircleColor(R.color.R0)
                    setV.lineWidth = 10f
                    setV.circleRadius = 3f
                    setV.setDrawCircleHole(true)
                    setV.formLineWidth = 1f
                    setV.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    setV.formSize = 15f
                    setV.valueTextSize = 9f
                    setV.enableDashedHighlightLine(10f, 5f, 0f)
                    setV.setDrawFilled(false)
                    setV.fillFormatter = IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }
                    setV.fillColor = R.color.G2

                    val dataSets: ArrayList<ILineDataSet> = ArrayList()
                    dataSets.add(setC)
                    dataSets.add(setV)
                    val data = LineData(dataSets)

                    chart.data = data
                }
            }
            2 -> {
                for (i in 0 until count) {
                    values.add(Entry(i.toFloat(), ahorros[i], resources.getDrawable(R.drawable.ic_bubbles)))
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
                        IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }
                    set1.fillColor = R.color.G2

                    val dataSets: ArrayList<ILineDataSet> = ArrayList()
                    dataSets.add(set1)
                    val data = LineData(dataSets)

                    chart.data = data
                }
            }
            else -> {
                for (i in 0 until count) {
                    values.add(
                        Entry(
                            i.toFloat(),
                            range,
                            resources.getDrawable(R.drawable.ic_bubbles)
                        )
                    )
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
                        IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }
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
            val chart: LineChart
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_chart, parent, false)
            val moneda = itemView.findViewById<TextView>(R.id.moneda)
            val valor = itemView.findViewById<TextView>(R.id.valor)
            val porcentaje = itemView.findViewById<TextView>(R.id.porcentaje)
            val chart = itemView.findViewById<LineChart>(R.id.thechart)
            return ViewHolder(
                itemView,
                moneda,
                valor,
                porcentaje,
                chart
            )
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val chart = holder.chart

            chart.setBackgroundColor(resources.getColor(R.color.N1))
            chart.description.isEnabled = false
            chart.setTouchEnabled(true)
            chart.setDrawGridBackground(false)
            chart.isDragEnabled = true
            chart.setScaleEnabled(true)

            val moneda: String
            val valor: Float
            val porcentaje: Float
            val count = 14

            when (position) {
                0 -> {
                    moneda = "Estático"
                    valor = 10.0F
                    porcentaje = 20.1F

                    setData(count, chart, position)
                }
                1 -> {
                    moneda = "Inversión"
                    valor = 20.1F
                    porcentaje = 30.2F

                    setData(count, chart, position)
                }
                2 -> {
                    moneda = "Moneda"
                    valor = 30.2F
                    porcentaje = 40.3F

                    setData(count, chart, position)
                }
                else -> {
                    moneda = "Moneda"
                    valor = 0.0F
                    porcentaje = 0.0F

                    setData(0, chart, 0)
                }
            }

            holder.moneda.text = moneda
            holder.valor.text = valor.toString()
            holder.porcentaje.text = porcentaje.toString()
        }


        override fun getItemCount(): Int {
            return 3
        }
    }
}