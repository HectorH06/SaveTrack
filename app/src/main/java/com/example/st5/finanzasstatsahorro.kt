package com.example.st5

import android.content.Context
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentFinanzasstatsahorroBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class finanzasstatsahorro : Fragment() {

    private lateinit var binding: FragmentFinanzasstatsahorroBinding

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
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val back = finanzasmain();

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

        binding.displaycharts.adapter = ChartAdapter()
    }

    private fun setData(count: Int, range: Float, chart: LineChart) {
        val values = ArrayList<Entry>()

        for (i in 0 until count) {
            values.add(Entry(i.toFloat(), range, resources.getDrawable(R.drawable.ic_bubbles)))
        }

        val set1: LineDataSet
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "DataSet 1")
            set1.setDrawIcons(false)

            set1.enableDashedLine(10f, 5f, 0f)

            set1.color = R.color.P1
            set1.setCircleColor(R.color.R0)

            set1.lineWidth = 1f
            set1.circleRadius = 3f

            set1.setDrawCircleHole(false)

            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            set1.valueTextSize = 9f

            set1.enableDashedHighlightLine(10f, 5f, 0f)

            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

            set1.fillColor = R.color.G2
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)

            val data = LineData(dataSets)

            chart.data = data
        }
    }

    private inner class ChartAdapter() :
        RecyclerView.Adapter<ChartAdapter.ViewHolder>() {
        inner class ViewHolder(
            itemView: View,
            val moneda: TextView,
            val valor: TextView,
            val porcentaje: TextView,
            val chart: LineChart
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chart, parent, false)
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
            val range = 20F

            when (position){
                0 -> {
                    moneda = "Estático"
                    valor = 10.0F
                    porcentaje = 20.1F

                    setData(count, range, chart)
                }
                1 -> {
                    moneda = "Inversión"
                    valor = 20.1F
                    porcentaje = 30.2F

                    setData(count, range, chart)
                }
                2 -> {
                    moneda = "Moneda"
                    valor = 30.2F
                    porcentaje = 40.3F

                    setData(count, range, chart)
                }
                else -> {
                    moneda = "Moneda"
                    valor = 0.0F
                    porcentaje = 0.0F

                    setData(0, 0F, chart)
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