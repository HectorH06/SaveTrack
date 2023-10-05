package com.example.st5

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentGruposmiembroslistBinding
import com.example.st5.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class grupoMembersList : Fragment() {
    private lateinit var binding: FragmentGruposmiembroslistBinding

    private var montos: MutableList<Monto> = mutableListOf()
    private lateinit var montosGrupo: List<MontoGrupo>
    private lateinit var group: Grupos
    private var miembros: MutableList<Usuario> = mutableListOf()
    private var iduser: Long = 0L

    companion object {
        private const val grupo = "grupor"
        fun sendGrupo(grup: Long): grupoMembersList {
            val fragment = grupoMembersList()
            val args = Bundle()
            Log.i("grup", grup.toString())
            args.putLong(grupo, grup)
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

    private suspend fun montosget(): MutableList<Monto> {
        withContext(Dispatchers.IO) {
            val montoGrupoDao = Stlite.getInstance(requireContext()).getMontoGrupoDao()
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

            val idgrupo: Long? = arguments?.getLong(grupo)
            Log.v("idg", "$idgrupo")
            iduser = usuarioDao.checkId().toLong()

            group = idgrupo?.let { gruposDao.getG(it) }!!
            Log.v("group", "$group")
            montosGrupo = montoGrupoDao.getAllMontosdeGrupo(idgrupo.toInt())

            Log.i("ALL MONTOS", montos.toString())
        }
        return montos
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGruposmiembroslistBinding.inflate(inflater, container, false)
        val decoder = Decoder(requireContext())
        lifecycleScope.launch {
            montos = montosget()
            try {
                val miembrosJSON = withContext(Dispatchers.IO) { JSONArray(URL("http://savetrack.com.mx/gruposMiembrosGet.php?localid=${group.idori}&admin=${group.admin}").readText()) }
                val miembrosG = Array(miembrosJSON.length()) { miembrosJSON.getInt(it) }

                if (!miembrosG.contains(iduser.toInt())) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.GruposContainer, gruposList()).addToBackStack(null).commit()
                    Toast.makeText(
                        requireContext(),
                        "Ya no formas parte del grupo",
                        Toast.LENGTH_SHORT
                    ).show()

                    deleteGrupo()
                } else {
                    val barText = "Miembros de ${group.nameg}"
                    binding.bar.text = barText

                    for (id in miembrosG) {
                        val nombre = withContext(Dispatchers.IO) { URL("http://savetrack.com.mx/usernameget.php?id=$id").readText().trim() }
                        Log.v("Memberrr", nombre)
                        val nuevoMiembro = Usuario(
                            iduser = id.toLong(),
                            nombre = nombre,
                            edad = 0,
                            chamba = 0,
                            foto = "",
                            diasaho = 0,
                            balance = 0.0,
                            meta = 0.0,
                        )
                        miembros.add(nuevoMiembro)
                    }

                    Log.v("idori", group.idori.toString())
                    Log.v("admin", group.admin.toString())
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Error during network call", e)
            }
            binding.displayMiembros.adapter = MiembroAdapter(miembros)
        }
        return binding.root
    }

    private suspend fun deleteGrupo () {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val idg = arguments?.getLong(grupo)
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
                val color = labelsDao.getColor(8000 + idg.toInt())
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

        val idgrupo: Long? = arguments?.getLong(grupo)
        Log.i("etiqueta", idgrupo.toString())

        val back = gruposList()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.GruposContainer, back).addToBackStack(null).commit()
        }

        binding.ConfigButton.setOnClickListener {
            var viewOrEdit: Fragment = grupoEdit()
            if (idgrupo != null) {
                viewOrEdit = if (group.admin == iduser) {
                    grupoEdit.sendGrupo(idgrupo)
                } else {
                    grupoView.sendGrupo(idgrupo)
                }
            }
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.GruposContainer, viewOrEdit).addToBackStack(null).commit()
        }

        binding.ShareButton.setOnClickListener {
            // TODO invitaciones
        }
    }

    private inner class MiembroAdapter (private val miembros: List<Usuario>):
        RecyclerView.Adapter<MiembroAdapter.MiembroViewHolder>() {
        inner class MiembroViewHolder(
            itemView: View,
            val nombreTextView: TextView,
            val adminIcon: ImageView,
            val montosTextView: TextView,
            val aporteTextView: TextView,
            val kickButton: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiembroViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_miembro, parent, false)
            val nombreTextView = itemView.findViewById<TextView>(R.id.UNombre)
            val adminIcon = itemView.findViewById<ImageView>(R.id.admin)
            val montosTextView = itemView.findViewById<TextView>(R.id.UMontos)
            val aporteTextView = itemView.findViewById<TextView>(R.id.UAporte)
            val kickButton = itemView.findViewById<Button>(R.id.expulsarbutton)
            return MiembroViewHolder(
                itemView,
                nombreTextView,
                adminIcon,
                montosTextView,
                aporteTextView,
                kickButton
            )
        }


        override fun onBindViewHolder(holder: MiembroViewHolder, position: Int) {
            val miembro = miembros[position]
            val idgrupo: Long? = arguments?.getLong(grupo)
            holder.nombreTextView.text = miembro.nombre
            if (group.admin == miembro.iduser){
                holder.adminIcon.alpha = 1f
            }
            //holder.montosTextView.text = montosDeMiembro.size.toString()
            //holder.aporteTextView.text = montosDeMiembro.sum().toString()
            if (iduser == group.admin) {
                holder.kickButton.alpha = 1f
                holder.kickButton.translationZ = 400f
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de montossss", miembros.size.toString())
            return miembros.size
        }
    }
}