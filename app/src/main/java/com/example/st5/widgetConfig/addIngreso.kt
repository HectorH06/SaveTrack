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
import com.example.st5.databinding.WidgetAddingresoBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class addIngreso : Fragment() {
    lateinit var binding: WidgetAddingresoBinding

    private lateinit var ingresos: List<Monto>
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
        binding = WidgetAddingresoBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            montosget()
            binding.displayIngresos.adapter = MontoAdapter(ingresos)
        }
        return binding.root
    }

    private suspend fun montosget() {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            ingresos = montoDao.getIngresos()
        }
    }

    private suspend fun montosgetOrdered(filter: String): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            montoDao.getIngresosOrdered(filter)
            Log.i("ALL MONTOS", ingresos.toString())
        }
        return ingresos
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.HConcepto.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetOrdered("concepto")
                binding.displayIngresos.adapter = MontoAdapter(ingresos)
            }
        }
        binding.HValor.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetOrdered("valor")
                binding.displayIngresos.adapter = MontoAdapter(ingresos)
            }
        }
        binding.HVeces.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetOrdered("fecha")
                binding.displayIngresos.adapter = MontoAdapter(ingresos)
            }
        }
        binding.HEtiqueta.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetOrdered("etiqueta")
                binding.displayIngresos.adapter = MontoAdapter(ingresos)
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
                val appWidgetManager = AppWidgetManager.getInstance(requireContext())
                val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(requireContext(), widgetProviderIngreso::class.java))
                val idw = appWidgetIds.last()
                Log.v("IDW", "$idw")
                val intent = Intent(context, widgetProviderIngreso::class.java)
                intent.action = "android.appwidget.action.APPWIDGET_UPDATE"
                intent.putExtra("IDM", monto.idmonto)
                intent.putExtra("IDW", idw)

                val wId = 10000 + monto.idmonto.toInt()
                val pendingIntent = PendingIntent.getBroadcast(requireContext(), wId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                val views = RemoteViews("com.example.st5", R.layout.item_widgetfast)
                views.setOnClickPendingIntent(R.id.fastConcepto, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetIds, views)

                val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds)
                requireActivity().setResult(RESULT_OK, resultValue)

                Log.v("Widget creado", monto.concepto)

                requireActivity().finish()
            }
            if (position == ingresos.size - 1){
                holder.itemView.setBackgroundResource(R.drawable.p1bottomcell)
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }
    }
}