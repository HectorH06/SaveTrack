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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentFinanzasinvpersoBinding
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


class finanzasPerso : Fragment() {

    private lateinit var binding: FragmentFinanzasinvpersoBinding

    private val ahorrosMap = mutableMapOf<LocalDate, Float>()
    private val currencyData = mutableMapOf<String, MutableList<Float>>()
    private val currencies = arrayOf("USD")
    private var dollarC = 0F
    private var dollarV = 0F
    private var rango: Long = 3L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.finanzas_container, finanzasstatsahorro()).addToBackStack(null)
                        .commit()
                    val fragmentToRemove = parentFragmentManager.findFragmentByTag("finanzasPerso")
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

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinanzasinvpersoBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
            }

            Log.i("MODO", isDarkMode.toString())

            getDivisas()
            getDollar()
            getAhorros()
            delay(3000)
            binding.perame.alpha = 0f
            Log.v("AHORROXDIA", ahorrosMap.toString())
            Log.v("DIVISAS", currencyData.toString())

            val chart = binding.thechart

            chart.setBackgroundColor(R.color.N1)
            chart.description.isEnabled = false
            chart.setTouchEnabled(true)
            chart.setDrawGridBackground(false)
            chart.isDragEnabled = true
            chart.setScaleEnabled(true)

            val count = rango.toInt()

            val ahorros = ahorrosMap.toSortedMap().values.toList()
            val size = ahorros.size - 1
            var balance = 0F
            for (i in size - count..size) {
                balance += ahorros[i]
            }
            val percent = ((ahorros[size] - ahorros[size - count]) / maxOf(ahorros[size - count], 1F)) * 100

            setData(count, chart)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val back = finanzasstatsahorro()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.finanzas_container, back).addToBackStack(null).commit()
            val fragmentToRemove = parentFragmentManager.findFragmentByTag("finanzasstatsahorro")
            if (fragmentToRemove != null) {
                parentFragmentManager.beginTransaction().remove(fragmentToRemove).commit()
            }
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

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty()) {
                    rango = text.toLong() - 1
                    lifecycleScope.launch {
                        binding.thechart.clear()
                        setData(rango.toInt(), binding.thechart)
                    }
                }
            }
        })

        binding.valor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                val aumentar = text.toDouble()
                val prevVal = currencyData["USD"]?.get(currencyData.size - 1)
                val percent = ((aumentar - (prevVal ?: return))/aumentar)*100
                if (text.isNotEmpty()) {
                    lifecycleScope.launch {
                        binding.thechart.clear()
                        setData(rango.toInt(), binding.thechart)
                    }
                }
            }
        })
        binding.valor2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                val aumentar = text.toDouble()
                val prevVal = currencyData["USD"]?.get(currencyData.size - 1)
                val percent = ((aumentar - (prevVal ?: return))/aumentar)*100
                if (text.isNotEmpty()) {
                    lifecycleScope.launch {
                        binding.thechart.clear()
                        setData(rango.toInt(), binding.thechart)
                    }
                }
            }
        })
        binding.porcentaje.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty() && text != ".") {
                    val aumentar = text.toDouble() / 100
                    val prevVal = currencyData["USD"]?.get(currencyData.size - 1)
                    val value = prevVal?.plus(prevVal.times(aumentar))
                    val formattedValue = value?.let { Decoder(requireContext()).format(it) }
                    binding.valor.setText(formattedValue.toString())
                }
            }
        })
        binding.porcentaje2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty() && text != ".") {
                    val aumentar = text.toDouble() / 100
                    val prevVal = currencyData["USD"]?.get(currencyData.size - 1)
                    val value = prevVal?.plus(prevVal.times(aumentar))
                    val formattedValue = value?.let { Decoder(requireContext()).format(it) }
                    binding.valor2.setText(formattedValue.toString())
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
            ingresos =
                montoDao.getStatI(todayInt, dom, dow, 100, todayInt).sumOf { it.valor }.toFloat()
            gastos =
                montoDao.getStatG(todayInt, dom, dow, 100, todayInt).sumOf { it.valor }.toFloat()
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
        for (i in 0..daysInRange) {
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
                            val value = 1 / responseData.optDouble(currency)
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
    private fun setData(count: Int, chart: LineChart) {
        val values1: MutableList<Entry> = mutableListOf()
        val values2: MutableList<Entry> = mutableListOf()

        Log.v("DIVISAS", currencyData.toString())

        val dolaresList = currencyData["USD"]
        var j = 0F
        if (dolaresList != null) {
            for (i in dolaresList.size - count - 1 until dolaresList.size) {
                j++
                val current = dolaresList[i]
                values1.add(Entry(j, current, R.drawable.ic_bubbles))
                values2.add(Entry(j, current, R.drawable.ic_bubbles))
            }
            j++
        }

        if (binding.valor.text.toString() != "" && binding.porcentaje.text.toString() != "" && binding.valor.text.toString() != "." && binding.porcentaje.text.toString() != ".") {
            values1.add(Entry(j, binding.valor.text.toString().toFloat(), R.drawable.ic_bubbles))
        }
        if (binding.valor2.text.toString() != "" && binding.porcentaje2.text.toString() != "" && binding.valor2.text.toString() != "." && binding.porcentaje2.text.toString() != ".") {
            values2.add(Entry(j, binding.valor2.text.toString().toFloat(), R.drawable.ic_bubbles))
        }

        val set1: LineDataSet
        val set2: LineDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values1
            set1.notifyDataSetChanged()
            set2 = chart.data.getDataSetByIndex(0) as LineDataSet
            set2.values = values2
            set2.notifyDataSetChanged()

            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values1, "Compra")
            set1.setDrawIcons(false)
            set1.enableDashedLine(16f, 0f, 0f)
            set1.color = R.color.P1
            set1.setCircleColor(R.color.G0)
            set1.lineWidth = 10f
            set1.circleRadius = 3f
            set1.setDrawCircleHole(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f
            set1.valueTextSize = 9f
            set1.enableDashedHighlightLine(10f, 5f, 0f)
            set1.setDrawFilled(false)
            set1.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
            set1.fillColor = R.color.B1

            set2 = LineDataSet(values2, "Venta")
            set2.setDrawIcons(false)
            set2.enableDashedLine(16f, 0f, 0f)
            set2.color = R.color.P1
            set2.setCircleColor(R.color.G0)
            set2.lineWidth = 10f
            set2.circleRadius = 3f
            set2.setDrawCircleHole(true)
            set2.formLineWidth = 1f
            set2.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set2.formSize = 15f
            set2.valueTextSize = 9f
            set2.enableDashedHighlightLine(10f, 5f, 0f)
            set2.setDrawFilled(false)
            set2.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
            set2.fillColor = R.color.R0

            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            dataSets.add(set2)
            val data = LineData(dataSets)

            chart.data = data
        }
    }
}