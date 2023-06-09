package com.example.st5

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
import com.example.st5.databinding.FragmentIndexgastoslistBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class indexGastosList : Fragment() {
    private lateinit var binding: FragmentIndexgastoslistBinding

    private lateinit var gastos: List<Monto>

    companion object {
        private const val etiqueta = "etiquetar"
        fun labelSearch(etiquet: Int): indexGastosList {
            val fragment = indexGastosList()
            val args = Bundle()
            Log.i("etiquet", etiquet.toString())
            val labe = when (etiquet) {
                0 -> 1
                1 -> 2
                2 -> 3
                3 -> 4
                4 -> 5
                5 -> 6
                6 -> 6
                7 -> 8
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIndexgastoslistBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            gastos = montosget()
            if (gastos != null) {
                binding.displayGastos.adapter = MontoAdapter(gastos)
            } else {
                Log.e("GASTOS", "No hay gastos")
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val label: Int? = arguments?.getInt(etiqueta)
        Log.i("etiqueta", label.toString())

        val back = indexmain()
        val addWithSwitchOff = indexadd.newInstance(false)

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.index_container, back).addToBackStack(null).commit()
        }

        binding.AgregarGastoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, addWithSwitchOff).addToBackStack(null).commit()
        }

        binding.HConcepto.setOnClickListener {
            lifecycleScope.launch {
                gastos = montosgetAlfabetica()
                if (gastos != null) {
                    binding.displayGastos.adapter = MontoAdapter(gastos)
                } else {
                    Log.e("GASTOS", "No hay gastos")
                }
            }
        }
        binding.HValor.setOnClickListener {
            lifecycleScope.launch {
                gastos = montosgetValuados()
                if (gastos != null) {
                    binding.displayGastos.adapter = MontoAdapter(gastos)
                } else {
                    Log.e("GASTOS", "No hay gastos")
                }
            }
        }
        binding.HFecha.setOnClickListener {
            lifecycleScope.launch {
                gastos = montosgetFechados()
                if (gastos != null) {
                    binding.displayGastos.adapter = MontoAdapter(gastos)
                } else {
                    Log.e("GASTOS", "No hay gastos")
                }
            }
        }
        binding.HEtiqueta.setOnClickListener {
            lifecycleScope.launch {
                gastos = montosgetEtiquetados()
                if (gastos != null) {
                    binding.displayGastos.adapter = MontoAdapter(gastos)
                } else {
                    Log.e("GASTOS", "No hay gastos")
                }
            }
        }
    }

    private suspend fun montosget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            gastos = if (label != null) {
                montoDao.getGastos(label)
            } else {
                montoDao.getGastos()
            }
            Log.i("ALL MONTOS", gastos.toString())
        }
        return gastos
    }

    private suspend fun montosgetAlfabetica(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            gastos = if (label != null) {
                montoDao.getGastosAlfabetica(label)
            } else {
                montoDao.getGastosAlfabetica()
            }
            Log.i("ALL MONTOS", gastos.toString())
        }
        return gastos
    }

    private suspend fun montosgetValuados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            gastos = if (label != null) {
                montoDao.getGastosValuados(label)
            } else {
                montoDao.getGastosValuados()
            }
            Log.i("ALL MONTOS", gastos.toString())
        }
        return gastos
    }

    private suspend fun montosgetFechados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            gastos = if (label != null) {
                montoDao.getGastosFechados(label)
            } else {
                montoDao.getGastosFechados()
            }
            Log.i("ALL MONTOS", gastos.toString())
        }
        return gastos
    }

    private suspend fun montosgetEtiquetados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            gastos = if (label != null) {
                montoDao.getGastosEtiquetados(label)
            } else {
                montoDao.getGastosEtiquetados()
            }
            Log.i("ALL MONTOS", gastos.toString())
        }
        return gastos
    }

    private inner class MontoAdapter(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val conceptoTextView: TextView,
            val valorTextView: TextView,
            val fechaTextView: TextView,
            val etiquetaTextView: TextView
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_tabla, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.IConcepto)
            val valorTextView = itemView.findViewById<TextView>(R.id.IValor)
            val fechaTextView = itemView.findViewById<TextView>(R.id.IFecha)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.IEtiqueta)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                fechaTextView,
                etiquetaTextView
            )
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