package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentFinanzaseventosupdateBinding
import com.example.st5.models.Eventos
import com.polyak.iconswitch.IconSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class finanzasEventosUpdate : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentFinanzaseventosupdateBinding

    private var label: Int = 0
    private var frecuencia: Int = 0
    private var fecha: Int = 0
    private var estado: Int = 0
    private var selectedDay = 39
    private var selectedLabel: String? = "Seleccionar"
    private var selectedfr: String? = "Seleccionar"

    private var nEvento = ""

    private var mutableEtiquetas: MutableList<String> = mutableListOf()
    private var mutableIds: MutableList<Long> = mutableListOf()
    private var mutableColores: MutableList<Int> = mutableListOf()

    companion object {
        private const val idv = "ide"
        fun sendEvento(
            ide: Long
        ): finanzasEventosUpdate {
            val fragment = finanzasEventosUpdate()
            val args = Bundle()
            args.putLong(idv, ide)
            Log.i("idv", idv)
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
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.finanzas_container, finanzasEventosList()).addToBackStack(null).commit()
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
        binding = FragmentFinanzaseventosupdateBinding.inflate(inflater, container, false)
        val ide = arguments?.getLong(idv)
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
            }

            Log.i("MODO", isDarkMode.toString())

            if (ide != null) {
                getEvento(ide.toInt())
            }
            binding.ConceptoField.setText(nEvento)
        }
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ide = arguments?.getLong(idv)
        lifecycleScope.launch {
            getLabels()
        }

        val adapterF = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.frecuenciaoptionseventos,
            android.R.layout.simple_spinner_item
        )
        val arrayEtiquetas = mutableEtiquetas
        val adapterL = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayEtiquetas
        )

        adapterF.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.FrecuenciaField.adapter = adapterF
        binding.LabelField.adapter = adapterL

        binding.updownSwitch.setCheckedChangeListener {
            when (binding.updownSwitch.checked) {
                IconSwitch.Checked.LEFT -> {
                    estado = 0
                    binding.LabelField.adapter = adapterL
                }
                IconSwitch.Checked.RIGHT -> {
                    estado = 1
                    binding.LabelField.adapter = adapterL
                }
                else -> {}
            }
        }

        IconSwitch.Checked.RIGHT

        binding.LabelField.onItemSelectedListener = this

        binding.FrecuenciaField.onItemSelectedListener = this

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.finanzas_container, finanzasEventosList()).addToBackStack(null).commit()
        }

        binding.Confirm.setOnClickListener {
            val concepto = binding.ConceptoField.text.toString()

            if (label != 0 && concepto != "" && selectedDay != 39) {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres guardar cambios?")
                    .setPositiveButton("Guardar") { dialog, _ ->
                        fecha = when (frecuencia) {
                            0 -> {
                                val day = binding.FechaField.dayOfMonth
                                val fDay = String.format("%02d", day)
                                val month = binding.FechaField.month + 1
                                val fMonth = String.format("%02d", month)
                                val year = binding.FechaField.year
                                val datedate = "$year$fMonth$fDay"
                                val fsi: Int = datedate.replace("-", "").toInt()

                                fsi
                            } // Único
                            30 -> {
                                val intday = binding.FechaField.dayOfMonth
                                Log.w("DAY", intday.toString())

                                intday
                            }
                            61, 91, 122, 183, 365 -> {
                                val day = binding.FechaField.dayOfMonth
                                val fDay = String.format("%02d", day)
                                val month = binding.FechaField.month + 1
                                val fMonth = String.format("%02d", month)
                                val datedate = "5$fMonth$fDay"
                                val fsi: Int = datedate.toInt()

                                fsi
                            } // Mensuales
                            else -> {
                                val intyear = binding.FechaField.year - 1900
                                Log.w("YEAR", intyear.toString())
                                val intmonth = binding.FechaField.month
                                Log.w("MONTH", intmonth.toString())
                                val intday = binding.FechaField.dayOfMonth
                                Log.w("DAY", intday.toString())
                                val datedate = "$intyear$intmonth$intday"
                                Log.w("DATE", datedate)

                                datedate.toInt()
                            } // Catch que agarra la fecha actual
                        }

                        Log.v("Concepto", concepto)
                        Log.v("Fecha", fecha.toString())
                        Log.v("Frecuencia", frecuencia.toString())
                        Log.v("Etiqueta", label.toString())
                        lifecycleScope.launch {
                            if (ide != null) {
                                eventoupdate(
                                    ide.toLong(),
                                    concepto,
                                    fecha,
                                    frecuencia,
                                    label,
                                    estado,
                                )
                            }
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                            .replace(R.id.finanzas_container, finanzasEventos()).addToBackStack(null).commit()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                confirmDialog.show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "No pueden haber campos vacíos",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.Cancel.setOnClickListener {
            val cancelDialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Seguro que quieres descartar cambios?")
                .setPositiveButton("Descartar") { dialog, _ ->
                    dialog.dismiss()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.finanzas_container, finanzasEventos()).addToBackStack(null).commit()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            cancelDialog.show()
        }

    }

    private suspend fun getEvento(ide: Int) {
        withContext(Dispatchers.IO) {
            val eventoDao = Stlite.getInstance(requireContext()).getEventosDao()
            nEvento = eventoDao.getNombre(ide)
        }
    }

    private suspend fun getLabels() {
        withContext(Dispatchers.IO) {
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val max = labelsDao.getMaxLabel()

            mutableIds.add(0)
            mutableEtiquetas.add("Seleccionar")
            mutableColores.add(222222)
            for (i in 1..max) {

                if (labelsDao.getPlabel(i) != null){
                    mutableIds.add(labelsDao.getIdLabel(i))
                    mutableEtiquetas.add(labelsDao.getPlabel(i))
                    mutableColores.add(labelsDao.getColor(i))

                    Log.v("leibels", "${labelsDao.getIdLabel(i)}, ${labelsDao.getPlabel(i)}, $max")
                }
            }
            Log.v("idl", "$mutableIds")
            Log.v("plabel", "$mutableEtiquetas")
            Log.v("color", "$mutableColores")
        }
    }

    private suspend fun eventoupdate(
        ide: Long,
        nombre: String,
        fecha: Int,
        frecuencia: Int,
        etiqueta: Int,
        estado: Int
    ) {
        withContext(Dispatchers.IO) {
            val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()

            val adddate = eventosDao.getAddDate(ide.toInt())
            val updateEvento = Eventos(
                idevento = ide,
                nombre = nombre,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                estado = estado,
                adddate = adddate
            )

            eventosDao.updateEvento(updateEvento)
            val eventos = eventosDao.getAllEventos()
            Log.i("ALL LABELS", "$eventos")
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selectedLabel = binding.LabelField.selectedItem?.toString()
        selectedfr = binding.FrecuenciaField.selectedItem?.toString()

        binding.LabelText.text = selectedLabel
        // region LABELS
        if (selectedLabel != null) {
            Log.v("ETIQUETA", selectedLabel.toString())
        }
        when (selectedLabel) {
            "Seleccionar" -> {
                label = 0
            }
            else -> {
                for (i in 0 until mutableEtiquetas.size) {
                    if (selectedLabel == mutableEtiquetas[i]) {
                        label = mutableIds[i].toInt()
                    }
                }
            }
        }
        // endregion

        // region FRECUENCIAS
        if (selectedfr != null) {
            Log.v("ETIQUETA", selectedfr.toString())
        }
        when (selectedfr) {
            "Única vez" -> {
                frecuencia = 0
                selectedDay = 40
            }
            "Mensual" -> {
                frecuencia = 30
                selectedDay = 40
            }
            "Bimestral" -> {
                frecuencia = 61
                selectedDay = 40
            }
            "Trimestral" -> {
                frecuencia = 91
                selectedDay = 40
            }
            "Cuatrimestral" -> {
                frecuencia = 122
                selectedDay = 40
            }
            "Semestral" -> {
                frecuencia = 183
                selectedDay = 40
            }
            "Anual" -> {
                frecuencia = 365
                selectedDay = 40
            }

            else -> {
                frecuencia = 0
                selectedDay = 40
                Log.d("NONE OR NOT", selectedDay.toString())
            }
        }
        // endregion
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        label = 0
        frecuencia = 0
    }
}