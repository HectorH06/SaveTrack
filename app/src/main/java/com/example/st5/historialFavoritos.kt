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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentHistorialfavoritosBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class historialFavoritos : Fragment() {
    private lateinit var binding: FragmentHistorialfavoritosBinding

    private lateinit var montosp: List<Monto>

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
        binding = FragmentHistorialfavoritosBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            montosp = montosget()
            binding.displayMontos.adapter = MontoAdapter(montosp)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = historialmain()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.historial_container, back).addToBackStack(null).commit()
        }
        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.index_container, Configuracion()).addToBackStack(null).commit()
        }

        binding.HConcepto.setOnClickListener {
            lifecycleScope.launch {
                montosp = montosgetAlfabetica()
                binding.displayMontos.adapter = MontoAdapter(montosp)
            }
        }
        binding.HValor.setOnClickListener {
            lifecycleScope.launch {
                montosp = montosgetValuados()
                binding.displayMontos.adapter = MontoAdapter(montosp)
            }
        }
        binding.HFecha.setOnClickListener {
            lifecycleScope.launch {
                montosp = montosgetVeces()
                binding.displayMontos.adapter = MontoAdapter(montosp)
            }
        }
        binding.HEtiqueta.setOnClickListener {
            lifecycleScope.launch {
                montosp = montosgetEtiquetados()
                binding.displayMontos.adapter = MontoAdapter(montosp)
            }
        }
    }

    private suspend fun montosget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            montosp = montoDao.getFavoritos()
            Log.i("ALL MONTOS", montosp.toString())
        }
        return montosp
    }

    private suspend fun montosgetAlfabetica(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            montosp = montoDao.getFavoritos()
            Log.i("ALL MONTOS", montosp.toString())
        }
        return montosp
    }

    private suspend fun montosgetValuados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            montosp = montoDao.getFavoritos()
            Log.i("ALL MONTOS", montosp.toString())
        }
        return montosp
    }

    private suspend fun montosgetVeces(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            montosp = montoDao.getFavoritos()
            Log.i("ALL MONTOS", montosp.toString())
        }
        return montosp
    }

    private suspend fun montosgetEtiquetados(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            montosp = montoDao.getFavoritos()
            Log.i("ALL MONTOS", montosp.toString())
        }
        return montosp
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
        adddate: Int,
        enddate: Int?,
        cooldown: Int
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
            val fechaTextView: TextView,
            val etiquetaTextView: TextView,
            val favM: Button,
            val updateM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_fav, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.FConcepto)
            val valorTextView = itemView.findViewById<TextView>(R.id.FValor)
            val fechaTextView = itemView.findViewById<TextView>(R.id.FFecha)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.FEtiqueta)
            val favM = itemView.findViewById<Button>(R.id.favMonto)
            val updateM = itemView.findViewById<Button>(R.id.editMonto)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                fechaTextView,
                etiquetaTextView,
                favM,
                updateM
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            var tempstat = 5
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = monto.valor.toString()
            holder.fechaTextView.text = monto.adddate.toString()
            holder.etiquetaTextView.text = monto.etiqueta.toString()
            if (monto.estado == 0 || monto.estado == 1 || monto.estado == 5 || monto.estado == 6){
                holder.favM.setBackgroundResource(R.drawable.ic_notstar)
                tempstat = 5
            }
            if (monto.estado == 3 || monto.estado == 4 || monto.estado == 8 || monto.estado == 9){
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
                        monto.adddate,
                        monto.enddate,
                        monto.cooldown
                    )
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.historial_container, historialFavoritos()).addToBackStack(null)
                    .commit()
            }
            val upup = indexmontoupdate.sendMonto(monto.idmonto, monto.concepto, monto.valor, monto.fecha, monto.frecuencia, monto.etiqueta, monto.interes, monto.veces, monto.adddate)
            holder.updateM.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.historial_container, upup).addToBackStack(null).commit()
            }
            if (position == montosp.size - 1){
                holder.itemView.setBackgroundResource(R.drawable.y1bottomcell)
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }
    }
}