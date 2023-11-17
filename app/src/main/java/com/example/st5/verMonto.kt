package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentVermontoBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class verMonto : Fragment() {
    private lateinit var binding: FragmentVermontoBinding

    private lateinit var theMonto: Monto

    private var mutableEtiquetas: MutableList<String> = mutableListOf()
    private var mutableIds: MutableList<Long> = mutableListOf()
    private var mutableColores: MutableList<Int> = mutableListOf()

    companion object {
        private const val idv = "idm"
        fun sendMonto(
            idm: Long
        ): verMonto {
            val fragment = verMonto()
            val args = Bundle()
            args.putLong(idv, idm)
            Log.i("idv", idv)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.historial_container, historialmain())
                        .addToBackStack(null).commit()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.index_container, indexmain())
                        .addToBackStack(null).commit()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.pda_container, planesdeahorromain())
                        .addToBackStack(null).commit()
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
        binding = FragmentVermontoBinding.inflate(inflater, container, false)
        val idm = arguments?.getLong(idv)
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
            }

            Log.i("MODO", isDarkMode.toString())

            if (idm != null) {
                getMonto(idm.toInt())
            }
            binding.displayData.adapter = MontoAdapter(theMonto)
            binding.bar.text = theMonto.concepto
        }
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            getLabels()
        }

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.historial_container, historialmain())
                .addToBackStack(null).commit()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.index_container, indexmain())
                .addToBackStack(null).commit()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.pda_container, planesdeahorromain())
                .addToBackStack(null).commit()
        }
        
        binding.Edit.setOnClickListener {
            val upup = theMonto.valorfinal?.let { it1 ->
                indexmontoupdate.sendMonto(
                    theMonto.idmonto,
                    theMonto.concepto,
                    it1,
                    theMonto.fecha,
                    theMonto.frecuencia,
                    theMonto.etiqueta,
                    theMonto.interes,
                    theMonto.veces,
                    theMonto.adddate
                )
            }

            if (upup != null) {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.pda_container, upup).addToBackStack(null).commit()
            }
        }

        binding.Cancel.setOnClickListener {
            val cancelDialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Seguro que quieres descartar cambios?")
                .setPositiveButton("Descartar") { dialog, _ ->
                    dialog.dismiss()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.historial_container, historialmain())
                        .addToBackStack(null).commit()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.index_container, indexmain())
                        .addToBackStack(null).commit()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.pda_container, planesdeahorromain())
                        .addToBackStack(null).commit()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            cancelDialog.show()
        }

    }

    private suspend fun getMonto(idm: Int) {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            theMonto = montoDao.getM(idm)
        }
    }

    private suspend fun getLabels() {
        withContext(Dispatchers.IO) {
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val max = labelsDao.getMaxLabel()

            mutableIds.add(0)
            mutableEtiquetas.add("Seleccionar")
            mutableColores.add(222222)
            for (i in 1..max) {

                if (labelsDao.getPlabel(i) != null){
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

    private inner class MontoAdapter(private val monto: Monto) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val nombreTextView: TextView,
            val valorTextView: TextView,
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_datamonto, parent, false)
            val nombreTextView = itemView.findViewById<TextView>(R.id.DNombre)
            val valorTextView = itemView.findViewById<TextView>(R.id.DValor)
            return MontoViewHolder(
                itemView,
                nombreTextView,
                valorTextView
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val decoder = Decoder(requireContext())
            lifecycleScope.launch {
                when (monto.estado) {
                    in 0 .. 4 -> {
                        holder.nombreTextView.text = when (position) {
                            0 -> "Concepto"
                            1 -> "Valor"
                            2 -> "Fecha"
                            3 -> "Frecuencia"
                            4 -> "Etiqueta"
                            5 -> "Veces"
                            6 -> {
                                holder.itemView.setBackgroundResource(R.drawable.p1bottomcell)
                                "Fecha creado"
                            }
                            else -> ""
                        }
                        holder.valorTextView.text = when (position) {
                            0 -> monto.concepto
                            1 -> decoder.format(monto.valor).toString()
                            2 -> monto.fecha?.let { decoder.date(it) }
                            3 -> "${monto.frecuencia}"
                            4 -> decoder.label(monto.etiqueta)
                            5 -> "${monto.veces}"
                            6 -> monto.adddate.let { decoder.date(it) }
                            else -> ""
                        }
                    }
                    in 5 .. 9 -> {
                        holder.nombreTextView.text = when (position) {
                            0 -> "Concepto"
                            1 -> "Valor"
                            2 -> "Valor final"
                            3 -> "Fecha"
                            4 -> "Frecuencia"
                            5 -> "Etiqueta"
                            6 -> "Interés"
                            7 -> "Tipo de interés"
                            8 -> "Veces"
                            9 -> "Fecha creado"
                            10 -> {
                                holder.itemView.setBackgroundResource(R.drawable.p1bottomcell)
                                "Fecha final"
                            }
                            else -> ""
                        }
                        holder.valorTextView.text = when (position) {
                            0 -> monto.concepto
                            1 -> decoder.format(monto.valor).toString()
                            2 -> monto.valorfinal?.let { decoder.format(it).toString() }
                            3 -> monto.fecha?.let { decoder.date(it) }
                            4 -> "${monto.frecuencia}"
                            5 -> decoder.label(monto.etiqueta)
                            6 -> "${monto.interes}"
                            7 -> when (monto.tipointeres) {
                                0 -> "Fijo"
                                1 -> "Compuesto"
                                else -> ""
                            }
                            8 -> "${monto.veces}"
                            9 -> monto.adddate.let { decoder.date(it) }
                            10 -> monto.enddate?.let { decoder.date(it) }.toString()
                            else -> ""
                        }
                    }
                    else -> {

                    }
                }
            }




        }


        override fun getItemCount(): Int {
            val retornable = when (monto.estado) {
                in 0 .. 4 -> 7
                in 5 .. 9 -> 11
                else -> 1
            }
            Log.v("características de monto", "3")
            return retornable
        }
    }
}