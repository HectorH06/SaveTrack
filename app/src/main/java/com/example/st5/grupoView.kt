package com.example.st5

import android.annotation.SuppressLint
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
import com.example.st5.databinding.FragmentViewgrupoBinding
import com.example.st5.models.Grupos
import com.example.st5.models.Labels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.util.*


class grupoView : Fragment() {
    private var color: Int = 0xffffff
    private var idori: Long = 0
    private var admin: Long = 0
    private var iduser: Long = 0

    private lateinit var binding: FragmentViewgrupoBinding

    companion object {
        private const val idv = "idg"
        fun sendGrupo(
            idg: Long
        ): grupoView {
            val fragment = grupoView()
            val args = Bundle()
            args.putLong(idv, idg)
            Log.i("idv", idv)
            fragment.arguments = args
            return fragment
        }
    }

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
        binding = FragmentViewgrupoBinding.inflate(inflater, container, false)
        colorPicker()
        return binding.root
    }

    private fun colorPicker() {
        binding.ColorField.setBackgroundColor(color) //ponerle su companion object
    }

    private suspend fun getGrupo () {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

            val idg = arguments?.getLong(idv)
            if (idg != null) {
                idori = gruposDao.getIdori(idg.toInt())
                admin = gruposDao.getAdmin(idg.toInt())
                iduser = usuarioDao.checkId().toLong()
            }
        }
    }

    private suspend fun deleteGrupo () {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val idg = arguments?.getLong(idv)
            if (idg != null) {
                val muertoGrupo = Grupos(
                    Id = idg,
                    nameg = gruposDao.getNameG(idg.toInt()),
                    description = gruposDao.getDescription(idg.toInt()),
                    type = 2,
                    admin = gruposDao.getAdmin(idg.toInt()),
                    idori = gruposDao.getIdori(idg.toInt()),
                    color = gruposDao.getColor(idg.toInt()),
                    enlace = gruposDao.getEnlace(idg.toInt())
                )
                gruposDao.updateGrupo(muertoGrupo)

                val plabel = labelsDao.getPlabel(8000 + idg.toInt())
                val muertaLabel = Labels(
                    idlabel = 8000 + idg,
                    plabel = plabel,
                    color = color,
                    estado = 1
                )

                labelsDao.updateLabel(muertaLabel)
                val labelss = labelsDao.getAllLabels()
                Log.i("ALL LABELS", labelss.toString())

                parentFragmentManager.beginTransaction()
                    .replace(R.id.historial_container, historialPapelera()).addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = gruposList()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.GruposContainer, back).addToBackStack(null).commit()
        }

        binding.Confirm.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.GruposContainer, back).addToBackStack(null).commit()

        }

        lifecycleScope.launch {
            getGrupo()
            val jsonObjectG = JSONObject(URL("http://savetrack.com.mx/grupoGet.php?localid=$idori&admin=$admin").readText())
            val miembrosJSON = JSONArray(URL("http://savetrack.com.mx/gruposMiembrosGet.php?localid=$idori&admin=$admin").readText())
            val miembrosG = Array(miembrosJSON.length()) {miembrosJSON.getInt(it)}

            val idgglobal: Long = jsonObjectG.getLong("idgrupoglobal")
            val idglocal: String = jsonObjectG.getString("idgrupolocal")
            val idadmin: Long = jsonObjectG.optLong("idadmin")

            val nombre: String = jsonObjectG.optString("nombre")
            val tipo: Int = jsonObjectG.optInt("tipo")
            val desc: String = jsonObjectG.optString("descripcion")
            val color: Int = jsonObjectG.optInt("color")
            val created: String = jsonObjectG.optString("created")
            val createdString = "Creado el $created"

            if (!miembrosG.contains(iduser.toInt())) {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                    .replace(R.id.GruposContainer, gruposList()).addToBackStack(null).commit()
                Toast.makeText(requireContext(), "Ya no formas parte del grupo", Toast.LENGTH_SHORT).show()

                deleteGrupo()

                binding.NombreField.text = nombre
                binding.ColorField.setBackgroundColor(color)
                binding.DescripcionField.text = desc
                binding.TypeField.text = when (tipo) {
                    0 -> "Fijo"
                    1 -> "Temporal"
                    else -> "Eliminado"
                }
                binding.AdminField.text = URL("http://savetrack.com.mx/usernameget.php?id=$admin").readText()
                binding.CreatedField.text = createdString
            }
        }
    }
}