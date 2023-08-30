package com.example.st5

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPlanesdeahorromainBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class planesdeahorromain : Fragment() {
    private lateinit var binding: FragmentPlanesdeahorromainBinding

    private lateinit var pda: List<Monto>
    private var dollarValue: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_planes_de_ahorro2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_planes_de_ahorro)
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
        binding = FragmentPlanesdeahorromainBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            pda = montosget()
            binding.displayPda.adapter = MontoAdapter(pda)
        }
        return binding.root
    }

    private suspend fun montosget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            pda = montoDao.getPDADeudas()
            Log.i("ALL MONTOS", pda.toString())
        }
        return pda
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.config.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.pda_container, Configuracion()).addToBackStack(null).commit()
        }

        suspend fun mostrarDatos() {
            withContext(Dispatchers.IO) {
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

                val diasaho = usuarioDao.checkDiasaho()
                val totalIngresos = ingresosGastosDao.checkSummaryI()
                val totalGastos = ingresosGastosDao.checkSummaryG()
                val totalisimo = totalIngresos - totalGastos
                val balance = "$totalisimo$"
                usuarioDao.updateBalance(usuarioDao.checkId(), totalisimo)

                val durl = "http://savetrack.com.mx/dlrval.php"
                val queue: RequestQueue = Volley.newRequestQueue(requireContext())
                val checkDollar = StringRequest(
                    Request.Method.GET, durl,
                    { response ->
                        dollarValue = response.toString()
                        Log.d("DÓLAR HOY", dollarValue)
                        binding.PACurrencyButton.text = buildString {
                            append("Dolar HOY: ")
                            append("$$dollarValue")
                        }
                    },
                    { error ->
                        Toast.makeText(
                            requireContext(), "No se ha podido conectar al valor del dólar hoy", Toast.LENGTH_SHORT
                        ).show()
                        binding.PACurrencyButton.text = buildString {
                            append("Sin conexión")
                        }
                        Log.d("error => $error", "SIE API ERROR")
                    }
                )
                queue.add(checkDollar)

                Log.v("Diasaho", diasaho.toString())
                Log.v("Balance", balance)

                binding.PADiasAhorrandoButton.text = buildString {
                    append("¡")
                    append(diasaho.toString())
                    append(" días ahorrando!")
                }
                binding.PASaldoActual.text = buildString {
                    append("Balance: ")
                    append(balance)
                }
                binding.PASaldoActual.setOnClickListener {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.pda_container, pdaDeudasList()).addToBackStack(null).commit()
                }
            }
        }
        lifecycleScope.launch {
            mostrarDatos()
        }
    }

    private suspend fun montoFavorito(
        idmonto: Long,
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
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val status = when (estado) {
                0 -> 3
                1 -> 4
                5 -> 8
                6 -> 9

                3 -> 0
                4 -> 1
                8 -> 5
                9 -> 6

                else -> 3
            }
            val enddate = montoDao.getEnded(idmonto.toInt())
            val cooldown = montoDao.getCooldown(idmonto.toInt())
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
                estado = status,
                adddate = adddate,
                enddate = enddate,
                cooldown = cooldown
            )

            montoDao.updateMonto(viejoMonto)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())

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

            var status = 6
            when (estado) {
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

            val sequence = montoDao.getSequence(id.toInt())
            val values = sequence.trim('.').split('.').map { it.toInt() }.toMutableList()
            if (values.isNotEmpty()) {
                val lastIndex = values.size - 1
                values[lastIndex] += 1
            }
            val updatedString = values.joinToString(".")
            val result = "$updatedString."

            var delay = montoDao.getDelay(id.toInt()) - 1
            delay = maxOf(delay, 0)
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
            when (estado) {
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
            var delay = montoDao.getDelay(id.toInt()) - 1
            delay = maxOf(delay, 0)
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
                veces = veces,
                estado = status,
                adddate = adddate,
                enddate = enddate,
                delay = delay,
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

            var cooldown = 0
            when (frecuencia) {
                14 -> cooldown = 1
                61 -> cooldown = 1
                91 -> cooldown = 2
                122 -> cooldown = 3
                183 -> cooldown = 5
                365 -> cooldown = 11
            }

            val delay = if (montoDao.getDelay(id.toInt()) != 0) {
                montoDao.getDelay(id.toInt()) + 1
            } else {
                2
            }
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
                veces = veces,
                estado = estado,
                adddate = adddate,
                enddate = enddate,
                cooldown = cooldown,
                delay = delay
            )

            montoDao.updateMonto(montoPresionado)
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
            val fechaTextView: TextView,
            val favM: Button,
            val check: Button,
            val skip: Button,
            val delay: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pda, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.pdaNombre)
            val valorTextView = itemView.findViewById<TextView>(R.id.pdaValor)
            val fechaTextView = itemView.findViewById<TextView>(R.id.pdaFecha)
            val favM = itemView.findViewById<Button>(R.id.favMonto)
            val check = itemView.findViewById<Button>(R.id.check)
            val skip = itemView.findViewById<Button>(R.id.skip)
            val delay = itemView.findViewById<Button>(R.id.delay)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                fechaTextView,
                favM,
                check,
                skip,
                delay
            )
        }


        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            var tempstat = 5
            holder.itemView.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.pda_container, pdaDeudasList()).addToBackStack(null).commit()
            }
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = "${monto.valor}$"
            holder.fechaTextView.text = monto.fecha.toString()
            if (monto.estado == 5 || monto.estado == 6){
                holder.favM.setBackgroundResource(R.drawable.ic_notstar)
                tempstat = 5
            }
            if (monto.estado == 8 || monto.estado == 9){
                holder.favM.setBackgroundResource(R.drawable.ic_star)
                tempstat = 8
            }
            holder.favM.setOnClickListener {
                if (tempstat == 5){
                    holder.favM.setBackgroundResource(R.drawable.ic_star)
                    tempstat = 8
                }
                if (tempstat == 8){
                    holder.favM.setBackgroundResource(R.drawable.ic_notstar)
                    tempstat = 5
                }
                lifecycleScope.launch {
                    montoFavorito(
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
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.pda_container, planesdeahorromain()).addToBackStack(null)
                    .commit()
            }
            holder.check.setOnClickListener {
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
                        .replace(R.id.index_container, indexPorPagar()).addToBackStack(null)
                        .commit()
                }
            }
            holder.skip.setOnClickListener {
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
                        .replace(R.id.index_container, indexPorPagar()).addToBackStack(null)
                        .commit()
                }
            }
            holder.delay.setOnClickListener {
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
                        .replace(R.id.index_container, indexPorPagar()).addToBackStack(null)
                        .commit()
                }
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }
    }
}