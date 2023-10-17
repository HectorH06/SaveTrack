package com.example.st5

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
import com.example.st5.databinding.FragmentHistorialeditlabelBinding
import com.example.st5.models.Labels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yuku.ambilwarna.AmbilWarnaDialog

class historialUpdate : Fragment() {
    private var color: Int = 0xffffff

    private lateinit var binding: FragmentHistorialeditlabelBinding

    companion object {
        private const val idl = "ide"
        private const val nombr = "nombre"
        private const val colo = "color"

        fun sendLabel(
            ide: Long,
            nombre: String,
            color: Int,
        ): historialUpdate {
            val fragment = historialUpdate()
            val args = Bundle()
            args.putLong(idl, ide)
            Log.i("id", idl)
            args.putString(nombr, nombre)
            args.putInt(colo, color)

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
                    val prev = historialmain()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.historial_container, prev)
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
        binding = FragmentHistorialeditlabelBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_historial2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_historial)
            }

            Log.i("MODO", isDarkMode.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(idl)
        val name = arguments?.getString(nombr) ?: "NO NAME"
        color = arguments?.getInt(colo) ?: 0x00ff00
        Log.v("ID", id.toString())
        Log.v("PLABEL", name)
        Log.v("COLOR", color.toString())

        binding.PlabelField.setText(name)
        binding.ColorField.setBackgroundColor(color)

        val back = historialEtiquetas()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.historial_container, back).addToBackStack(null).commit()
        }

        fun colorPicker(supportsAlpha: Boolean) {
            val dialog = AmbilWarnaDialog(
                requireContext(),
                color,
                supportsAlpha,
                object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                        this@historialUpdate.color = color
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
            val plabel = binding.PlabelField.text.toString()

            if (plabel != "" && color != 0xffffff) {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres guardar cambios?")
                    .setPositiveButton("Guardar") { dialog, _ ->
                        Log.v("Plabel", plabel)
                        Log.v("Color", color.toString())

                        val idlabel = arguments?.getLong(idl) ?: 0

                        lifecycleScope.launch {
                            labeledit(idlabel, plabel, color)
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                            .replace(R.id.historial_container, back).addToBackStack(null).commit()
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
                        .replace(R.id.historial_container, back).addToBackStack(null).commit()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            cancelDialog.show()
        }
    }

    private suspend fun labeledit(
        idlabel: Long,
        plabel: String,
        color: Int
    ) {
        withContext(Dispatchers.IO) {
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val oldLabel = Labels(
                idlabel = idlabel,
                plabel = plabel,
                color = color
            )

            labelsDao.updateLabel(oldLabel)
            val labels = labelsDao.getAllLabels()
            Log.i("ALL LABELS", labels.toString())

        }
    }
}