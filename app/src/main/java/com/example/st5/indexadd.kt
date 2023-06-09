package com.example.st5

import android.app.AlertDialog
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
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentIndexaddBinding
import com.example.st5.models.Monto
import com.polyak.iconswitch.IconSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.text.DecimalFormat


class indexadd : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentIndexaddBinding

    private var label: Long = 0L
    private var frecuencia: Long = 0L
    private var fecha: String = ""
    private var interes: Double = 0.0

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIndexaddBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = indexmain()

        val switchValue = arguments?.getBoolean(switchval) ?: false
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

        hideFechaField()
        hideInteresField()

        // TODO SPINNER unificar, y hacer opciones para fecha, hacer filtros para la gráfica

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
        }

        binding.LabelField.onItemSelectedListener = this

        binding.FrecuenciaField.onItemSelectedListener = this

        // TODO: INTERÉS de seekbar a textview

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



        binding.Confirm.setOnClickListener {
            val concepto = binding.ConceptoField.text.toString()
            val valorstr = binding.ValorField.text.toString()

            val intyear = binding.FechaField.year - 1900
            Log.w("YEAR", intyear.toString())
            val intmonth = binding.FechaField.month
            Log.w("MONTH", intmonth.toString())
            val intday = binding.FechaField.dayOfMonth
            Log.w("DAY", intday.toString())
            val datedate = Date(intyear, intmonth, intday)
            Log.w("DATE", datedate.toString())
            fecha = datedate.toString()
            // TODO fecha.length == 8, es única, if fecha,length == 16, fecha inicio y fecha final, poner barra superior en cada subvista y duplicar subvista para hacer edición de montos

            var interes = 0.0

            if (label != 0L && concepto != "" && valorstr != "" && valorstr != ".") {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres guardar cambios?")
                    .setPositiveButton("Guardar") { dialog, _ ->
                        var valor = valorstr.toDouble()
                        valor = truncateDouble(valor)
                        if (label <= 8) {
                            valor *= -1
                        }

                        if (label == 8L || label == 16L){
                            interes = binding.InteresField.text.toString().toDouble()
                        }

                        Log.v("Concepto", concepto)
                        Log.v("Valor", valor.toString())
                        Log.v("Fecha", fecha)
                        Log.v("Frecuencia", frecuencia.toString())
                        Log.v("Etiqueta", label.toString())
                        Log.v("Interes", interes.toString())
                        lifecycleScope.launch {
                            montoadd(concepto, valor, fecha, frecuencia, label, interes)
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
        if (switchValue) {
            binding.ValorField.hint = "$0.00"
            binding.LabelField.adapter = adapterI
        } else {
            binding.ValorField.hint = "$0.00"
            binding.LabelField.adapter = adapterG
            binding.updownSwitch.checked = IconSwitch.Checked.RIGHT
        }
    }

    private suspend fun montoadd(
        concepto: String,
        valor: Double,
        fecha: String,
        frecuencia: Long,
        etiqueta: Long,
        interes: Double
    ) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()

            val iduser = usuarioDao.checkId().toLong()
            val nuevoMonto = Monto(
                iduser = iduser,
                concepto = concepto,
                valor = valor,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                interes = interes
            )

            montoDao.insertMonto(nuevoMonto)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())

        }
    }

    private fun displayFrecField(){
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
    private fun truncateDouble(value: Double): Double {
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(value).toDouble()
    }
    private fun hideFrecField(){
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
    private fun displayFechaField(){
        binding.FechaField.animate()
            .alpha(1f)
            .translationY(0f)
            .translationZ(150f)
            .setDuration(300)
            .setStartDelay(200)
            .setListener(null)
            .start()
        binding.FrecuenciaField.setBackgroundResource(R.drawable.p1midcell)
        Log.v("FRECUENCIA", frecuencia.toString())
    }
    private fun hideFechaField(){
        binding.FechaField.animate()
            .alpha(0f)
            .translationY(-50f)
            .translationZ(-150f)
            .setDuration(200)
            .setStartDelay(0)
            .setListener(null)
            .start()
        binding.FrecuenciaField.setBackgroundResource(R.drawable.p1bottomcell)
        Log.v("FRECUENCIA", frecuencia.toString())
    }
    private fun displayInteresField(){
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
    private fun hideInteresField(){
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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val selectedLabel = binding.LabelField.selectedItem?.toString()
        if (selectedLabel != null) {
            Log.v("ETIQUETA", selectedLabel)
        }
        when (selectedLabel) {
            "Alimento" -> {
                label = 1
                displayFrecField()
            }
            "Hogar" -> {
                label = 2
                displayFrecField()
            }
            "Bienestar" -> {
                label = 3
                displayFrecField()
            }
            "Otras necesidades" -> {
                label = 4
                displayFrecField()
            }
            "Gasto hormiga" -> {
                label = 5
                displayFrecField()
            }
            "Ocio y demás" -> {
                label = 6
                displayFrecField()
            }
            "Obsequio" -> {
                label = 7
                hideFrecField()
                displayFechaField()
            }
            "Deuda" -> {
                label = 8
                displayFrecField() //Justificar la deuda y condiciones con intereses
                displayInteresField()
            }


            "Salario" -> {
                label = 9
                displayFrecField()
            }
            "Venta" -> {
                label = 10
                hideFrecField()
            }
            "Beca" -> {
                label = 11
                displayFrecField()
            }
            "Pensión" -> {
                label = 12
                displayFrecField()
            }
            "Manutención" -> {
                label = 13
                displayFrecField()
            }
            "Ingreso pasivo" -> {
                label = 14
                hideFrecField()
            }
            "Regalo" -> {
                label = 15
                hideFrecField()
            }
            "Préstamo" -> {
                label = 16
                displayFrecField() //Justificar préstamo con reglas de deudas, e intereses
                displayInteresField()
            }

            else -> label = 0
        }



        val selectedfr = binding.FrecuenciaField.selectedItem?.toString()

        if (selectedfr != null) {
            Log.v("ETIQUETA", selectedfr)
        }
        when (selectedfr) {
            "Única vez" -> {
                frecuencia = 0
                hideFechaField()
            }
            "Diario" -> {
                frecuencia = 1
                hideFechaField()
            }
            "Semanal" -> {
                frecuencia = 7
                displayFechaField()
                // TODO día de la semana para semanal y quincenal, y de ahí tomar el string de fecha y crear casos para el autoincremento con rangos
            }
            "Quincenal" -> {
                frecuencia = 14
                displayFechaField()
            }
            "Mensual" -> {
                frecuencia = 30 // TODO: revisar lo de meses irregulares a partir de aquí
            }
            "Bimestral" -> {
                frecuencia = 61
            }
            "Trimestral" -> {
                frecuencia = 91
            }
            "Cuatrimestral" -> {
                frecuencia = 122
            }
            "Semestral" -> {
                frecuencia = 183
            }
            "Anual" -> {
                frecuencia = 365
            }

            else -> frecuencia = 0
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        label = 0
        frecuencia = 0
    }
}