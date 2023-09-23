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

    private suspend fun allOut() {
        withContext(Dispatchers.IO) {
            val assetsDao = Stlite.getInstance(requireContext()).getAssetsDao()
            val conySugDao = Stlite.getInstance(requireContext()).getConySugDao()
            val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val montoGrupoDao = Stlite.getInstance(requireContext()).getMontoGrupoDao()
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

            val cs = hashMapOf<String, Array<ConySug>>(
                "A" to arrayOf(ConySug(), ConySug()),
                "C" to arrayOf(ConySug(), ConySug()),
                "E" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "G" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "I" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug()),
                "L" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "M" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "D" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug()),
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

            if (consejosAllActive == consejosAll) {cs["C"]?.set(0, ConySug(idcon = 2, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (consejosAllRejected.size >= consejosAll.size/2) {cs["C"]?.set(1, ConySug(idcon = 3, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            // endregion

            // region EVENTOS
            val eventosAll = eventosDao.getAllEventos()
            val eventosAllUnabled = eventosDao.getAllUnabledEventos()

            if (eventosAll.size <= 3) {cs["E"]?.set(0, ConySug(idcon = 4, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (eventosAll.size >= 30) {cs["E"]?.set(1, ConySug(idcon = 5, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (eventosAllUnabled.size >= eventosAll.size/2) {cs["E"]?.set(2, ConySug(idcon = 6, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (!assetsNotifEnabled) {cs["E"]?.set(3, ConySug(idcon = 7, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}
            if (!assetsNotifEnabled) {cs["E"]?.set(4, ConySug(idcon = 8, nombre = "", contenido = "", estado = 0, flag = 0, type = 0, style = 0))}

            // endregion

            // region GRUPOS

            // endregion

            // region INGRESOSGASTOS

            // endregion

            // region ETIQUETAS

            // endregion

            // region MONTOS

            // endregion

            // region DEUDAS

            // endregion

            // region USUARIO

            // endregion

            //recuerda añadir la comprobación para asegurarse de si existen o no los eventos
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