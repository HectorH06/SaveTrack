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
import com.example.st5.databinding.FragmentFinanzaseventosaddBinding
import com.example.st5.models.Eventos
import com.polyak.iconswitch.IconSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*

class finanzasEventosAdd : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentFinanzaseventosaddBinding

    private var label: Int = 0
    private var frecuencia: Int = 0
    private var fecha: Int = 0
    private var estado: Int = 0
    private var selectedDay = 39
    private var selectedLabel: String? = "Seleccionar"
    private var selectedfr: String? = "Seleccionar"

    private var mutableEtiquetas: MutableList<String> = mutableListOf()
    private var mutableIds: MutableList<Long> = mutableListOf()
    private var mutableColores: MutableList<Int> = mutableListOf()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

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
        binding = FragmentFinanzaseventosaddBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        
        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, Configuracion()).addToBackStack(null).commit()
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
                            30, 61, 91, 122, 183, 365 -> {
                                val intday = binding.FechaField.dayOfMonth
                                Log.w("DAY", intday.toString())

                                intday
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

                        val adddateStr: String = LocalDate.now().toString()
                        val adddate = adddateStr.replace("-", "").toInt()

                        Log.v("Concepto", concepto)
                        Log.v("Fecha", fecha.toString())
                        Log.v("Frecuencia", frecuencia.toString())
                        Log.v("Etiqueta", label.toString())
                        Log.v("Addate", adddate.toString())
                        lifecycleScope.launch {
                            eventoadd(
                                concepto,
                                fecha,
                                frecuencia,
                                label,
                                estado,
                                adddate
                            )
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
    
    private suspend fun eventoadd(
        nombre: String,
        fecha: Int,
        frecuencia: Int,
        etiqueta: Int,
        estado: Int,
        adddate: Int
    ) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()

            val iduser = usuarioDao.checkId().toLong()

            val nuevoEvento = Eventos(
                nombre = nombre,
                fecha = fecha, 
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                estado = estado,
                adddate = adddate
            )

            eventosDao.insertEvento(nuevoEvento)
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
                binding.FechaField.calendarViewShown = false
            }
            "Mensual" -> {
                frecuencia = 30
                selectedDay = 40
                binding.FechaField.calendarViewShown = true
            }
            "Bimestral" -> {
                frecuencia = 61
                selectedDay = 40
                binding.FechaField.calendarViewShown = true
            }
            "Trimestral" -> {
                frecuencia = 91
                selectedDay = 40
                binding.FechaField.calendarViewShown = true
            }
            "Cuatrimestral" -> {
                frecuencia = 122
                selectedDay = 40
                binding.FechaField.calendarViewShown = true
            }
            "Semestral" -> {
                frecuencia = 183
                selectedDay = 40
                binding.FechaField.calendarViewShown = true
            }
            "Anual" -> {
                frecuencia = 365
                selectedDay = 40
                binding.FechaField.calendarViewShown = true
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