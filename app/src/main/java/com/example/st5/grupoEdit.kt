package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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
import com.example.st5.databinding.FragmentEditgrupoBinding
import com.example.st5.models.Grupos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yuku.ambilwarna.AmbilWarnaDialog
import java.util.*


class grupoEdit : Fragment() {
    private var color: Int = 0xffffff

    private lateinit var binding: FragmentEditgrupoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_perfil2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_perfil)
            }

            Log.i("MODO", isDarkMode.toString())
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = gruposList()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.GruposContainer, prev)
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
        binding = FragmentEditgrupoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = gruposList()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.GruposContainer, back).addToBackStack(null).commit()
        }

        fun colorPicker(supportsAlpha: Boolean) {
            val dialog = AmbilWarnaDialog(
                requireContext(),
                color,
                supportsAlpha,
                object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                        this@grupoEdit.color = color
                        binding.ColorField.setBackgroundColor(color)
                    }

                    override fun onCancel(dialog: AmbilWarnaDialog) {
                        Toast.makeText(requireContext(), "cancel", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            dialog.show()
        }

        binding.ColorField.setOnClickListener {
            colorPicker(false)
        }

        binding.Confirm.setOnClickListener {
            val nombre = binding.NombreField.text.toString()
            val descrip = binding.DescripcionField.text.toString()

            if (nombre != "" && color != 0xffffff && descrip.length <= 100) {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres guardar cambios?")
                    .setPositiveButton("Guardar") { dialog, _ ->
                        Log.v("Nombre", nombre)
                        Log.v("Descripcion", descrip)
                        Log.v("Color", color.toString())

                        lifecycleScope.launch {
                            grupoEdit(nombre, descrip, color)
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                            .replace(R.id.GruposContainer, back).addToBackStack(null).commit()
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
                        .replace(R.id.GruposContainer, back).addToBackStack(null).commit()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            cancelDialog.show()
        }
    }

    private suspend fun grupoEdit(
        nombre: String,
        descripcion: String,
        color: Int
    ) {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

            val iduser = usuarioDao.checkId()

            val id = gruposDao.getIdGrupo(iduser)
            //companion object de Id
            val viejoGrupo = Grupos(
                Id = id,
                nameg = nombre,
                description = descripcion,
                type = gruposDao.getType(id.toInt()),
                admin = iduser.toLong(),
                idori = 0,
                color = color,
                enlace = ""
            )

            gruposDao.updateGrupo(viejoGrupo)
            val grupos = gruposDao.getAllGrupos()
            Log.i("ALL GRUPOS", grupos.toString())

        }
    }
}