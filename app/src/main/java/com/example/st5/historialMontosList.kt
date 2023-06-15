package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
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
import com.example.st5.databinding.FragmentHistorialmontoslistBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class historialMontosList : Fragment() {
    private lateinit var binding: FragmentHistorialmontoslistBinding

    private lateinit var montosf: List<Monto>

    companion object {
        private const val fecha = "fechar"
        fun fechaSearch(fech: String): historialMontosList {
            val fragment = historialMontosList()
            val args = Bundle()
            Log.i("fech", fech)
            
            if (fech != null) {
                args.putString(fecha, fech)
            }
            fragment.arguments = args
            return fragment
        }
    }

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
        binding = FragmentHistorialmontoslistBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
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
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.historial_container, back).addToBackStack(null).commit()
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

    private suspend fun montosget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val datedate: String? = arguments?.getString(fecha)

            val truefecha = formatoFecha.parse(datedate)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            var dom = calendar.get(Calendar.DAY_OF_MONTH).toString()
            var w = calendar.get(Calendar.DAY_OF_WEEK)
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

            Log.i("datedate", datedate.toString())
            Log.i("DOM", dom)
            Log.i("DOW", dow)

            montosf = if (datedate != null) {
                montoDao.getMontoXFecha(datedate, dom, dow, "Diario")
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

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val datedate: String? = arguments?.getString(fecha)

            val truefecha = formatoFecha.parse(datedate)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            var dom = calendar.get(Calendar.DAY_OF_MONTH).toString()
            var w = calendar.get(Calendar.DAY_OF_WEEK)
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

            Log.i("datedate", datedate.toString())
            Log.i("DOM", dom)
            Log.i("DOW", dow)

            montosf = if (datedate != null) {
                montoDao.getMontoXFechaAlfabetica(datedate, dom, dow, "Diario")
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

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val datedate: String? = arguments?.getString(fecha)

            val truefecha = formatoFecha.parse(datedate)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            var dom = calendar.get(Calendar.DAY_OF_MONTH).toString()
            var w = calendar.get(Calendar.DAY_OF_WEEK)
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

            Log.i("datedate", datedate.toString())
            Log.i("DOM", dom)
            Log.i("DOW", dow)

            montosf = if (datedate != null) {
                montoDao.getMontoXFechaValuados(datedate, dom, dow, "Diario")
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

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val datedate: String? = arguments?.getString(fecha)

            val truefecha = formatoFecha.parse(datedate)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            var dom = calendar.get(Calendar.DAY_OF_MONTH).toString()
            var w = calendar.get(Calendar.DAY_OF_WEEK)
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

            Log.i("datedate", datedate.toString())
            Log.i("DOM", dom)
            Log.i("DOW", dow)

            montosf = if (datedate != null) {
                montoDao.getMontoXFechaVeces(datedate, dom, dow, "Diario")
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

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val datedate: String? = arguments?.getString(fecha)

            val truefecha = formatoFecha.parse(datedate)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            var dom = calendar.get(Calendar.DAY_OF_MONTH).toString()
            var w = calendar.get(Calendar.DAY_OF_WEEK)
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

            Log.i("datedate", datedate.toString())
            Log.i("DOM", dom)
            Log.i("DOW", dow)

            montosf = if (datedate != null) {
                montoDao.getMontoXFechaEtiquetados(datedate, dom, dow, "Diario")
            } else {
                montoDao.getMontoXFechaEtiquetados()
            }
            Log.i("ALL MONTOS", montosf.toString())
        }
        return montosf
    }

    private suspend fun montodelete(
        idmonto: Long,
        concepto: String,
        valor: Double,
        fecha: String,
        frecuencia: Long?,
        etiqueta: Long,
        interes: Double?,
        veces: Long?
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
                veces = veces
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
            val updateM: Button,
            val deleteM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.MConcepto)
            val valorTextView = itemView.findViewById<TextView>(R.id.MValor)
            val vecesTextView = itemView.findViewById<TextView>(R.id.MVeces)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.MEtiqueta)
            val updateM = itemView.findViewById<Button>(R.id.editMonto)
            val deleteM = itemView.findViewById<Button>(R.id.deleteMonto)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                vecesTextView,
                etiquetaTextView,
                updateM,
                deleteM
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = monto.valor.toString()
            holder.vecesTextView.text = monto.veces.toString()
            holder.etiquetaTextView.text = monto.etiqueta.toString()
            val upup = indexmontoupdate.sendMonto(monto.idmonto, monto.concepto, monto.valor, monto.fecha, monto.frecuencia, monto.etiqueta, monto.interes, monto.veces)
            holder.updateM.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.historial_container, upup).addToBackStack(null).commit()
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
                            montodelete(monto.idmonto, monto.concepto, monto.valor, monto.fecha, monto.frecuencia, monto.etiqueta, monto.interes, monto.veces)
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