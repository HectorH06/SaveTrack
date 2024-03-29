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
import com.example.st5.databinding.FragmentIndexingresolistBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class indexIngresosList : Fragment() {
    private lateinit var binding: FragmentIndexingresolistBinding

    private lateinit var ingresos: List<Monto>
    private lateinit var fastable: List<Monto>

    companion object {
        private const val etiqueta = "etiquetar"
        fun labelSearch(etiquet: Int): indexIngresosList {
            val fragment = indexIngresosList()
            val args = Bundle()
            Log.i("etiquet", etiquet.toString())
            val labe = when (etiquet) {
                0 -> 10001
                1 -> 10002
                2 -> 10003
                3 -> 10004
                4 -> 10005
                5 -> 10006
                6 -> 10007
                7 -> 10008
                else -> null
            }
            if (labe != null) {
                args.putInt(etiqueta, labe)
            }
            fragment.arguments = args
            return fragment
        }

        private const val selfL = "self"
        fun selfLS(self: Int): indexIngresosList {
            val fragment = indexIngresosList()
            val args = Bundle()
            Log.i("etiquet", self.toString())
            args.putInt(selfL, self)
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
        binding = FragmentIndexingresolistBinding.inflate(inflater, container, false)
        val decoder = Decoder(requireContext())
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_index2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_index)
            }

            Log.i("MODO", isDarkMode.toString())

            ingresos = montosget()
            fastable = fastget()
            binding.displayIngresos.adapter = MontoAdapter(ingresos)
            binding.totalI.text = "$" + decoder.format(totalIngresos()).toString()
            binding.fastAdd.adapter = MontoAdapter2(fastable)
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
                binding.displayIngresos.adapter = MontoAdapter(ingresos)
            }
        }
        binding.HValor.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetValuados()
                binding.displayIngresos.adapter = MontoAdapter(ingresos)
            }
        }
        binding.HFecha.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetFechados()
                binding.displayIngresos.adapter = MontoAdapter(ingresos)
            }
        }
        binding.HEtiqueta.setOnClickListener {
            lifecycleScope.launch {
                ingresos = montosgetEtiquetados()
                binding.displayIngresos.adapter = MontoAdapter(ingresos)
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

    private suspend fun montoPapelera(
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
            var status = 2
            when (estado) {
                1 -> status = 2
                6 -> status = 7
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

    private suspend fun fastget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val label: Int? = arguments?.getInt(etiqueta)

            fastable = if (label != null) {
                montoDao.getIFast(label)
            } else {
                montoDao.getIFast()
            }
            Log.i("ALL FASTADD", fastable.toString())
        }
        return fastable
    }

    private inner class MontoAdapter(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val conceptoTextView: TextView,
            val valorTextView: TextView,
            val fechaTextView: TextView,
            val etiquetaTextView: TextView,
            val optionsM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_tabla, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.IConcepto)
            val valorTextView = itemView.findViewById<TextView>(R.id.IValor)
            val fechaTextView = itemView.findViewById<TextView>(R.id.IFecha)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.IEtiqueta)
            val optionsM = itemView.findViewById<Button>(R.id.itemOptions)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                fechaTextView,
                etiquetaTextView,
                optionsM
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            var tempstat = 5
            val decoder = Decoder(requireContext())
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = decoder.format(monto.valor).toString()
            when (monto.etiqueta) {
                10001, 10003, 10004, 10005 -> {
                    holder.fechaTextView.text = monto.fecha?.let { decoder.date(it) }
                }
                10002, 10006, 10007 -> {
                    val texto = "No aplica"
                    holder.fechaTextView.text = texto
                }
            }

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
                inflater.inflate(R.menu.options_item_tabla, popupMenu.menu)
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
                            val ver = verMonto.sendMonto(monto.idmonto)

                            parentFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                .replace(R.id.index_container, ver).addToBackStack(null).commit()

                            true
                        }
                        R.id.action_editMonto -> {
                            val upup = indexmontoupdate.sendMonto(
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
                                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                .replace(R.id.index_container, upup).addToBackStack(null).commit()

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
                                                monto.estado,
                                                monto.adddate
                                            )
                                        }
                                        dialog.dismiss()
                                        parentFragmentManager.beginTransaction()
                                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                            .replace(R.id.index_container, indexmain())
                                            .addToBackStack(null)
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
            if (position == ingresos.size - 1){
                holder.itemView.setBackgroundResource(R.drawable.p1bottomcell)
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }
    }

    private inner class MontoAdapter2(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter2.MontoViewHolder2>() {
        inner class MontoViewHolder2(
            itemView: View,
            val vecesTextView: TextView,
            val conceptoTextView: TextView,
            val valorTextView: TextView
        ) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder2 {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_fasti, parent, false)
            val vecesTextView = itemView.findViewById<TextView>(R.id.fastVeces)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.fastNombre)
            val valorTextView = itemView.findViewById<TextView>(R.id.fastValor)
            return MontoViewHolder2(itemView, vecesTextView, conceptoTextView, valorTextView)
        }


        override fun onBindViewHolder(holder: MontoViewHolder2, position: Int) {
            val monto = montos[position]
            val decoder = Decoder(requireContext())
            holder.itemView.setOnClickListener {
                lifecycleScope.launch {
                    fup(monto.idmonto,
                        monto.concepto,
                        monto.valor,
                        monto.fecha,
                        monto.frecuencia,
                        monto.etiqueta,
                        monto.interes,
                        monto.veces,
                        monto.adddate
                    )
                    val label: Int? = arguments?.getInt(selfL)
                    var iilinstance = indexIngresosList()
                    if (label != null) {
                        iilinstance = selfLS(label)
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.index_container, iilinstance).addToBackStack(null).commit()
                }
            }
            holder.vecesTextView.text = monto.veces.toString()
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = decoder.format(monto.valor).toString()
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
            adddate: Int
        ) {
            withContext(Dispatchers.IO) {
                val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                val ingresoGastoDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

                var nv: Long? = 1
                if (veces != null)
                    nv = veces + 1

                val sequence = montoDao.getSequence(id.toInt())
                val values = sequence.trim('.').split('.').map { it.toInt() }.toMutableList()
                if (values.isNotEmpty()) {
                    val lastIndex = values.size - 1
                    values[lastIndex] += 1
                }
                val updatedString = values.joinToString(".")
                val result = "$updatedString."

                val enddate = montoDao.getEnded(id.toInt())
                val valorfinal = montoDao.getValorFinal(id.toInt())
                val tipointeres = montoDao.getTipoInteres(id.toInt())
                val delay = montoDao.getDelay(id.toInt())
                val cooldown = montoDao.getCooldown(id.toInt())
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
                    adddate = adddate,
                    enddate = enddate,
                    cooldown = cooldown,
                    delay = delay,
                    sequence = result
                )

                val totalIngresos = ingresoGastoDao.checkSummaryI()

                ingresoGastoDao.updateSummaryI(
                    montoPresionado.iduser.toInt(),
                    totalIngresos + montoPresionado.valor
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