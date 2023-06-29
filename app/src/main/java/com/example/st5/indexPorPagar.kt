package com.example.st5

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
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
import com.example.st5.databinding.FragmentIndexporpagarBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*


class indexPorPagar : Fragment() {
    private lateinit var binding: FragmentIndexporpagarBinding

    private lateinit var fastable: List<Monto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_index2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_index)
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

        withContext(Dispatchers.IO) {
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
        binding = FragmentIndexporpagarBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            fastable = fastget()
            binding.checkList.adapter = MontoAdapter(fastable)
            binding.totalG.text = totalGastos()

            binding.empty.animate()
                .alpha(0f)
                .start()
            if (fastable.isEmpty()){
                binding.empty.animate()
                    .alpha(1f)
                    .start()
            }
        }
        return binding.root
    }

    private suspend fun totalGastos(): String {
        var totalG: Double

        withContext(Dispatchers.IO) {
            val igDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

            totalG = igDao.checkSummaryG()
        }

        return totalG.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = indexmain()
        val addWithSwitchOn = indexadd.newInstance(false)

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.index_container, back).addToBackStack(null).commit()
        }

        binding.AgregarGastoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, addWithSwitchOn).addToBackStack(null).commit()
        }
    }

    private suspend fun fastget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val fechaActual = LocalDate.now()
            val today = fechaActual.toString()

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val truefecha = formatoFecha.parse(today)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dm = calendar.get(Calendar.DAY_OF_MONTH)
            val dom = String.format("%02d", dm)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = "Diario"
            when (w) {
                1 -> dow = "Sunday"
                2 -> dow = "Monday"
                3 -> dow = "Tuesday"
                4 -> dow = "Wednesday"
                5 -> dow = "Thursday"
                6 -> dow = "Friday"
                7 -> dow = "Saturday"
            }

            val addd: Int = today.replace("-", "").toInt()

            Log.i("DOM", dom)
            Log.i("DOW", dow)

            Log.i("todayyyy", today)

            fastable = montoDao.getGXFecha(today, dom, dow, "Diario", addd)
            Log.i("ALL TODOLIST", fastable.toString())
        }
        return fastable
    }

    private inner class MontoAdapter(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val vecesTextView: TextView,
            val conceptoTextView: TextView,
            val valorTextView: TextView
        ) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_fastadd, parent, false)
            val vecesTextView = itemView.findViewById<TextView>(R.id.fastVeces)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.fastNombre)
            val valorTextView = itemView.findViewById<TextView>(R.id.fastValor)
            return MontoViewHolder(itemView, vecesTextView, conceptoTextView, valorTextView)
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            holder.itemView.setOnClickListener {
                lifecycleScope.launch {
                    fup(
                        monto.idmonto,
                        monto.concepto,
                        monto.valor,
                        monto.fecha,
                        monto.frecuencia,
                        monto.etiqueta,
                        monto.interes,
                        monto.veces,
                        monto.adddate
                    )

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.index_container, indexPorPagar()).addToBackStack(null)
                        .commit()
                }
            }
            holder.vecesTextView.text = monto.veces.toString()
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = monto.valor.toString()
        }

        suspend fun fup(
            id: Long,
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
                val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                val ingresoGastoDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

                var nv: Long? = 1
                if (veces != null)
                    nv = veces + 1

                val iduser = usuarioDao.checkId().toLong()
                val montoPresionado = Monto(
                    idmonto = id,
                    iduser = iduser,
                    concepto = concepto,
                    valor = valor,
                    fecha = fecha,
                    frecuencia = frecuencia,
                    etiqueta = etiqueta,
                    interes = interes,
                    veces = nv,
                    estado = 1,
                    adddate = adddate
                )

                val totalGastos = ingresoGastoDao.checkSummaryG()

                ingresoGastoDao.updateSummaryG(
                    montoPresionado.iduser.toInt(),
                    totalGastos + montoPresionado.valor
                )
                montoDao.updateMonto(montoPresionado)
                val montos = montoDao.getMonto()
                Log.i("ALL MONTOS", montos.toString())
            }
        }

        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }
    }
}