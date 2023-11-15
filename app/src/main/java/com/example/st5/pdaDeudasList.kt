package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPdadeudaslistBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.*


class pdaDeudasList : Fragment() {
    private lateinit var binding: FragmentPdadeudaslistBinding

    private lateinit var deudas: List<Monto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = planesdeahorromain()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.pda_container, prev)
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
        binding = FragmentPdadeudaslistBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_planes_de_ahorro2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_planes_de_ahorro)
            }

            Log.i("MODO", isDarkMode.toString())

            deudas = montosget()
            binding.displayMontos.adapter = MontoAdapter(deudas)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = planesdeahorromain()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.pda_container, back).addToBackStack(null).commit()
        }

        binding.HConcepto.setOnClickListener {
            lifecycleScope.launch {
                deudas = montosgetAlfabetica()
                binding.displayMontos.adapter = MontoAdapter(deudas)
            }
        }
        binding.HValor.setOnClickListener {
            lifecycleScope.launch {
                deudas = montosgetValuados()
                binding.displayMontos.adapter = MontoAdapter(deudas)
            }
        }
        binding.HVeces.setOnClickListener {
            lifecycleScope.launch {
                deudas = montosgetVeces()
                binding.displayMontos.adapter = MontoAdapter(deudas)
            }
        }
        binding.HEtiqueta.setOnClickListener {
            lifecycleScope.launch {
                deudas = montosgetEtiquetados()
                binding.displayMontos.adapter = MontoAdapter(deudas)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun montosget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            binding.bar.text = "Deudas"
            deudas = montoDao.getDeudasList()
            Log.i("ALL MONTOS", deudas.toString())
        }
        return deudas
    }

    private suspend fun montosgetAlfabetica(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            deudas = montoDao.getDeudasAlfabetica()
            Log.i("ALL MONTOS", deudas.toString())
        }
        return deudas
    }

    private suspend fun montosgetValuados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            deudas = montoDao.getDeudasValuadas()
            Log.i("ALL MONTOS", deudas.toString())
        }
        return deudas
    }

    private suspend fun montosgetVeces(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            deudas = montoDao.getDeudasVeces()
            Log.i("ALL MONTOS", deudas.toString())
        }
        return deudas
    }

    private suspend fun montosgetEtiquetados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            deudas = montoDao.getDeudasEtiquetadas()
            Log.i("ALL MONTOS", deudas.toString())
        }
        return deudas
    }

    private suspend fun montoPapelera(
        idmonto: Long,
        concepto: String,
        valor: Double,
        fecha: Int?,
        frecuencia: Int?,
        etiqueta: Int,
        interes: Double?,
        veces: Long?,
        adddate: Int
    ) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val status = 7

            val enddate = montoDao.getEnded(idmonto.toInt())
            val valorfinal = montoDao.getValorFinal(idmonto.toInt())
            val tipointeres = montoDao.getTipoInteres(idmonto.toInt())
            val delay = montoDao.getDelay(idmonto.toInt())
            val sequence = montoDao.getSequence(idmonto.toInt())
            val cooldown = montoDao.getCooldown(idmonto.toInt())
            val iduser = usuarioDao.checkId().toLong()
            val viejoMonto = Monto(
                idmonto = idmonto,
                iduser = iduser,
                concepto = concepto,
                valor = valor,
                valorfinal = valorfinal,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                interes = interes,
                tipointeres = tipointeres,
                veces = veces,
                estado = status,
                adddate = adddate,
                enddate = enddate,
                cooldown = cooldown,
                delay = delay,
                sequence = sequence
            )

            montoDao.updateMonto(viejoMonto)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())

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
            val valorfinal = montoDao.getValorFinal(idmonto.toInt())
            val tipointeres = montoDao.getTipoInteres(idmonto.toInt())
            val delay = montoDao.getDelay(idmonto.toInt())
            val sequence = montoDao.getSequence(idmonto.toInt())
            val cooldown = montoDao.getCooldown(idmonto.toInt())
            val iduser = usuarioDao.checkId().toLong()
            val viejoMonto = Monto(
                idmonto = idmonto,
                iduser = iduser,
                concepto = concepto,
                valor = valor,
                valorfinal = valorfinal,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                interes = interes,
                tipointeres = tipointeres,
                veces = veces,
                estado = status,
                adddate = adddate,
                enddate = enddate,
                cooldown = cooldown,
                delay = delay,
                sequence = sequence
            )

            montoDao.updateMonto(viejoMonto)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())

        }
    }

    suspend fun liq(
        id: Long,
        concepto: String,
        valor: Double,
        fecha: Int?,
        frecuencia: Int?,
        etiqueta: Int,
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

            val cooldown = 0

            val enddate = montoDao.getEnded(id.toInt())
            val valorfinal = montoDao.getValorFinal(id.toInt())
            val tipointeres = montoDao.getTipoInteres(id.toInt())
            val delay = montoDao.getDelay(id.toInt())
            val sequence = montoDao.getSequence(id.toInt())
            val iduser = usuarioDao.checkId().toLong()
            val montoPresionado = Monto(
                idmonto = id,
                iduser = iduser,
                concepto = concepto,
                valor = valor,
                valorfinal = valorfinal,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                interes = interes,
                tipointeres = tipointeres,
                veces = nv,
                estado = 10,
                adddate = adddate,
                enddate = enddate,
                cooldown = cooldown,
                delay = delay,
                sequence = sequence
            )

            val totalGastos = ingresoGastoDao.checkSummaryG()

            ingresoGastoDao.updateSummaryG(
                montoPresionado.iduser.toInt(),
                totalGastos + montoPresionado.valorfinal!!
            )
            montoDao.updateMonto(montoPresionado)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())
        }
    }

    private fun truncateDouble(value: Double): Double {
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(value).toDouble()
    }

    private inner class MontoAdapter(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val conceptoTextView: TextView,
            val interesTextView: TextView,
            val valorTextView: TextView,
            val valorFinalTextView: TextView,
            val fechaTextView: TextView,
            val fechaFinalTextView: TextView,
            val etiquetaTextView: TextView,
            val optionsM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_deuda, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.DConcepto)
            val interesTextView = itemView.findViewById<TextView>(R.id.DBottom)
            val valorTextView = itemView.findViewById<TextView>(R.id.DValor)
            val valorFinalTextView = itemView.findViewById<TextView>(R.id.DValorFinal)
            val fechaTextView = itemView.findViewById<TextView>(R.id.DFecha)
            val fechaFinalTextView = itemView.findViewById<TextView>(R.id.DFechaFinal)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.DEtiqueta)
            val optionsM = itemView.findViewById<Button>(R.id.itemOptions)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                interesTextView,
                valorTextView,
                valorFinalTextView,
                fechaTextView,
                fechaFinalTextView,
                etiquetaTextView,
                optionsM
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            var tempstat = 5
            val decoder = Decoder(requireContext())

            holder.conceptoTextView.text = monto.concepto
            holder.interesTextView.text = monto.interes.toString()
            holder.valorTextView.text = truncateDouble(monto.valor).toString()
            holder.valorFinalTextView.text = monto.valorfinal?.let { truncateDouble(it).toString() }
            holder.fechaTextView.text = monto.fecha?.let { decoder.date(it) }
            holder.fechaFinalTextView.text = monto.enddate?.let { decoder.date(it) }
            lifecycleScope.launch {
                holder.etiquetaTextView.text = decoder.label(monto.etiqueta)
            }

            if (monto.estado == 0 || monto.estado == 1 || monto.estado == 5 || monto.estado == 6) {
                tempstat = 5
            }
            if (monto.estado == 3 || monto.estado == 4 || monto.estado == 8 || monto.estado == 9) {
                tempstat = 8
            }

            holder.optionsM.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), holder.optionsM, Gravity.END)
                val inflater = popupMenu.menuInflater
                inflater.inflate(R.menu.options_item_deuda, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.action_favMonto -> {
                            if (tempstat == 5) {
                                tempstat = 8
                            }
                            if (tempstat == 8) {
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

                            true
                        }
                        R.id.action_verMonto -> {
                            val upup = monto.valorfinal?.let { it1 ->
                                indexmontoupdate.sendMonto(
                                    monto.idmonto,
                                    monto.concepto,
                                    it1,
                                    monto.fecha,
                                    monto.frecuencia,
                                    monto.etiqueta,
                                    monto.interes,
                                    monto.veces,
                                    monto.adddate
                                )
                            }

                            if (upup != null) {
                                parentFragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                    .replace(R.id.pda_container, upup).addToBackStack(null).commit()
                            }

                            true
                        }
                        R.id.action_liquidateMonto -> {
                            lifecycleScope.launch {
                                liq(
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
                                    .replace(R.id.pda_container, pdaDeudasList()).addToBackStack(null)
                                    .commit()
                            }

                            true
                        }
                        R.id.action_editMonto -> {
                            val upup = monto.valorfinal?.let { it1 ->
                                indexmontoupdate.sendMonto(
                                    monto.idmonto,
                                    monto.concepto,
                                    it1,
                                    monto.fecha,
                                    monto.frecuencia,
                                    monto.etiqueta,
                                    monto.interes,
                                    monto.veces,
                                    monto.adddate
                                )
                            }

                            if (upup != null) {
                                parentFragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                    .replace(R.id.pda_container, upup).addToBackStack(null).commit()
                            }

                            true
                        }
                        R.id.action_deleteMonto -> {
                            if (monto.estado == 3 || monto.estado == 4 || monto.estado == 8 || monto.estado == 9 || tempstat == 8) {
                                val confirmDialog = AlertDialog.Builder(requireContext())
                                    .setTitle("El monto ${monto.concepto} no se puede eliminar porque está marcado como favorito")
                                    .setPositiveButton("Aceptar") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .create()

                                confirmDialog.show()
                            } else {
                                val confirmDialog = AlertDialog.Builder(requireContext())
                                    .setTitle("¿Seguro que quieres enviar el monto ${monto.concepto} a la papelera?")
                                    .setPositiveButton("Eliminar") { dialog, _ ->

                                        Log.v("Id del monto actualizado", monto.idmonto.toString())
                                        Log.v("Concepto", monto.concepto)
                                        Log.v("Valor", monto.valor.toString())
                                        Log.v("Fecha", monto.fecha.toString())
                                        Log.v("Frecuencia", monto.frecuencia.toString())
                                        Log.v("Etiqueta", monto.etiqueta.toString())
                                        Log.v("Interes", monto.interes.toString())
                                        Log.v("Veces", monto.veces.toString())
                                        lifecycleScope.launch {
                                            montoPapelera(
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
                                        }
                                        dialog.dismiss()
                                        parentFragmentManager.beginTransaction()
                                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                            .replace(R.id.pda_container, pdaDeudasList()).addToBackStack(null)
                                            .commit()
                                    }
                                    .setNegativeButton("Cancelar") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .create()

                                confirmDialog.show()
                            }

                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }
    }
}