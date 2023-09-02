package com.example.st5

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentIndexmandadosBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*


class indexmandados : Fragment() {
    private lateinit var binding: FragmentIndexmandadosBinding

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
        binding = FragmentIndexmandadosBinding.inflate(inflater, container, false)
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

            val fechaActual = LocalDate.now().toString()
            val today: Int = fechaActual.replace("-", "").toInt()
            val addd: Int = today

            Log.i("todayyyy", today.toString())

            fastable = montoDao.getGXFecha(100, 100, 100, 100, addd)
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
            val valorTextView: TextView,
            val skipButton: ImageButton,
            val delayButton: ImageButton
        ) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_fastadd, parent, false)
            val vecesTextView = itemView.findViewById<TextView>(R.id.fastVeces)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.fastNombre)
            val valorTextView = itemView.findViewById<TextView>(R.id.fastValor)
            val skipButton = itemView.findViewById<ImageButton>(R.id.skipMonto)
            val delayButton = itemView.findViewById<ImageButton>(R.id.delayMonto)
            return MontoViewHolder(itemView, vecesTextView, conceptoTextView, valorTextView, skipButton, delayButton)
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]

            if (monto.delay >= 1) holder.itemView.setBackgroundResource(R.drawable.fastshapedelayed)

            if (monto.delay >= 3) holder.itemView.setBackgroundResource(R.drawable.fastshapeurgent)

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
                        monto.estado,
                        monto.adddate
                    )

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.index_container, indexmandados()).addToBackStack(null)
                        .commit()
                }
            }
            holder.vecesTextView.text = monto.veces.toString()
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = monto.valor.toString()
            holder.skipButton.setOnClickListener {
                lifecycleScope.launch {
                    skip(
                        monto.idmonto,
                        monto.concepto,
                        monto.valor,
                        monto.fecha,
                        monto.frecuencia,
                        monto.etiqueta,
                        monto.interes,
                        monto.veces,
                        monto.estado,
                        monto.adddate
                    )

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.index_container, indexmandados()).addToBackStack(null)
                        .commit()
                }
            }
            holder.delayButton.setOnClickListener {
                lifecycleScope.launch {
                    delay(
                        monto.idmonto,
                        monto.concepto,
                        monto.valor,
                        monto.fecha,
                        monto.frecuencia,
                        monto.etiqueta,
                        monto.interes,
                        monto.veces,
                        monto.estado,
                        monto.adddate
                    )

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.index_container, indexmandados()).addToBackStack(null)
                        .commit()
                }
            }
        }

        suspend fun fup(
            id: Long,
            concepto: String,
            valor: Double,
            fecha: Int?,
            frecuencia: Int?,
            etiqueta: Int,
            interes: Double?,
            veces: Long?,
            estado: Int?,
            adddate: Int
        ) {
            withContext(Dispatchers.IO) {
                val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                val ingresoGastoDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

                var nv: Long? = 1
                if (veces != null)
                    nv = veces + 1

                var status = estado
                var delay = montoDao.getDelay(id.toInt())
                if (delay != 0) {
                    delay -= 1
                    delay = maxOf(delay, 0)
                } else {
                    when (estado) {
                        0 -> status = 1
                        3 -> status = 4
                        5 -> status = 6
                        8 -> status = 9
                    }
                }
                var cooldown = 0
                when (frecuencia) {
                    14 -> cooldown = 1
                    61 -> cooldown = 1
                    91 -> cooldown = 2
                    122 -> cooldown = 3
                    183 -> cooldown = 5
                    365 -> cooldown = 11
                }

                val sequence = montoDao.getSequence(id.toInt())
                val values = sequence.trim('.').split('.').map { it.toInt() }.toMutableList()
                if (values.isNotEmpty()) {
                    val lastIndex = values.size - 1
                    values[lastIndex] += 1
                }
                val updatedString = values.joinToString(".")
                val result = "$updatedString."
                val tipointeres = montoDao.getTipoInteres(id.toInt())
                val enddate = montoDao.getEnded(id.toInt())
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
                    tipointeres = tipointeres,
                    veces = nv,
                    estado = status,
                    adddate = adddate,
                    enddate = enddate,
                    cooldown = cooldown,
                    delay = delay,
                    sequence = result
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

        suspend fun skip(
            id: Long,
            concepto: String,
            valor: Double,
            fecha: Int?,
            frecuencia: Int?,
            etiqueta: Int,
            interes: Double?,
            veces: Long?,
            estado: Int?,
            adddate: Int
        ) {
            withContext(Dispatchers.IO) {
                val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

                var status = estado
                var delay = montoDao.getDelay(id.toInt())
                if (delay != 0) {
                    delay -= 1
                    delay = maxOf(delay, 0)
                } else {
                    when (estado) {
                        0 -> status = 1
                        3 -> status = 4
                        5 -> status = 6
                        8 -> status = 9
                    }
                }
                var cooldown = 0
                when (frecuencia) {
                    14 -> cooldown = 1
                    61 -> cooldown = 1
                    91 -> cooldown = 2
                    122 -> cooldown = 3
                    183 -> cooldown = 5
                    365 -> cooldown = 11
                }
                val sequence = montoDao.getSequence(id.toInt())
                val tipointeres = montoDao.getTipoInteres(id.toInt())
                val enddate = montoDao.getEnded(id.toInt())
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
                    tipointeres = tipointeres,
                    veces = veces,
                    estado = status,
                    adddate = adddate,
                    enddate = enddate,
                    delay = delay,
                    sequence = sequence,
                    cooldown = cooldown
                )

                montoDao.updateMonto(montoPresionado)
                val montos = montoDao.getMonto()
                Log.i("ALL MONTOS", montos.toString())
            }
        }

        suspend fun delay(
            id: Long,
            concepto: String,
            valor: Double,
            fecha: Int?,
            frecuencia: Int?,
            etiqueta: Int,
            interes: Double?,
            veces: Long?,
            estado: Int?,
            adddate: Int
        ) {
            withContext(Dispatchers.IO) {
                val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

                var status = estado
                when (estado) {
                    0 -> status = 1
                    3 -> status = 4
                    5 -> status = 6
                    8 -> status = 9
                }
                var cooldown = 0
                when (frecuencia) {
                    14 -> cooldown = 1
                    61 -> cooldown = 1
                    91 -> cooldown = 2
                    122 -> cooldown = 3
                    183 -> cooldown = 5
                    365 -> cooldown = 11
                }
                val delay = montoDao.getDelay(id.toInt()) + 1
                val sequence = montoDao.getSequence(id.toInt())
                val tipointeres = montoDao.getTipoInteres(id.toInt())
                val enddate = montoDao.getEnded(id.toInt())
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
                    tipointeres = tipointeres,
                    veces = veces,
                    estado = status,
                    adddate = adddate,
                    enddate = enddate,
                    delay = delay,
                    sequence = sequence,
                    cooldown = cooldown
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