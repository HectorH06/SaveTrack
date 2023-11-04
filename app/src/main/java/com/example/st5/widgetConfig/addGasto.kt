package com.example.st5.widgetConfig

import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.Decoder
import com.example.st5.Index
import com.example.st5.R
import com.example.st5.database.Stlite
import com.example.st5.databinding.WidgetAddgastoBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class addGasto : Fragment() {
    lateinit var binding: WidgetAddgastoBinding

    private lateinit var gastos: List<Monto>

    private lateinit var preview: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(activity, Index::class.java)
                    startActivity(intent)
                }
            })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = WidgetAddgastoBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            montosget()
            binding.displayGastos.adapter = MontoAdapter(gastos)
        }
        return binding.root
    }

    private suspend fun montosget() {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            gastos = montoDao.getGastos()
        }
    }

    private suspend fun montosgetOrdered(filter: String): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            montoDao.getGastosOrdered(filter)
            Log.i("ALL MONTOS", gastos.toString())
        }
        return gastos
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.HConcepto.setOnClickListener {
            lifecycleScope.launch {
                gastos = montosgetOrdered("concepto")
                binding.displayGastos.adapter = MontoAdapter(gastos)
            }
        }
        binding.HValor.setOnClickListener {
            lifecycleScope.launch {
                gastos = montosgetOrdered("valor")
                binding.displayGastos.adapter = MontoAdapter(gastos)
            }
        }
        binding.HVeces.setOnClickListener {
            lifecycleScope.launch {
                gastos = montosgetOrdered("fecha")
                binding.displayGastos.adapter = MontoAdapter(gastos)
            }
        }
        binding.HEtiqueta.setOnClickListener {
            lifecycleScope.launch {
                gastos = montosgetOrdered("etiqueta")
                binding.displayGastos.adapter = MontoAdapter(gastos)
            }
        }
    }

    private inner class MontoAdapter(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val conceptoTextView: TextView,
            val valorTextView: TextView,
            val vecesTextView: TextView,
            val etiquetaTextView: TextView,
            val addM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_widgetadd, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.WConcepto)
            val valorTextView = itemView.findViewById<TextView>(R.id.WValor)
            val vecesTextView = itemView.findViewById<TextView>(R.id.WVeces)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.WEtiqueta)
            val addM = itemView.findViewById<Button>(R.id.addMonto)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                vecesTextView,
                etiquetaTextView,
                addM
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            val decoder = Decoder(requireContext())
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = decoder.format(monto.valor).toString()
            holder.vecesTextView.text = monto.veces.toString()
            lifecycleScope.launch {
                holder.etiquetaTextView.text = decoder.label(monto.etiqueta)
            }
            holder.addM.setOnClickListener {
                val intent = Intent(context, widgetProviderGasto::class.java)
                intent.action = "android.appwidget.action.APPWIDGET_UPDATE"
                intent.putExtra("IDM", monto.idmonto)

                val wId = 10000 + monto.idmonto.toInt()
                val pendingIntent = PendingIntent.getBroadcast(requireContext(), wId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                val views = RemoteViews("com.example.st5", R.layout.item_widgetfast)
                views.setOnClickPendingIntent(R.id.fastConcepto, pendingIntent)

                val appWidgetManager = AppWidgetManager.getInstance(requireContext())
                val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(requireContext(), widgetProviderGasto::class.java))
                appWidgetManager.updateAppWidget(appWidgetIds, views)

                val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds)
                requireActivity().setResult(RESULT_OK, resultValue)

                Log.v("Widget creado", monto.concepto)

                requireActivity().finish()
            }
            if (position == gastos.size - 1){
                holder.itemView.setBackgroundResource(R.drawable.p1bottomcell)
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }
    }
}