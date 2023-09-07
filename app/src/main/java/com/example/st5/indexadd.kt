package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ca.antonious.materialdaypicker.SingleSelectionMode
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentIndexaddBinding
import com.example.st5.models.Monto
import com.polyak.iconswitch.IconSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class indexadd : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentIndexaddBinding

    private var label: Int = 0
    private var frecuencia: Int = 0
    private var fecha: Int = 0
    private var estado: Int = 0
    private var interes: Double = 0.0
    private var tipointeres: Int = 0
    private var selectedDay = 39
    private var enddate = 30001231
    private var selectedLabel: String? = "Seleccionar"
    private var selectedfr: String? = "Seleccionar"

    private var mutableEtiquetas: MutableList<String> = mutableListOf()
    private var mutableIds: MutableList<Long> = mutableListOf()
    private var mutableColores: MutableList<Int> = mutableListOf()

    companion object {
        private const val switchval = "switchValue"
        fun newInstance(switchValue: Boolean): indexadd {
            val fragment = indexadd()
            val args = Bundle()
            args.putBoolean(switchval, switchValue)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_index2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_index)
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
        binding = FragmentIndexaddBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = indexmain()

        val switchValue = arguments?.getBoolean(switchval) ?: false
        lifecycleScope.launch {
            masmenos(switchValue)
            getLabels()
        }

        val adapterF = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.frecuenciaoptions,
            android.R.layout.simple_spinner_item
        )
        val adapterI = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipooptions,
            android.R.layout.simple_spinner_item
        )
        val arrayEtiquetas = mutableEtiquetas
        val adapterG = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayEtiquetas
        )
        adapterF.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterG.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterI.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.FrecuenciaField.adapter = adapterF
        binding.FrecuenciaField.alpha = 0f

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.index_container, back).addToBackStack(null).commit()
        }

        binding.updownSwitch.setCheckedChangeListener {
            when (binding.updownSwitch.checked) {
                IconSwitch.Checked.LEFT -> {
                    binding.ValorField.hint = "Ingreso"
                    binding.LabelField.adapter = adapterI
                    binding.yocreoquesi.text = "Préstamo"
                }
                IconSwitch.Checked.RIGHT -> {
                    binding.ValorField.hint = "Gasto"
                    binding.LabelField.adapter = adapterG
                    binding.yocreoquesi.text = "Deuda"
                }
                else -> {}
            }
            hideAll()
        }

        binding.LabelField.onItemSelectedListener = this

        binding.FrecuenciaField.onItemSelectedListener = this

        val max = 10000
        val min = 0
        binding.InteresSeekbar.max = max
        binding.InteresSeekbar.min = min


        binding.InteresSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress.toFloat() / 100
                binding.InteresField.setText(value.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.InteresField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty()) {
                    val value = text.toFloat() * 100
                    binding.InteresSeekbar.progress = value.toInt()
                }
            }
        })

        binding.yocreoquesi.setOnClickListener {
            if (binding.yocreoquesi.isChecked) {
                displayFrecField() //Justificar la deuda y condiciones con intereses
                displayInteresField()
                displayFechaFinalField()
                displayIC()
            } else {
                hideFrecField()
                hideInteresField()
                hideFechaFinalField()
                hideIC()
            }
        }


        binding.WeekField.selectionMode = SingleSelectionMode.create()
        binding.WeekField.locale = Locale.getDefault()
        binding.WeekField.setDaySelectionChangedListener { selectedDays ->
            if (selectedDays.isNotEmpty()) {
                val aux = selectedDays[0].name.lowercase()
                selectedDay = when (aux) {
                    "monday" -> 41
                    "tuesday" -> 42
                    "wednesday" -> 43
                    "thursday" -> 44
                    "friday" -> 45
                    "saturday" -> 46
                    "sunday" -> 47

                    else -> {
                        40
                    }
                }
                Log.i("Día seleccionado", selectedDay.toString())
            } else {
                Log.i("Día seleccionado", "NONE")
            }
        }

        binding.Confirm.setOnClickListener {
            val concepto = binding.ConceptoField.text.toString()
            val valorstr = binding.ValorField.text.toString()
            var veces = 0L

            var interes = 0.0

            if (label != 0 && concepto != "" && valorstr != "" && valorstr != "." && selectedDay != 39) {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres guardar cambios?")
                    .setPositiveButton("Guardar") { dialog, _ ->
                        var valor = valorstr.toDouble()
                        valor = truncateDouble(valor)
                        var valorfinal = valor

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
                            1 -> {
                                veces = 0
                                100
                            } // Diario
                            7, 14 -> {
                                selectedDay
                            } // Semanales
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

                        if (binding.yocreoquesi.isChecked) {
                            estado = 5
                            interes = binding.InteresField.text.toString().toDouble()
                            val day = binding.FechaFinalField.dayOfMonth
                            val fDay = String.format("%02d", day)
                            val month = binding.FechaFinalField.month + 1
                            val fMonth = String.format("%02d", month)
                            val year = binding.FechaFinalField.year
                            val datedate = "$year$fMonth$fDay"
                            enddate = datedate.replace("-", "").toInt()

                            val addY = adddate.toString().substring(0, 4).toInt()
                            val addM = adddate.toString().substring(4, 6).toInt()
                            val addD = adddate.toString().substring(6, 8).toInt()
                            val endY = enddate.toString().substring(0, 4).toInt()
                            val endM = enddate.toString().substring(4, 6).toInt()
                            val endD = enddate.toString().substring(6, 8).toInt()
                            val addedAt = LocalDate.of(addY, addM, addD)
                            val toEnd = LocalDate.of(endY, endM, endD)
                            Log.v("FECHASASAS", "$addedAt, $toEnd, ${ChronoUnit.DAYS.between(addedAt, toEnd)}")
                            val duracion: Int = when (frecuencia) {
                                0 -> { 1 }
                                1 -> { ChronoUnit.DAYS.between(addedAt, toEnd).toInt() }
                                7 -> { ChronoUnit.DAYS.between(addedAt, toEnd).toInt()/7 }
                                14 -> { ChronoUnit.DAYS.between(addedAt, toEnd).toInt()/14 }
                                30 -> { ChronoUnit.MONTHS.between(addedAt, toEnd).toInt() }
                                61 -> { ChronoUnit.MONTHS.between(addedAt, toEnd).toInt()/2 }
                                91 -> { ChronoUnit.MONTHS.between(addedAt, toEnd).toInt()/3 }
                                122 -> { ChronoUnit.MONTHS.between(addedAt, toEnd).toInt()/4 }
                                183 -> { ChronoUnit.MONTHS.between(addedAt, toEnd).toInt()/6 }
                                365 -> { ChronoUnit.YEARS.between(addedAt, toEnd).toInt() }
                                else -> { 1 }
                            }

                            val tasa: Double = interes / duracion
                            val aux = valorfinal
                            tipointeres = if (binding.interesCompuesto.isChecked) {
                                valor = (aux / duracion) + aux * (tasa / 100)
                                for (i in 0 until duracion) {
                                    valorfinal += valorfinal * (tasa / 100)
                                }

                                2
                            } else {
                                valorfinal += (valorfinal * interes) / 100
                                valor = valorfinal / duracion

                                1
                            }
                        }

                        Log.v("Concepto", concepto)
                        Log.v("Valor", valor.toString())
                        Log.v("Fecha", fecha.toString())
                        Log.v("Frecuencia", frecuencia.toString())
                        Log.v("Etiqueta", label.toString())
                        Log.v("Interes", interes.toString())
                        Log.v("Veces", veces.toString())
                        Log.v("Addate", adddate.toString())
                        lifecycleScope.launch {
                            montoadd(
                                concepto,
                                valor,
                                valorfinal,
                                fecha,
                                frecuencia,
                                label,
                                interes,
                                tipointeres,
                                veces,
                                estado,
                                adddate
                            )
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                            .replace(R.id.index_container, back).addToBackStack(null).commit()
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
                        .replace(R.id.index_container, back).addToBackStack(null).commit()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            cancelDialog.show()
        }

        hideAll()
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

    private fun masmenos(switchValue: Boolean) {
        Log.v("masomenos", switchValue.toString())

        val adapterI = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipooptions,
            android.R.layout.simple_spinner_item
        )

        val arrayEtiquetas = mutableEtiquetas

        Log.i("ETIQUETAS", "$arrayEtiquetas")
        val adapterG = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayEtiquetas
        )
        adapterG.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterI.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val select = "Seleccionar"
        if (switchValue) {
            binding.ValorField.hint = "$0.00"
            binding.LabelField.adapter = adapterI
            binding.LabelText.text = select
        } else {
            binding.ValorField.hint = "$0.00"
            binding.LabelField.adapter = adapterG
            binding.LabelText.text = select
            binding.updownSwitch.checked = IconSwitch.Checked.RIGHT
        }
    }

    private suspend fun montoadd(
        concepto: String,
        valor: Double,
        valorfinal: Double?,
        fecha: Int,
        frecuencia: Int,
        etiqueta: Int,
        interes: Double,
        tipointeres: Int,
        veces: Long?,
        estado: Int,
        adddate: Int
    ) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val iduser = usuarioDao.checkId().toLong()

            val nuevoMonto = Monto(
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
                estado = estado,
                adddate = adddate,
                enddate = enddate,
                cooldown = 0
            )

            montoDao.insertMonto(nuevoMonto)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())

        }
    }

    private fun displayFrecField() {
        binding.FrecuenciaField.animate()
            .alpha(1f)
            .translationY(0f)
            .translationZ(0f)
            .setDuration(300)
            .setStartDelay(200)
            .setListener(null)
            .start()
        binding.LabelText.setBackgroundResource(R.drawable.p1midcell)
        Log.v("LABEL", label.toString())
    }

    private fun hideFrecField() {
        hideFechaField()
        selectedfr = "Seleccionar"
        binding.FrecuenciaField.animate()
            .alpha(0f)
            .translationY(-50f)
            .translationZ(-100f)
            .setDuration(200)
            .setStartDelay(0)
            .setListener(null)
            .start()
        binding.LabelText.setBackgroundResource(R.drawable.p1bottomcell)
        Log.v("LABEL", label.toString())
    }

    private fun displayFechaField() {
        hideWeekField()
        binding.FechaField.animate()
            .alpha(1f)
            .translationY(0f)
            .translationZ(150f)
            .setDuration(300)
            .setStartDelay(200)
            .setListener(null)
            .start()
        binding.FrecuenciaField.setBackgroundResource(R.drawable.p1midcell)
        Log.v("FECHA", fecha.toString())
    }

    private fun hideFechaField() {
        binding.FechaField.animate()
            .alpha(0f)
            .translationY(-50f)
            .translationZ(-150f)
            .setDuration(200)
            .setStartDelay(0)
            .setListener(null)
            .start()
        binding.FrecuenciaField.setBackgroundResource(R.drawable.p1bottomcell)
        Log.v("FECHA", fecha.toString())
    }

    private fun displayFechaFinalField() {
        hideWeekField()
        binding.FechaFinalField.animate()
            .alpha(1f)
            .translationY(0f)
            .translationZ(150f)
            .setDuration(300)
            .setStartDelay(200)
            .setListener(null)
            .start()
        binding.InteresField.setBackgroundResource(R.drawable.p1midcell)
        Log.v("FECHA", fecha.toString())
    }

    private fun hideFechaFinalField() {
        binding.FechaFinalField.animate()
            .alpha(0f)
            .translationY(-50f)
            .translationZ(-150f)
            .setDuration(200)
            .setStartDelay(0)
            .setListener(null)
            .start()
        binding.InteresField.setBackgroundResource(R.drawable.p1bottomcell)
        Log.v("FECHA", fecha.toString())
    }

    private fun displayWeekField() {
        hideFechaField()
        binding.WeekField.animate()
            .alpha(1f)
            .translationY(0f)
            .translationZ(150f)
            .setDuration(300)
            .setStartDelay(200)
            .setListener(null)
            .start()
        binding.FrecuenciaField.setBackgroundResource(R.drawable.p1bottomcell)
        Log.v("DAY OF WEEK", fecha.toString())
    }

    private fun hideWeekField() {
        selectedDay = 39
        binding.WeekField.animate()
            .alpha(0f)
            .translationY(-50f)
            .translationZ(-150f)
            .setDuration(200)
            .setStartDelay(0)
            .setListener(null)
            .start()
        binding.FrecuenciaField.setBackgroundResource(R.drawable.p1bottomcell)
        Log.v("DAY OF WEEK", fecha.toString())
    }

    private fun displayInteresField() {
        binding.InteresField.animate()
            .alpha(1f)
            .translationY(0f)
            .translationZ(150f)
            .setDuration(300)
            .setStartDelay(200)
            .setListener(null)
            .start()
        binding.InteresSeekbar.animate()
            .alpha(1f)
            .translationY(0f)
            .translationZ(150f)
            .setDuration(300)
            .setStartDelay(200)
            .setListener(null)
            .start()
        binding.FechaField.setBackgroundResource(R.drawable.p1midcell)
        Log.v("INTERÉS", interes.toString())
    }

    private fun hideInteresField() {
        interes = 0.0
        binding.InteresField.animate()
            .alpha(0f)
            .translationY(-50f)
            .translationZ(-150f)
            .setDuration(200)
            .setStartDelay(0)
            .setListener(null)
            .start()
        binding.InteresSeekbar.animate()
            .alpha(0f)
            .translationY(-50f)
            .translationZ(-150f)
            .setDuration(200)
            .setStartDelay(0)
            .setListener(null)
            .start()
        binding.FechaField.setBackgroundResource(R.drawable.p1bottomcell)
        Log.v("INTERÉS", interes.toString())
    }

    private fun displayIC() {
        binding.interesCompuesto.animate()
            .alpha(1f)
            .translationY(0f)
            .translationZ(150f)
            .setDuration(300)
            .setStartDelay(200)
            .setListener(null)
            .start()
    }

    private fun hideIC() {
        binding.interesCompuesto.animate()
            .alpha(0f)
            .translationY(-50f)
            .translationZ(-150f)
            .setDuration(200)
            .setStartDelay(0)
            .setListener(null)
            .start()
    }
    private fun hideAll() {
        hideWeekField()
        hideFrecField()
        hideFechaField()
        hideInteresField()
        hideFechaFinalField()
        hideIC()
    }

    private fun truncateDouble(value: Double): Double {
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(value).toDouble()
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
            "Salario" -> {
                label = 10001
                displayFrecField()
                hideInteresField()
            }
            "Venta" -> {
                label = 10002
                hideFrecField()
                hideInteresField()
            }
            "Beca" -> {
                label = 10003
                displayFrecField()
                hideInteresField()
            }
            "Pensión" -> {
                label = 10004
                displayFrecField()
                hideInteresField()
            }
            "Manutención" -> {
                label = 10005
                displayFrecField()
                hideInteresField()
            }
            "Ingreso pasivo" -> {
                label = 10006
                hideFrecField()
                hideInteresField()
            }
            "Regalo" -> {
                label = 10007
                frecuencia = 0
                hideFrecField()
                hideInteresField()
            }

            "Seleccionar" -> {
                label = 0
            }
            else -> {
                for (i in 0 until mutableEtiquetas.size) {
                    if (selectedLabel == mutableEtiquetas[i]) {
                        label = mutableIds[i].toInt()
                    }
                }
                displayFrecField()
                displayFechaField()
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
                displayFechaField()

                selectedDay = 40
                binding.FechaField.calendarViewShown = false
            }
            "Diario" -> {
                frecuencia = 1
                hideFechaField()

                selectedDay = 40
            }
            "Semanal" -> {
                frecuencia = 7
                displayWeekField()

                selectedDay = 39
            }
            "Quincenal" -> {
                frecuencia = 14
                displayWeekField()

                selectedDay = 39
            }
            "Mensual" -> {
                frecuencia = 30
                displayFechaField()

                selectedDay = 40
                binding.FechaField.calendarViewShown = true

                // TODO guardar día del mes (para meses irregulares, tomar el número máximo del mes si el día rebasa, ej: 31 de enero sería 28 de febrero o 29 si es año biciesto, pero si se elige el 28 de febrero)
                // TODO procesos en segundo plano
            }
            "Bimestral" -> {
                frecuencia = 61
                displayFechaField()

                selectedDay = 40
                binding.FechaField.calendarViewShown = true
            }
            "Trimestral" -> {
                frecuencia = 91
                displayFechaField()

                selectedDay = 40
                binding.FechaField.calendarViewShown = true
            }
            "Cuatrimestral" -> {
                frecuencia = 122
                displayFechaField()

                selectedDay = 40
                binding.FechaField.calendarViewShown = true
            }
            "Semestral" -> {
                frecuencia = 183
                displayFechaField()

                selectedDay = 40
                binding.FechaField.calendarViewShown = true
            }
            "Anual" -> {
                frecuencia = 365
                displayFechaField()

                selectedDay = 40
                binding.FechaField.calendarViewShown = true
            }

            else -> {
                frecuencia = 0

                hideInteresField()
                hideWeekField()
                hideFechaField()

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