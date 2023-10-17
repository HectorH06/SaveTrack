package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.example.st5.databinding.FragmentFinanzaseventoslistBinding
import com.example.st5.models.Eventos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class finanzasEventosList : Fragment() {
    private lateinit var binding: FragmentFinanzaseventoslistBinding

    private lateinit var eventos: List<Eventos>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = finanzasmain()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.finanzas_container, prev)
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
        binding = FragmentFinanzaseventoslistBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
            }

            Log.i("MODO", isDarkMode.toString())

            eventos = eventosget()
            binding.displayEventos.adapter = EventosAdapter(eventos)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = finanzasmain()

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

        binding.AgregarEventoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, finanzasEventosAdd()).addToBackStack(null).commit()
        }

        binding.HEvento.setOnClickListener {
            lifecycleScope.launch {
                eventos = eventosgetOrderedBy(0)
                binding.displayEventos.adapter = EventosAdapter(eventos)
            }
        }
        binding.HFecha.setOnClickListener {
            lifecycleScope.launch {
                eventos = eventosgetOrderedBy(1)
                binding.displayEventos.adapter = EventosAdapter(eventos)
            }
        }
        binding.HEtiqueta.setOnClickListener {
            lifecycleScope.launch {
                eventos = eventosgetOrderedBy(2)
                binding.displayEventos.adapter = EventosAdapter(eventos)
            }
        }
    }

    private suspend fun eventosget(): List<Eventos> {
        withContext(Dispatchers.IO) {
            val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()
            eventos = eventosDao.getAllEventos()
        }
        return eventos
    }

    private suspend fun eventosgetOrderedBy(key: Int): List<Eventos> {
        withContext(Dispatchers.IO) {
            val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()

        }
        return eventos
    }

    private suspend fun eventoDelete(idevento: Long) {
        withContext(Dispatchers.IO) {
            val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()

            val id = idevento.toInt()
            val nombre = eventosDao.getNombre(id)
            val fecha = eventosDao.getFecha(id)
            val frecuencia = eventosDao.getFrecuencia(id)
            val etiqueta = eventosDao.getEtiqueta(id)
            val adddate = eventosDao.getAddDate(id)

            val muertoEvento = Eventos(
                idevento = idevento,
                nombre = nombre,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                estado = 4,
                adddate = adddate,
            )

            eventosDao.updateEvento(muertoEvento)

            parentFragmentManager.beginTransaction()
                .replace(R.id.finanzas_container, historialPapelera()).addToBackStack(null)
                .commit()
        }
    }

    private inner class EventosAdapter(private val eventos: List<Eventos>) :
        RecyclerView.Adapter<EventosAdapter.EventosViewHolder>() {
        inner class EventosViewHolder(
            itemView: View,
            val nombreTextView: TextView,
            val fechaTextView: TextView,
            val etiquetaTextView: TextView,
            val updateM: Button,
            val deleteM: Button
        ) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventosViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_evento, parent, false)
            val nombreTextView = itemView.findViewById<TextView>(R.id.ENombre)
            val fechaTextView = itemView.findViewById<TextView>(R.id.EFecha)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.EEtiqueta)
            val updateM = itemView.findViewById<Button>(R.id.editEvento)
            val deleteM = itemView.findViewById<Button>(R.id.deleteEvento)
            return EventosViewHolder(
                itemView,
                nombreTextView,
                fechaTextView,
                etiquetaTextView,
                updateM,
                deleteM
            )
        }


        override fun onBindViewHolder(holder: EventosViewHolder, position: Int) {
            val evento = eventos[position]
            val decoder = Decoder(requireContext())
            holder.nombreTextView.text = evento.nombre
            holder.fechaTextView.text = evento.fecha?.let { decoder.date(it) }
            lifecycleScope.launch {
                holder.etiquetaTextView.text = decoder.label(evento.etiqueta)
            }
            val upup = finanzasEventosUpdate.sendEvento(evento.idevento)
            holder.updateM.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.finanzas_container, upup).addToBackStack(null).commit()
            }
            holder.deleteM.setOnClickListener {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres eliminar el evento ${evento.nombre}?")
                    .setPositiveButton("Eliminar") { dialog, _ ->
                        lifecycleScope.launch {
                            eventoDelete(evento.idevento)
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                            .replace(R.id.finanzas_container, finanzasmain()).addToBackStack(null)
                            .commit()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss()
                    }
                    .create()

                confirmDialog.show()
            }
            if (position == eventos.size - 1) {
                holder.itemView.setBackgroundResource(R.drawable.p1bottomcell)
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de eventossss", eventos.size.toString())
            return eventos.size
        }
    }
}