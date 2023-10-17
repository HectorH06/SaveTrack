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
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentGruposaddBinding
import com.example.st5.models.Grupos
import com.example.st5.models.Labels
import com.polyak.iconswitch.IconSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import yuku.ambilwarna.AmbilWarnaDialog
import java.nio.charset.Charset
import java.util.*


class gruposAdd : Fragment() {
    private var color: Int = 0xffffff
    private var type: Int = 0

    private lateinit var binding: FragmentGruposaddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        binding = FragmentGruposaddBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_perfil2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_perfil)
            }

            Log.i("MODO", isDarkMode.toString())
        }
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
                        this@gruposAdd.color = color
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

        binding.updownSwitch.setCheckedChangeListener {
            when (binding.updownSwitch.checked) {
                IconSwitch.Checked.LEFT -> {
                    type = 0
                }
                IconSwitch.Checked.RIGHT -> {
                    type = 1
                }
                else -> {}
            }
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
                        Log.v("Tipo", type.toString())
                        Log.v("Color", color.toString())

                        lifecycleScope.launch {
                            grupoAdd(nombre, descrip, type, color)
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

    private suspend fun grupoAdd(
        nombre: String,
        descripcion: String,
        type: Int,
        color: Int
    ) {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val iduser = usuarioDao.checkId()
            val nuevoGrupo = Grupos(
                nameg = nombre,
                description = descripcion,
                type = type,
                admin = iduser.toLong(),
                idori = 0,
                color = color,
                enlace = ""
            )
            gruposDao.insertGrupo(nuevoGrupo)

            val gId = gruposDao.getMaxGrupo().toLong()
            val grupoUp = Grupos(
                Id = gId,
                nameg = nombre,
                description = descripcion,
                type = type,
                admin = iduser.toLong(),
                idori = gId,
                color = color,
                enlace = ""
            )
            gruposDao.updateGrupo(grupoUp)

            val nuevaLabel = Labels(
                idlabel = 8000 + gId,
                plabel = nombre,
                color = color
            )
            labelsDao.insertLabel(nuevaLabel)

            val queue = Volley.newRequestQueue(requireContext())
            var url = "http://savetrack.com.mx/grupoPost.php?"
            val miembros = JSONArray()
            miembros.put(iduser)
            val requestBody = "localid=${grupoUp.Id}&admin=$iduser&miembros=$miembros&nameg=${grupoUp.nameg}&type=${grupoUp.type}&desc=${grupoUp.description}&color=${grupoUp.color}"
            url += requestBody
            val stringReq: StringRequest =
                object : StringRequest(
                    Method.POST, url,
                    Response.Listener { response ->
                        val strResp2 = response.toString()
                        Log.d("API", strResp2)

                    },
                    Response.ErrorListener { error ->
                        Log.d("API", "error => $error")
                    }
                ) {
                    override fun getBody(): ByteArray {
                        return requestBody.toByteArray(Charset.defaultCharset())
                    }
                }
            Log.e("stringReq", stringReq.toString())
            queue.add(stringReq)

            val grupos = gruposDao.getAllGrupos()
            val labels = labelsDao.getAllLabels()
            Log.i("ALL GRUPOS", grupos.toString())
            Log.i("ALL LABELS", labels.toString())

        }
    }
}