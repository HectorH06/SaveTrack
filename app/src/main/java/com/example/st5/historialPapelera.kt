package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentHistorialpapeleraBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class historialPapelera : Fragment() {
    private lateinit var binding: FragmentHistorialpapeleraBinding

    private lateinit var montosp: List<Monto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_historial2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_historial)
            }

            Log.i("MODO", isDarkMode.toString())
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = indexmain()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.index_container, prev)
                        .addToBackStack(null).commit()
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

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistorialpapeleraBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            montosp = montosget()
            binding.displayMontos.adapter = MontoAdapter(montosp)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = historialmain()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.historial_container, back).addToBackStack(null).commit()
        }

        binding.HConcepto.setOnClickListener {
            lifecycleScope.launch {
                montosp = montosgetAlfabetica()
                binding.displayMontos.adapter = MontoAdapter(montosp)
            }
        }
        binding.HValor.setOnClickListener {
            lifecycleScope.launch {
                montosp = montosgetValuados()
                binding.displayMontos.adapter = MontoAdapter(montosp)
            }
        }
        binding.HVeces.setOnClickListener {
            lifecycleScope.launch {
                montosp = montosgetVeces()
                binding.displayMontos.adapter = MontoAdapter(montosp)
            }
        }
        binding.HEtiqueta.setOnClickListener {
            lifecycleScope.launch {
                montosp = montosgetEtiquetados()
                binding.displayMontos.adapter = MontoAdapter(montosp)
            }
        }
    }

    private suspend fun montosget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            montosp = montoDao.getPapelera()
            Log.i("ALL MONTOS", montosp.toString())
        }
        return montosp
    }

    private suspend fun montosgetAlfabetica(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            montosp = montoDao.getPapelera()
            Log.i("ALL MONTOS", montosp.toString())
        }
        return montosp
    }

    private suspend fun montosgetValuados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            montosp = montoDao.getPapelera()
            Log.i("ALL MONTOS", montosp.toString())
        }
        return montosp
    }

    private suspend fun montosgetVeces(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            montosp = montoDao.getPapelera()
            Log.i("ALL MONTOS", montosp.toString())
        }
        return montosp
    }

    private suspend fun montosgetEtiquetados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            montosp = montoDao.getPapelera()
            Log.i("ALL MONTOS", montosp.toString())
        }
        return montosp
    }

    private suspend fun montoback(
        idmonto: Long,
        concepto: String,
        valor: Double,
        fecha: String,
        frecuencia: Long?,
        etiqueta: Long,
        interes: Double?,
        veces: Long?,
        adddate: Int
    ) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val iduser = usuarioDao.checkId().toLong()
            val viejoMonto = Monto(
                idmonto = idmonto,
                iduser = iduser,
                concepto = concepto,
                valor = valor,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                interes = interes,
                veces = veces,
                estado = 0,
                adddate = adddate
            )

            montoDao.updateMonto(viejoMonto)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())

        }
    }

    private suspend fun montodelete(
        idmonto: Long,
        concepto: String,
        valor: Double,
        fecha: String,
        frecuencia: Long?,
        etiqueta: Long,
        interes: Double?,
        veces: Long?,
        adddate: Int
    ) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val iduser = usuarioDao.checkId().toLong()
            val muertoMonto = Monto(
                idmonto = idmonto,
                iduser = iduser,
                concepto = concepto,
                valor = valor,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                interes = interes,
                veces = veces,
                adddate = adddate
            )

            montoDao.deleteMonto(muertoMonto)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())

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
            val zM: Button,
            val deleteM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_papelera, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.PConcepto)
            val valorTextView = itemView.findViewById<TextView>(R.id.PValor)
            val vecesTextView = itemView.findViewById<TextView>(R.id.PVeces)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.PEtiqueta)
            val zM = itemView.findViewById<Button>(R.id.getbackMonto)
            val deleteM = itemView.findViewById<Button>(R.id.killMonto)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                vecesTextView,
                etiquetaTextView,
                zM,
                deleteM
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = monto.valor.toString()
            holder.vecesTextView.text = monto.veces.toString()
            holder.etiquetaTextView.text = monto.etiqueta.toString()
            holder.zM.setOnClickListener {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres recuperar el monto ${monto.concepto}?")
                    .setPositiveButton("Recuperar") { dialog, _ ->

                        Log.v("Id del monto actualizado", monto.idmonto.toString())
                        Log.v("Concepto", monto.concepto)
                        Log.v("Valor", monto.valor.toString())
                        Log.v("Fecha", monto.fecha)
                        Log.v("Frecuencia", monto.frecuencia.toString())
                        Log.v("Etiqueta", monto.etiqueta.toString())
                        Log.v("Interes", monto.interes.toString())
                        Log.v("Veces", monto.veces.toString())
                        lifecycleScope.launch {
                            montoback(monto.idmonto, monto.concepto, monto.valor, monto.fecha, monto.frecuencia, monto.etiqueta, monto.interes, monto.veces, monto.adddate)
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                            .replace(R.id.index_container, indexmain()).addToBackStack(null).commit()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                confirmDialog.show()
            }
            holder.deleteM.setOnClickListener {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres eliminar el monto ${monto.concepto}? Esta acción no se puede deshacer")
                    .setPositiveButton("Guardar") { dialog, _ ->

                        Log.v("Id del monto actualizado", monto.idmonto.toString())
                        Log.v("Concepto", monto.concepto)
                        Log.v("Valor", monto.valor.toString())
                        Log.v("Fecha", monto.fecha)
                        Log.v("Frecuencia", monto.frecuencia.toString())
                        Log.v("Etiqueta", monto.etiqueta.toString())
                        Log.v("Interes", monto.interes.toString())
                        Log.v("Veces", monto.veces.toString())
                        lifecycleScope.launch {
                            montodelete(monto.idmonto, monto.concepto, monto.valor, monto.fecha, monto.frecuencia, monto.etiqueta, monto.interes, monto.veces, monto.adddate)
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
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
    }
}