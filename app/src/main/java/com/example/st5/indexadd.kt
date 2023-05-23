package com.example.st5

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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


class indexadd : Fragment() {
    private lateinit var binding: FragmentIndexaddBinding

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
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.ViewContainer, prev)
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
        lifecycleScope.launch{
            masmenos(switchValue)
        }
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.etiquetaoptions,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.LabelField.adapter = adapter

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.ViewContainer, back).addToBackStack(null).commit()
        }

        binding.updownSwitch.setCheckedChangeListener{
            when (binding.updownSwitch.checked){
                IconSwitch.Checked.LEFT -> binding.ValorField.hint = "Ingreso"
                IconSwitch.Checked.RIGHT -> binding.ValorField.hint = "Gasto"
                else -> {binding.ValorField.hint = "youputo"}
            }
        }

        binding.Confirm.setOnClickListener{
            val concepto = binding.ConceptoField.text.toString()
            val valor = binding.ValorField.text.toString().toDouble()
            val fecha = binding.FechaField.text.toString()
            val frecuencia = binding.FrecuenciaField.text.toString().toLong()
            val tipo = binding.TipoField.text.toString()
            var label: Long = 0
            binding.LabelField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedLabel = parent.getItemAtPosition(position).toString()
                    Log.v("ETIQUETAA", selectedLabel)

                    label = when (selectedLabel) {
                        "Alimento" -> 1
                        "Hogar" -> 2
                        "Bienestar" -> 3
                        "Otras necesidades" -> 4
                        "Gasto hormiga" -> 5
                        "Ocio y demás" -> 6
                        else -> 0
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            val confirmDialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Seguro que quieres guardar cambios?")
                .setPositiveButton("Guardar") { dialog, _ ->
                    Log.v("Concepto", concepto)
                    Log.v("Valor", valor.toString())
                    Log.v("Fecha", fecha)
                    Log.v("Frecuencia", frecuencia.toString())
                    Log.v("Tipo", tipo)
                    Log.v("Etiqueta", label.toString())
                    lifecycleScope.launch{
                        montoadd(concepto, valor, fecha, frecuencia, tipo, label)
                    }
                    dialog.dismiss()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.ViewContainer, back).addToBackStack(null).commit()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            confirmDialog.show()
        }

        binding.Cancel.setOnClickListener {
            val cancelDialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Seguro que quieres descartar cambios?")
                .setPositiveButton("Descartar") { dialog, _ ->
                    dialog.dismiss()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.ViewContainer, back).addToBackStack(null).commit()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            cancelDialog.show()
        }

    }

    private fun masmenos(switchValue: Boolean){
        Log.v("masomenos", switchValue.toString())
            if (switchValue){
                binding.ValorField.hint = "Ingreso"
            } else {
                binding.ValorField.hint = "Gasto"
                binding.updownSwitch.checked = IconSwitch.Checked.RIGHT
            }
    }

    private suspend fun montoadd(concepto: String, valor: Double, fecha: String, frecuencia: Long, tipo: String, etiqueta: Long) {
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
                tipo = tipo,
                etiqueta = etiqueta
            )

            montoDao.insertMonto(nuevoMonto)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())
        }
    }
}