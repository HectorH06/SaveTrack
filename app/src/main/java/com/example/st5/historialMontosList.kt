package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
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
import com.example.st5.databinding.FragmentHistorialmontoslistBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class historialMontosList : Fragment() {
    private lateinit var binding: FragmentHistorialmontoslistBinding

    private lateinit var montosf: List<Monto>
    private var label = 0

    companion object {
        private const val fecha = "fechar"
        private const val etiqueta = "etiquetar"
        fun fechaSearch(fech: Int): historialMontosList {
            val fragment = historialMontosList()
            val args = Bundle()
            Log.i("fech", fech.toString())

            args.putInt(fecha, fech)
            fragment.arguments = args
            return fragment
        }
        fun fechaSearch(fech: Int, etiquet: Int): historialMontosList {
            val fragment = historialMontosList()
            val args = Bundle()
            Log.i("fech", fech.toString())
            Log.i("etiquet", etiquet.toString())

            args.putInt(fecha, fech)
            args.putInt(etiqueta, etiquet)
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
                    lifecycleScope.launch {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                            .replace(R.id.index_container, prev)
                            .addToBackStack(null).commit()
                    }
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
        binding = FragmentHistorialmontoslistBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_historial2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_historial)
            }

            Log.i("MODO", isDarkMode.toString())

            val etiq = if (arguments?.getInt(etiqueta) != null){
                arguments?.getInt(etiqueta)
            } else {
                0
            }
            if (etiq != null) {label = etiq}
            Log.v("etiqueta a buscar", label.toString())

            montosf = montosget()
            binding.displayMontos.adapter = MontoAdapter(montosf)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val datedate: String? = arguments?.getString(fecha)
        Log.i("datedate", datedate.toString())

        val back = historialmain()

        binding.goback.setOnClickListener {
            lifecycleScope.launch {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.historial_container, back).addToBackStack(null).commit()
            }
        }

        binding.HConcepto.setOnClickListener {
            lifecycleScope.launch {
                montosf = montosgetAlfabetica()
                binding.displayMontos.adapter = MontoAdapter(montosf)
            }
        }
        binding.HValor.setOnClickListener {
            lifecycleScope.launch {
                montosf = montosgetValuados()
                binding.displayMontos.adapter = MontoAdapter(montosf)
            }
        }
        binding.HVeces.setOnClickListener {
            lifecycleScope.launch {
                montosf = montosgetVeces()
                binding.displayMontos.adapter = MontoAdapter(montosf)
            }
        }
        binding.HEtiqueta.setOnClickListener {
            lifecycleScope.launch {
                montosf = montosgetEtiquetados()
                binding.displayMontos.adapter = MontoAdapter(montosf)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun montosget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val formatoFecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val datedate: Int? = arguments?.getInt(fecha)

            val fs = "$datedate"

            val truefecha = formatoFecha.parse(fs)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dom = calendar.get(Calendar.DAY_OF_MONTH)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = 100
            when (w) {
                1 -> dow = 47
                2 -> dow = 41
                3 -> dow = 42
                4 -> dow = 43
                5 -> dow = 44
                6 -> dow = 45
                7 -> dow = 46
            }

            val yyy = calendar.get(Calendar.YEAR)
            val mesesito = when (calendar.get(Calendar.MONTH)) {
                0 -> "Enero"
                1 -> "Febrero"
                2 -> "Marzo"
                3 -> "Abril"
                4 -> "Mayo"
                5 -> "Junio"
                6 -> "Julio"
                7 -> "Agosto"
                8 -> "Septiembre"
                9 -> "Octubre"
                10 -> "Noviembre"
                11 -> "Diciembre"
                else -> "cualquier mes"
            }
            val semanita = when (dow) {
                47 -> "Domingo"
                41 -> "Lunes"
                42 -> "Martes"
                43 -> "Miércoles"
                44 -> "Jueves"
                45 -> "Viernes"
                46 -> "Sábado"
                else -> ""
            }

            Log.i("datedate", fs)
            Log.i("DOM", dom.toString())
            Log.i("DOW", dow.toString())

            binding.bar.text = "Montos del $semanita $dom de $mesesito del $yyy"

            Log.v("labelll", "$label")
            montosf = if (datedate != null) {
                if (label != 0) {
                    Log.v("con etiquetona", "")
                    montoDao.getMontoXFecha(datedate, dom, dow, 100, datedate, label)
                } else {
                    Log.v("sin etiquetona", "")
                    montoDao.getMontoXFecha(datedate, dom, dow, 100, datedate)
                }
            } else {
                montoDao.getMontoXFecha()
            }
            Log.i("ALL MONTOS", montosf.toString())
        }
        return montosf
    }

    private suspend fun montosgetAlfabetica(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val formatoFecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val datedate: Int? = arguments?.getInt(fecha)

            val truefecha = formatoFecha.parse(datedate.toString())
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dom = calendar.get(Calendar.DAY_OF_MONTH)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = 100
            when (w) {
                1 -> dow = 47
                2 -> dow = 41
                3 -> dow = 42
                4 -> dow = 43
                5 -> dow = 44
                6 -> dow = 45
                7 -> dow = 46
            }

            Log.i("datedate", datedate.toString())
            Log.i("DOM", dom.toString())
            Log.i("DOW", dow.toString())

            montosf = if (datedate != null) {
                if (label != 0) {
                    montoDao.getMontoXFechaAlfabetica(datedate, dom, dow, 100, datedate, label)
                } else {
                    montoDao.getMontoXFechaAlfabetica(datedate, dom, dow, 100, datedate)
                }
            } else {
                montoDao.getMontoXFechaAlfabetica()
            }
            Log.i("ALL MONTOS", montosf.toString())
        }
        return montosf
    }

    private suspend fun montosgetValuados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val formatoFecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val datedate: Int? = arguments?.getInt(fecha)

            val truefecha = formatoFecha.parse(datedate.toString())
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dom = calendar.get(Calendar.DAY_OF_MONTH)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = 100
            when (w) {
                1 -> dow = 47
                2 -> dow = 41
                3 -> dow = 42
                4 -> dow = 43
                5 -> dow = 44
                6 -> dow = 45
                7 -> dow = 46
            }

            Log.i("datedate", datedate.toString())
            Log.i("DOM", dom.toString())
            Log.i("DOW", dow.toString())

            montosf = if (datedate != null) {
                if (label != 0) {
                    montoDao.getMontoXFechaValuados(datedate, dom, dow, 100, datedate, label)
                } else {
                    montoDao.getMontoXFechaValuados(datedate, dom, dow, 100, datedate)
                }
            } else {
                montoDao.getMontoXFechaValuados()
            }
            Log.i("ALL MONTOS", montosf.toString())
        }
        return montosf
    }

    private suspend fun montosgetVeces(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val formatoFecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val datedate: Int? = arguments?.getInt(fecha)

            val truefecha = formatoFecha.parse(datedate.toString())
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dom = calendar.get(Calendar.DAY_OF_MONTH)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = 100
            when (w) {
                1 -> dow = 47
                2 -> dow = 41
                3 -> dow = 42
                4 -> dow = 43
                5 -> dow = 44
                6 -> dow = 45
                7 -> dow = 46
            }

            Log.i("datedate", datedate.toString())
            Log.i("DOM", dom.toString())
            Log.i("DOW", dow.toString())

            montosf = if (datedate != null) {
                if (label != 0) {
                    montoDao.getMontoXFechaVeces(datedate, dom, dow, 100, datedate, label)
                } else {
                    montoDao.getMontoXFechaVeces(datedate, dom, dow, 100, datedate)
                }
            } else {
                montoDao.getMontoXFechaVeces()
            }
            Log.i("ALL MONTOS", montosf.toString())
        }
        return montosf
    }

    private suspend fun montosgetEtiquetados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val formatoFecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val datedate: Int? = arguments?.getInt(fecha)

            val truefecha = formatoFecha.parse(datedate.toString())
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dom = calendar.get(Calendar.DAY_OF_MONTH)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = 100
            when (w) {
                1 -> dow = 47
                2 -> dow = 41
                3 -> dow = 42
                4 -> dow = 43
                5 -> dow = 44
                6 -> dow = 45
                7 -> dow = 46
            }

            Log.i("datedate", datedate.toString())
            Log.i("DOM", dom.toString())
            Log.i("DOW", dow.toString())

            montosf = if (datedate != null) {
                if (label != 0) {
                    montoDao.getMontoXFechaEtiquetados(datedate, dom, dow, 100, datedate, label)
                } else {
                    montoDao.getMontoXFechaEtiquetados(datedate, dom, dow, 100, datedate)
                }
            } else {
                montoDao.getMontoXFechaEtiquetados()
            }
            Log.i("ALL MONTOS", montosf.toString())
        }
        return montosf
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

    private inner class MontoAdapter(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val conceptoTextView: TextView,
            val valorTextView: TextView,
            val vecesTextView: TextView,
            val etiquetaTextView: TextView,
            val optionsM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.MConcepto)
            val valorTextView = itemView.findViewById<TextView>(R.id.MValor)
            val vecesTextView = itemView.findViewById<TextView>(R.id.MVeces)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.MEtiqueta)
            val optionsM = itemView.findViewById<Button>(R.id.itemOptions)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                vecesTextView,
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
            holder.vecesTextView.text = monto.veces.toString()
            lifecycleScope.launch {
                holder.etiquetaTextView.text = decoder.label(monto.etiqueta)
            }

            if (monto.estado == 0 || monto.estado == 1 || monto.estado == 5 || monto.estado == 6){
                tempstat = 5
            }
            if (monto.estado == 3 || monto.estado == 4 || monto.estado == 8 || monto.estado == 9){
                tempstat = 8
            }

            holder.optionsM.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), holder.optionsM, Gravity.END)
                val inflater = popupMenu.menuInflater
                inflater.inflate(R.menu.options_item_tabla, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.action_favMonto -> {
                            if (tempstat == 5){
                                tempstat = 8
                            }
                            if (tempstat == 8){
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
                                .replace(R.id.historial_container, ver).addToBackStack(null).commit()

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
                                .replace(R.id.historial_container, upup).addToBackStack(null).commit()

                            true
                        }
                        R.id.action_deleteMonto -> {
                            if (monto.estado == 3 || monto.estado == 4 || monto.estado == 8 || monto.estado ==  9 || tempstat == 8) {
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
                                            .replace(R.id.historial_container, historialmain()).addToBackStack(null)
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