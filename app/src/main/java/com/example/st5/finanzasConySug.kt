package com.example.st5

import android.content.Context
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
import com.example.st5.databinding.FragmentFinanzasconsejosysugBinding
import com.example.st5.models.ConySug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class finanzasConySug : Fragment(){
    private lateinit var binding: FragmentFinanzasconsejosysugBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            var isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
            }
            Log.i("MODO", isDarkMode.toString())
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.finanzas_container, finanzasmain()).addToBackStack(null).commit()
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val back = finanzasmain();

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.finanzas_container, back).addToBackStack(null).commit()
        }

        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, Configuracion()).addToBackStack(null).commit()
        }
    }

    private fun similarityCalculator(search: String, result: String): Double {
        val len1 = search.length
        val len2 = result.length
        val matrix = Array(len1 + 1) { IntArray(len2 + 1) }

        for (i in 0..len1) {
            matrix[i][0] = i
        }

        for (j in 0..len2) {
            matrix[0][j] = j
        }

        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (search[i - 1] == result[j - 1]) 0 else 1
                matrix[i][j] = minOf(
                    matrix[i - 1][j] + 1,
                    matrix[i][j - 1] + 1,
                    matrix[i - 1][j - 1] + cost
                )
            }
        }

        val maxLen = maxOf(len1, len2)
        val similarity = 1.0 - matrix[len1][len2].toDouble() / maxLen.toDouble()
        return similarity * 100
    }
    private suspend fun allOut() {
        withContext(Dispatchers.IO) {
            // region GET
            val localDate = LocalDate.now()
            val day = localDate.dayOfMonth
            val fDay = String.format("%02d", day)
            val month = localDate.monthValue - 1
            val fMonth = String.format("%02d", month)
            val year = localDate.year
            val datedate = "$year$fMonth$fDay"
            val today: Int = datedate.replace("-", "").toInt()

            val assetsDao = Stlite.getInstance(requireContext()).getAssetsDao()
            val conySugDao = Stlite.getInstance(requireContext()).getConySugDao()
            val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val montoGrupoDao = Stlite.getInstance(requireContext()).getMontoGrupoDao()
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            // endregion

            val cs = hashMapOf(
                "A" to arrayOf(ConySug(), ConySug()),
                "C" to arrayOf(ConySug(), ConySug()),
                "E" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "G" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "I" to arrayOf(ConySug(), ConySug()),
                "L" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "M" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "D" to arrayOf(ConySug(), ConySug(), ConySug()),
                "U" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug())
            )

            // region ASSETS
            val assetsDarkModeEnabled = assetsDao.getTheme() != 0
            val assetsNotifEnabled = assetsDao.getNotif() != 0
            if (assetsDarkModeEnabled) {cs["A"]?.set(0,ConySug(idcon = 0, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (assetsNotifEnabled) {cs["A"]?.set(1, ConySug(idcon = 1, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}

            // endregion

            // region CONYSUG
            val consejosAll = conySugDao.getAllCon()
            val consejosAllActive = conySugDao.getAllActiveCon()
            val consejosAllRejected = conySugDao.getAllRejectedCon()

            if (consejosAllActive == consejosAll) {cs["C"]?.set(0, ConySug(idcon = 100, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (consejosAllRejected.size >= consejosAll.size/2) {cs["C"]?.set(1, ConySug(idcon = 101, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            // endregion

            // region EVENTOS
            val eventosAll = eventosDao.getAllEventos()
            val eventosAllUnabled = eventosDao.getAllUnabledEventos()
            val eventosMaxAddDate =  eventosDao.getMaxAddDate()

            if (eventosAll.size <= 3) {cs["E"]?.set(0, ConySug(idcon = 200, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (eventosAll.size >= 30) {cs["E"]?.set(1, ConySug(idcon = 201, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (eventosAllUnabled.size >= eventosAll.size/2) {cs["E"]?.set(2, ConySug(idcon = 202, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (!assetsNotifEnabled) {cs["E"]?.set(3, ConySug(idcon = 203, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (!assetsNotifEnabled) {cs["E"]?.set(4, ConySug(idcon = 204, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (today - eventosMaxAddDate >= 1200) {cs["E"]?.set(5, ConySug(idcon = 205, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (eventosAll.size <= 2) {cs["E"]?.set(6, ConySug(idcon = 206, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            for (i in 0 until eventosAll.size - 1) {
                if (similarityCalculator(eventosAll[i].nombre, eventosAll[i + 1].nombre) >= 85) {cs["E"]?.set(7, ConySug(idcon = 207 + i.toLong(), nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            }
            // endregion

            // region GRUPOS

            // endregion

            // region INGRESOSGASTOS
            val ingresosTotales = ingresosGastosDao.checkSummaryI()
            val egresosTotales = ingresosGastosDao.checkSummaryG()

            if (ingresosTotales >= egresosTotales + (egresosTotales * .05)) {cs["I"]?.set(0, ConySug(idcon = 400, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (ingresosTotales < egresosTotales) {cs["I"]?.set(1, ConySug(idcon = 401, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            // endregion

            // region ETIQUETAS
            val labelsAll = labelsDao.getAllLabels()

            if (labelsAll.isEmpty()) {cs["L"]?.set(0, ConySug(idcon = 500, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (labelsAll.size <= 3) {cs["L"]?.set(1, ConySug(idcon = 501, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (labelsAll.size >= 25) {cs["L"]?.set(2, ConySug(idcon = 502, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            //if (CURRENT LABEL IS NOT BEING USED) {cs["L"]?.set(3, ConySug(idcon = 503, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            for (i in 0 until labelsAll.size - 1) {
                if (similarityCalculator(labelsAll[i].plabel, labelsAll[i + 1].plabel) >= 80) {cs["L"]?.set(4, ConySug(idcon = 504 + i.toLong(), nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            }
            for (i in 0 until labelsAll.size - 1) {
                if (similarityCalculator(labelsAll[i].color.toString(), labelsAll[i + 1].color.toString()) >= 80) {cs["L"]?.set(5, ConySug(idcon = 555 + i.toLong(), nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            }
            // endregion

            // region MONTOS
            val montosAll = montoDao.getAllMontos()
            val ingresosAll = montoDao.getIngresos()
            val egresosAll = montoDao.getGastos()
            val montosDelayed = montoDao.getDelayed(today)

            if (montosAll.isEmpty()) {cs["M"]?.set(0, ConySug(idcon = 600, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (ingresosAll.isEmpty()) {cs["M"]?.set(1, ConySug(idcon = 601, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (egresosAll.isEmpty()) {cs["M"]?.set(2, ConySug(idcon = 602, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (montosAll.size <= 5) {cs["M"]?.set(3, ConySug(idcon = 603, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (montosAll.size >= 500) {cs["M"]?.set(4, ConySug(idcon = 604, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            for (i in 0 until montosAll.size - 1) {
                if (similarityCalculator(montosAll[i].concepto, montosAll[i + 1].concepto) >= 80) {cs["M"]?.set(5, ConySug(idcon = 605 + i.toLong(), nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            }
            for (i in montosDelayed.indices) {
                cs["M"]?.set(6, ConySug(idcon = 656 + i.toLong(), nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))
            }
            // endregion

            // region DEUDAS
            val deudasAll = montoDao.getDeudasList()

            if (deudasAll.size >= montosAll.size * .8) {cs["D"]?.set(0, ConySug(idcon = 700, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            for (i in 0 until deudasAll.size - 1) {
                if (deudasAll[i].delay >= 3) {cs["D"]?.set(1, ConySug(idcon = 701 + i.toLong(), nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            }
            for (i in 0 until deudasAll.size - 1) {
                if ((deudasAll[i].valorfinal ?: return@withContext) >= ingresosTotales/2) {cs["D"]?.set(2, ConySug(idcon = 752 + i.toLong(), nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            }
            // endregion

            // region USUARIO
            val usuarioMeta = usuarioDao.checkMeta()

            if (usuarioMeta >= ingresosTotales - egresosTotales) {cs["U"]?.set(0, ConySug(idcon = 700, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (usuarioMeta <= (ingresosTotales - egresosTotales) * .1) {cs["U"]?.set(1, ConySug(idcon = 701, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (usuarioMeta == 0.0) {cs["U"]?.set(2, ConySug(idcon = 702, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            // endregion

            //recuerda añadir la comprobación para asegurarse de si existen o no los eventos
            //cada for debe tener un add dentro de sí para evitar sobreescritura
            //poner minOf en cada for para que no exceda su límite de id
        }
    }

    private inner class ConsejoAdapter(private val consejos: List<ConySug>) :
        RecyclerView.Adapter<ConsejoAdapter.ConsejoViewHolder>() {
        inner class ConsejoViewHolder(
            itemView: View,
            val consejo: TextView,
            val accept: Button,
            val ignore: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsejoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_consejo, parent, false)
            val consejo = itemView.findViewById<TextView>(R.id.consejo)
            val accept = itemView.findViewById<Button>(R.id.aceptar)
            val ignore = itemView.findViewById<Button>(R.id.ignorar)
            return ConsejoViewHolder(
                itemView,
                consejo,
                accept,
                ignore
            )
        }


        override fun onBindViewHolder(holder: ConsejoViewHolder, position: Int) {
            holder.consejo.text = ""
            holder.accept.text = ""
            holder.ignore.text = ""
        }


        override fun getItemCount(): Int {
            Log.v("size de Consejossss", consejos.size.toString())
            return consejos.size
            // tienes que hacer un generador de listas con un objeto que sea una lista, y tendrás que generar sus objetos a partir de datos internos con condicionales (si el usuario tiene ventas, ponerle al usuario vendedor en su perfil, solo en caso de que no lo tenga)
        }
    }
}