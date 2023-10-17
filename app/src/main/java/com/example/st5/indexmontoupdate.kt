package com.example.st5

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
import com.example.st5.databinding.FragmentIndexmontoupdateBinding
import com.example.st5.models.Monto
import com.polyak.iconswitch.IconSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs

class indexmontoupdate : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentIndexmontoupdateBinding

    private var label: Int = 0
    private var frecuencia: Int = 0
    private var fecha: Int = 0
    private var interes: Double = 0.0
    private var selectedDay = 39
    private var selectedLabel: String? = "Seleccionar"
    private var selectedfr: String? = "Seleccionar"

    companion object {
        private const val switchval = "switchValue"
        fun newInstance(switchValue: Boolean): indexmontoupdate {
            val fragment = indexmontoupdate()
            val args = Bundle()
            args.putBoolean(switchval, switchValue)
            fragment.arguments = args
            return fragment
        }

        private const val idm = "ide"
        private const val concep = "concept"
        private const val valu = "value"
        private const val dat = "date"
        private const val frequenc = "frequency"
        private const val labe = "label"
        private const val interes = "interest"
        private const val time = "times"
        private const val adddat = "adddate"

        fun sendMonto(
            ide: Long,
            concept: String,
            value: Double,
            date: Int?,
            frequency: Int?,
            label: Int,
            interest: Double?,
            times: Long?,
            adddate: Int
        ): indexmontoupdate {
            val fragment = indexmontoupdate()
            val args = Bundle()
            args.putLong(idm, ide)
            Log.i("id", idm)
            args.putString(concep, concept)
            args.putDouble(valu, value)
            if (date != null) {
                args.putInt(dat, date)
            }
            if (frequency != null) {
                args.putInt(frequenc, frequency)
            }
            args.putInt(labe, label)
            if (interest != null) {
                args.putDouble(interes, interest)
            }
            if (times != null) {
                args.putLong(time, times)
            }
            args.putInt(adddat, adddate)
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

        withContext(Dispatchers.IO){
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
        binding = FragmentIndexmontoupdateBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_index2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_index)
            }

            Log.i("MODO", isDarkMode.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = indexmain()

        binding.ConceptoField.setText(arguments?.getString(concep))
        binding.InteresField.setText(arguments?.getDouble(interes.toString()).toString())

        val aux = arguments?.getDouble(valu)
        var switchValue = false
        if (aux != null) {
            if (aux >= 0){switchValue = true}
        }

        lifecycleScope.launch {
            masmenos(switchValue)
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
        val adapterG = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.etiquetaoptions,
            android.R.layout.simple_spinner_item
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
                }
                IconSwitch.Checked.RIGHT -> {
                    binding.ValorField.hint = "Gasto"
                    binding.LabelField.adapter = adapterG
                }
                else -> {}
            }
            hideAll()
        }

        binding.LabelField.onItemSelectedListener = this

        binding.FrecuenciaField.onItemSelectedListener = this

        val max = 100
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

        binding.WeekField.selectionMode = SingleSelectionMode.create()
        binding.WeekField.locale = Locale.getDefault()
        binding.WeekField.setDaySelectionChangedListener { selectedDays ->
            if (selectedDays.isNotEmpty()) {
                var aux = selectedDays[0].name.toLowerCase()
                selectedDay = when (aux) {
                    "monday" -> 41
                    "tuesday" -> 42
                    "wednesday" -> 43
                    "thursday" -> 44
                    "friday" -> 45
                    "saturday" -> 46
                    "sunday" -> 47

                    else -> {40}
                }
                Log.i("Día seleccionado", selectedDay.toString())
            } else {
                Log.i("Día seleccionado", "NONE")
            }
        }

        binding.Confirm.setOnClickListener {
            val concepto = binding.ConceptoField.text.toString()
            val valorstr = binding.ValorField.text.toString()
            var veces = arguments?.getLong(time)

            var interes = 0.0

            if (label != 0 && concepto != "" && valorstr != "" && valorstr != "." && selectedDay != 39) {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres guardar cambios en el monto $concepto?")
                    .setPositiveButton("Guardar") { dialog, _ ->

                        val idn = arguments?.getLong(idm)
                        var idm = 0L
                        if (idn != null){
                            idm = idn.toLong()
                        }


                        var valor = valorstr.toDouble()
                        valor = truncateDouble(valor)
                        if (label <= 8) {
                            valor *= -1
                        }

                        if (label == 8 || label == 16) {
                            interes = binding.InteresField.text.toString().toDouble()
                        }

                        fecha = when(frecuencia){
                            0 -> {
                                val intyear = binding.FechaField.year - 1900
                                Log.w("YEAR", intyear.toString())
                                val intmonth = binding.FechaField.month
                                Log.w("MONTH", intmonth.toString())
                                val intday = binding.FechaField.dayOfMonth
                                Log.w("DAY", intday.toString())
                                val datedate = "$intyear$intmonth$intday"
                                Log.w("DATE", datedate)

                                if (label == 10){
                                    40
                                } else {
                                    datedate.toInt()
                                }
                            } // Único
                            1 -> {
                                veces = 1
                                100
                            } // Diario
                            7, 14 -> {selectedDay} // Semanales
                            30, 61, 91, 122, 183 -> {
                                val intday = binding.FechaField.dayOfMonth
                                Log.w("DAY", intday.toString())
                                val datedate = "$intday"
                                Log.w("DATE", datedate)

                                datedate.toInt()
                            } // Mensuales
                            365 -> {
                                val intmonth = binding.FechaField.month
                                Log.w("MONTH", intmonth.toString())
                                val intday = binding.FechaField.dayOfMonth
                                Log.w("DAY", intday.toString())
                                val datedate = "$intmonth-$intday"
                                Log.w("DATE", datedate)

                                datedate.toInt()
                            } // Anual
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

                        val adddate: Int = arguments?.getInt(adddat) ?: 0

                        Log.v("Id del monto actualizado", id.toString())
                        Log.v("Concepto", concepto)
                        Log.v("Valor", valor.toString())
                        Log.v("Fecha", fecha.toString())
                        Log.v("Frecuencia", frecuencia.toString())
                        Log.v("Etiqueta", label.toString())
                        Log.v("Interes", interes.toString())
                        Log.v("Veces", veces.toString())
                        Log.v("FECHA de CREACIÓN", adddate.toString())
                        lifecycleScope.launch {
                            montoupdate(idm, concepto, valor, fecha, frecuencia, label, interes, veces, adddate)
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

    private fun masmenos(switchValue: Boolean) {
        Log.v("masomenos", switchValue.toString())

        val adapterI = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipooptions,
            android.R.layout.simple_spinner_item
        )
        val adapterG = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.etiquetaoptions,
            android.R.layout.simple_spinner_item
        )
        adapterG.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterI.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val valu: Double? = arguments?.let { abs(it.getDouble(valu)) }
        if (switchValue) {
            binding.ValorField.hint = "$0.00"
            binding.ValorField.setText(valu.toString())
            binding.LabelField.adapter = adapterI
        } else {
            binding.ValorField.hint = "$0.00"
            binding.ValorField.setText(valu.toString())
            binding.LabelField.adapter = adapterG
            binding.updownSwitch.checked = IconSwitch.Checked.RIGHT
        }
    }

    private suspend fun montoupdate(
        id: Long,
        concepto: String,
        valor: Double,
        fecha: Int,
        frecuencia: Int,
        etiqueta: Int,
        interes: Double,
        veces: Long?,
        adddate: Int
    ) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val enddate = montoDao.getEnded(id.toInt())
            val cooldown = montoDao.getCooldown(id.toInt())
            val iduser = usuarioDao.checkId().toLong()
            val viejoMonto = Monto(
                idmonto = id,
                iduser = iduser,
                concepto = concepto,
                valor = valor,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                interes = interes,
                veces = veces,
                adddate = adddate,
                enddate = enddate,
                cooldown = cooldown
            )

            montoDao.updateMonto(viejoMonto)
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
        binding.LabelField.setBackgroundResource(R.drawable.p1midcell)
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
        binding.LabelField.setBackgroundResource(R.drawable.p1bottomcell)
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

    private fun hideAll() {
        hideWeekField()
        hideFrecField()
        hideFechaField()
        hideInteresField()
    }

    private fun truncateDouble(value: Double): Double {
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(value).toDouble()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selectedLabel = binding.LabelField.selectedItem?.toString()
        selectedfr = binding.FrecuenciaField.selectedItem?.toString()

        // region LABELS
        if (selectedLabel != null) {
            Log.v("ETIQUETA", selectedLabel.toString())
        }
        when (selectedLabel) {
            "Alimento" -> {
                label = 1
                displayFrecField()
                hideInteresField()
            }
            "Hogar" -> {
                label = 2
                displayFrecField()
                hideInteresField()
            }
            "Bienestar" -> {
                label = 3
                displayFrecField()
                hideInteresField()
            }
            "Otras necesidades" -> {
                label = 4
                displayFrecField()
                hideInteresField()
            }
            "Gasto hormiga" -> {
                label = 5
                displayFrecField()
                hideInteresField()
            }
            "Ocio y demás" -> {
                label = 6
                displayFrecField()
                hideInteresField()
            }
            "Obsequio" -> {
                label = 7
                frecuencia = 0
                hideFrecField()
                displayFechaField()
                hideInteresField()
            }
            "Deuda" -> {
                label = 8
                displayFrecField() //Justificar la deuda y condiciones con intereses
                displayInteresField()
            }


            "Salario" -> {
                label = 9
                displayFrecField()
                hideInteresField()
            }
            "Venta" -> {
                label = 10
                hideFrecField()
                hideInteresField()
            }
            "Beca" -> {
                label = 11
                displayFrecField()
                hideInteresField()
            }
            "Pensión" -> {
                label = 12
                displayFrecField()
                hideInteresField()
            }
            "Manutención" -> {
                label = 13
                displayFrecField()
                hideInteresField()
            }
            "Ingreso pasivo" -> {
                label = 14
                hideFrecField()
                hideInteresField()
            }
            "Regalo" -> {
                label = 15
                frecuencia = 0
                hideFrecField()
                hideInteresField()
            }
            "Préstamo" -> {
                label = 16
                displayFrecField() //Justificar préstamo con reglas de deudas, e intereses
                displayInteresField()
            }

            else -> {
                label = 0
                hideAll()
            }
        }
        // endregion

        // region FRECUENCIAS
        if (selectedfr != null) {
            Log.v("FRECUENCIA", selectedfr.toString())
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