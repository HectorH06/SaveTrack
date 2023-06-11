package com.example.st5

import android.annotation.SuppressLint
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
import com.example.st5.databinding.FragmentIndexingresolistBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class indexIngresosList : Fragment() {
    private lateinit var binding: FragmentIndexingresolistBinding

    private lateinit var ingresos: List<Monto>

    companion object {
        private const val etiqueta = "etiquetar"
        fun labelSearch(etiquet: Int): indexIngresosList {
            val fragment = indexIngresosList()
            val args = Bundle()
            Log.i("etiquet", etiquet.toString())
            val labe = when (etiquet) {
                0 -> 9
                1 -> 10
                2 -> 11
                3 -> 12
                4 -> 13
                5 -> 14
                6 -> 15
                7 -> 16
                else -> null
            }
            if (labe != null) {
                args.putInt(etiqueta, labe)
            }
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
                    val prev = indexmain()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.index_container, prev)
                        .addToBackStack(null).commit()
                }
            })
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIndexingresolistBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            ingresos = montosget()
            binding.displayIngresos.adapter = MontoAdapter(ingresos)
            binding.totalI.text = "$" + totalIngresos().toString()
        }
        return binding.root
    }

    private suspend fun totalIngresos(): Double {
        var totalI: Double

        withContext(Dispatchers.IO) {
            val igDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

            totalI = igDao.checkSummaryI()
        }

        return totalI
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val label: Int? = arguments?.getInt(etiqueta)

        Log.i("etiqueta", label.toString())

        val back = indexmain()
        val addWithSwitchOn = indexadd.newInstance(true)

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.index_container, back).addToBackStack(null).commit()
        }

        binding.AgregarIngresoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, addWithSwitchOn).addToBackStack(null).commit()
        }

        binding.HConcepto.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetAlfabetica()
                if (ingresos != null) {
                    binding.displayIngresos.adapter = MontoAdapter(ingresos)
                } else {
                    Log.e("INGRESOS", "No hay ingresos")
                }
            }
        }
        binding.HValor.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetValuados()
                if (ingresos != null) {
                    binding.displayIngresos.adapter = MontoAdapter(ingresos)
                } else {
                    Log.e("INGRESOS", "No hay ingresos")
                }
            }
        }
        binding.HFecha.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetFechados()
                if (ingresos != null) {
                    binding.displayIngresos.adapter = MontoAdapter(ingresos)
                } else {
                    Log.e("INGRESOS", "No hay ingresos")
                }
            }
        }
        binding.HEtiqueta.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetEtiquetados()
                if (ingresos != null) {
                    binding.displayIngresos.adapter = MontoAdapter(ingresos)
                } else {
                    Log.e("INGRESOS", "No hay ingresos")
                }
            }
        }
    }

    private suspend fun montosget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            ingresos = if (label != null) {
                montoDao.getIngresos(label)
            } else {
                montoDao.getIngresos()
            }
            Log.i("ALL MONTOS", ingresos.toString())
        }
        return ingresos
    }

    private suspend fun montosgetAlfabetica(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            ingresos = if (label != null) {
                montoDao.getIngresosAlfabetica(label)
            } else {
                montoDao.getIngresosAlfabetica()
            }
            Log.i("ALL MONTOS", ingresos.toString())
        }
        return ingresos
    }

    private suspend fun montosgetValuados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            ingresos = if (label != null) {
                montoDao.getIngresosValuados(label)
            } else {
                montoDao.getIngresosValuados()
            }
            Log.i("ALL MONTOS", ingresos.toString())
        }
        return ingresos
    }

    private suspend fun montosgetFechados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            ingresos = if (label != null) {
                montoDao.getIngresosFechados(label)
            } else {
                montoDao.getIngresosFechados()
            }
            Log.i("ALL MONTOS", ingresos.toString())
        }
        return ingresos
    }

    private suspend fun montosgetEtiquetados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            ingresos = if (label != null) {
                montoDao.getIngresosEtiquetados(label)
            } else {
                montoDao.getIngresosEtiquetados()
            }
            Log.i("ALL MONTOS", ingresos.toString())
        }
        return ingresos
    }

    private inner class MontoAdapter(private val montos: List<Monto>) : RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val conceptoTextView: TextView,
            val valorTextView: TextView,
            val fechaTextView: TextView,
            val etiquetaTextView: TextView
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_tabla, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.IConcepto)
            val valorTextView = itemView.findViewById<TextView>(R.id.IValor)
            val fechaTextView = itemView.findViewById<TextView>(R.id.IFecha)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.IEtiqueta)
            return MontoViewHolder(itemView, conceptoTextView, valorTextView, fechaTextView, etiquetaTextView)
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = monto.valor.toString()
            holder.fechaTextView.text = monto.fecha
            holder.etiquetaTextView.text = monto.etiqueta.toString()
        }


        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }
    }
}