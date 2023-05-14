package com.example.st5

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPerfileditarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class perfileditar : Fragment() {
    private lateinit var binding: FragmentPerfileditarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = perfilmain()
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
        binding = FragmentPerfileditarBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val back = perfilmain()
        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.ViewContainer, back).addToBackStack(null).commit()
        }

        binding.Confirm.setOnClickListener {
            val confirmDialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Seguro que quieres guardar cambios?")
                .setPositiveButton("Guardar") { dialog, _ ->
                    lifecycleScope.launch {
                        guardarCambios()
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

        lifecycleScope.launch {
            mostrarDatos()
        }


    }

    private suspend fun mostrarDatos() {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(
                requireContext()
            ).getUsuarioDao()

            Log.v(
                "id", id.toString()
            )
            val nombre = usuarioDao.checkName()
            val edad = usuarioDao.checkAge()
            val chamba = usuarioDao.checkChamba()

            Log.v("Name", nombre)
            Log.v("Age", edad.toString())
            Log.v("Chamba", chamba.toString())

            binding.UsernameeditperfTV.text = nombre
            binding.AgeeditperfTV.setText(edad.toString())
        }
    }

    private suspend fun guardarCambios() {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

            val idt = usuarioDao.checkId()
            val nuevoNombre = binding.UsernameeditperfTV.text.toString()
            val nuevaEdad = binding.AgeeditperfTV.text.toString()

            // ARBOL DE DECISIONES PARA CADA CASO DE CHAMBA
            // AUTÓMATA FINITO o algo así
            /*
            CHAMBAS
                1. Salario
                2. Vendedor
                3. Pensionado
                4. Becado
                5. Mantenido
                6. Inversionista
            */

            val c = arrayOf("0", "0", "0", "0", "0", "0")

            if (binding.chamba1.isChecked) {
                c[0] = "1"
            }
            if (binding.chamba2.isChecked) {
                c[1] = "2"
            }
            if (binding.chamba3.isChecked) {
                c[2] = "3"
            }
            if (binding.chamba4.isChecked) {
                c[3] = "4"
            }
            if (binding.chamba5.isChecked) {
                c[4] = "5"
            }
            if (binding.chamba6.isChecked) {
                c[5] = "6"
            }

            var cFinal = ""
            for (v in c) {
                cFinal += v
            }

            val nuevaChamba = cFinal.toLong()

            if (nuevoNombre.isEmpty() || nuevaEdad.isEmpty() || cFinal.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Todos los campos deben ser completados",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@withContext
            }

            usuarioDao.updateAge(idt, nuevaEdad.toLong())
            usuarioDao.updateChamba(idt, nuevaChamba)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Cambios guardados correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}