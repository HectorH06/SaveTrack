package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentMontosgrupoBinding
import com.example.st5.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class montosGrupo : Fragment() {
    private lateinit var binding: FragmentMontosgrupoBinding

    private var montos: MutableList<Monto> = mutableListOf()
    private var miembros: MutableList<Usuario> = mutableListOf()
    private lateinit var montosGrupo: List<MontoGrupo>
    private lateinit var group: Grupos
    private var iduser: Long = 0L

    companion object {
        private const val grupo = "grupor"
        fun grupoSearch(grup: Long): montosGrupo {
            val fragment = montosGrupo()
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

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMontosgrupoBinding.inflate(inflater, container, false)
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
                    binding.bar.text = group.nameg

                    Log.v("idori", group.idori.toString())
                    Log.v("admin", group.admin.toString())
                    val jsonArr = withContext(Dispatchers.IO) { JSONArray(URL("http://savetrack.com.mx/montosgrupoMultiget?localid=${group.idori}&admin=${group.admin}").readText()) }
                    Log.v("jsonArr", jsonArr.toString())
                    for (i in 0 until jsonArr.length()) {
                        val jsonObj = jsonArr.getJSONObject(i)
                        if (jsonObj.getLong("idmglocal") != null) {
                            val idmonto: Long = jsonObj.getLong("idmglocal")
                            val iduse: Long = jsonObj.getLong("idusuario")
                            val concepto: String = jsonObj.optString("concepto")
                            val valor: Double = jsonObj.optDouble("valor")
                            val valorfinal: Double = jsonObj.optDouble("valorfinal")
                            val fecha: Int = jsonObj.optInt("fecha")
                            val frecuencia: Int = jsonObj.optInt("frecuencia")
                            val etiqueta: Int = jsonObj.optInt("etiqueta")
                            val interes: Double = jsonObj.optDouble("interes")
                            val tipointeres: Int = jsonObj.optInt("tipointeres")
                            val veces: Long = jsonObj.optLong("veces")
                            val estado: Int = jsonObj.optInt("estado")
                            val adddate: Int = jsonObj.optInt("adddate")
                            val enddate: Int = jsonObj.optInt("enddate")
                            val cooldown: Int = jsonObj.optInt("cooldown")
                            val delay: Int = jsonObj.optInt("delay")
                            val sequence: String = jsonObj.optString("sequence")

                            val nuevoMonto = Monto(
                                idmonto = idmonto,
                                iduser = iduse,
                                concepto = concepto,
                                valor = valor,
                                valorfinal = valorfinal,
                                fecha = fecha,
                                frecuencia = frecuencia,
                                etiqueta = 8000 + group.Id.toInt(),
                                interes = interes,
                                tipointeres = tipointeres,
                                veces = veces,
                                estado = estado,
                                adddate = adddate,
                                enddate = enddate,
                                cooldown = cooldown,
                                delay = delay,
                                sequence = sequence
                            )
                            Log.v("Current monto $i", nuevoMonto.toString())

                            montos.add(nuevoMonto)
                        } else {
                            Log.v("Current monto $i", "VACÍO")
                        }
                    }

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
            binding.displayGastos.adapter = MontoAdapter(montos)
            binding.totalG.text = "$" + decoder.format(0.0).toString()
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
                    .replace(R.id.GruposContainer, gruposList()).addToBackStack(null)
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

        binding.AgregarGastoButton.setOnClickListener {
            if (idgrupo != null) {
                val add = grupoMontoAdd.sendGrupo(idgrupo)
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                    .replace(R.id.GruposContainer, add).addToBackStack(null).commit()
            }
        }

        binding.MembersButton.setOnClickListener {
            if (idgrupo != null) {
                val members = grupoMembersList.sendGrupo(idgrupo)
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                    .replace(R.id.GruposContainer, members).addToBackStack(null).commit()
            }
        }
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

    private suspend fun montoPapelera(
        idmonto: Long,
        concepto: String,
        valor: Double,
        fecha: Int?,
        frecuencia: Int?,
        etiqueta: Int,
        interes: Double?,
        veces: Long?,
        estado: Int?,
        adddate: Int
    ) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            var status = 2
            when (estado) {
                1 -> status = 2
                6 -> status = 7
            }

            val enddate = montoDao.getEnded(idmonto.toInt())
            val valorfinal = montoDao.getValorFinal(idmonto.toInt())
            val tipointeres = montoDao.getTipoInteres(idmonto.toInt())
            val delay = montoDao.getDelay(idmonto.toInt())
            val sequence = montoDao.getSequence(idmonto.toInt())
            val cooldown = montoDao.getCooldown(idmonto.toInt())
            val iduser = usuarioDao.checkId().toLong()
            val viejoMonto = Monto(
                idmonto = idmonto,
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
                estado = status,
                adddate = adddate,
                enddate = enddate,
                cooldown = cooldown,
                delay = delay,
                sequence = sequence
            )

            montoDao.updateMonto(viejoMonto)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())

        }
    }

    private suspend fun montoFavorito(
        idmonto: Long,
        concepto: String,
        valor: Double,
        fecha: Int?,
        frecuencia: Int?,
        etiqueta: Int,
        interes: Double?,
        veces: Long?,
        estado: Int?,
        adddate: Int
    ) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val status = when (estado) {
                0 -> 3
                1 -> 4
                5 -> 8
                6 -> 9

                3 -> 0
                4 -> 1
                8 -> 5
                9 -> 6

                else -> 3
            }
            val enddate = montoDao.getEnded(idmonto.toInt())
            val valorfinal = montoDao.getValorFinal(idmonto.toInt())
            val tipointeres = montoDao.getTipoInteres(idmonto.toInt())
            val delay = montoDao.getDelay(idmonto.toInt())
            val sequence = montoDao.getSequence(idmonto.toInt())
            val cooldown = montoDao.getCooldown(idmonto.toInt())
            val iduser = usuarioDao.checkId().toLong()
            val viejoMonto = Monto(
                idmonto = idmonto,
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
                estado = status,
                adddate = adddate,
                enddate = enddate,
                cooldown = cooldown,
                delay = delay,
                sequence = sequence
            )

            montoDao.updateMonto(viejoMonto)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())

        }
    }

    private inner class MontoAdapter(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val conceptoTextView: TextView,
            val valorTextView: TextView,
            val fechaTextView: TextView,
            val etiquetaTextView: TextView,
            val favM: Button,
            val updateM: Button,
            val deleteM: Button
        ) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_montogrupo, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.IConcepto)
            val valorTextView = itemView.findViewById<TextView>(R.id.IValor)
            val fechaTextView = itemView.findViewById<TextView>(R.id.IFecha)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.IEtiqueta)
            val favM = itemView.findViewById<Button>(R.id.favMonto)
            val updateM = itemView.findViewById<Button>(R.id.editMonto)
            val deleteM = itemView.findViewById<Button>(R.id.deleteMonto)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                fechaTextView,
                etiquetaTextView,
                favM,
                updateM,
                deleteM
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            val searchId = monto.iduser
            val miembro = miembros.find { it.iduser == searchId }
            val idgrupo: Long? = arguments?.getLong(grupo)
            var tempstat = 5
            val decoder = Decoder(requireContext())
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = decoder.format(monto.valor).toString()
            holder.fechaTextView.text = monto.fecha?.let { decoder.date(it) }
            if (miembro != null) { holder.etiquetaTextView.text = miembro.nombre }
            if (monto.estado == 0 || monto.estado == 1 || monto.estado == 5 || monto.estado == 6){
                holder.favM.setBackgroundResource(R.drawable.ic_notstar)
                tempstat = 5
            }
            if (monto.estado == 3 || monto.estado == 4 || monto.estado == 8 || monto.estado == 9){
                holder.favM.setBackgroundResource(R.drawable.ic_star)
                tempstat = 8
            }
            holder.favM.setOnClickListener {
                if (tempstat == 5){
                    holder.favM.setBackgroundResource(R.drawable.ic_star)
                    tempstat = 8
                }
                if (tempstat == 8){
                    holder.favM.setBackgroundResource(R.drawable.ic_notstar)
                    tempstat = 5
                }
                lifecycleScope.launch {
                    montoFavorito(
                        monto.idmonto,
                        monto.concepto,
                        monto.valor,
                        monto.fecha,
                        monto.frecuencia,
                        monto.etiqueta,
                        monto.interes,
                        monto.veces,
                        monto.estado,
                        monto.adddate
                    )
                }
            }
            val upup = idgrupo?.let {
                if (false) {
                    grupoEdit.sendGrupo(it)
                } else {
                    grupoView.sendGrupo(it)
                }
            }
            holder.updateM.setOnClickListener {
                if (upup != null) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.GruposContainer, upup).addToBackStack(null).commit()
                }
            }
            holder.deleteM.setOnClickListener {
                if (monto.estado == 3 || monto.estado == 4 || monto.estado == 8 || monto.estado == 9 || tempstat == 8) {
                    val confirmDialog = AlertDialog.Builder(requireContext())
                        .setTitle("El monto ${monto.concepto} no se puede eliminar porque está marcado como favorito")
                        .setPositiveButton("Aceptar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    confirmDialog.show()
                } else {
                    val confirmDialog = AlertDialog.Builder(requireContext())
                        .setTitle("¿Seguro que quieres enviar el monto ${monto.concepto} a la papelera?")
                        .setPositiveButton("Eliminar") { dialog, _ ->

                            Log.v("Id del monto actualizado", monto.idmonto.toString())
                            Log.v("Concepto", monto.concepto)
                            Log.v("Valor", monto.valor.toString())
                            Log.v("Fecha", monto.fecha.toString())
                            Log.v("Frecuencia", monto.frecuencia.toString())
                            Log.v("Etiqueta", monto.etiqueta.toString())
                            Log.v("Interes", monto.interes.toString())
                            Log.v("Veces", monto.veces.toString())
                            lifecycleScope.launch {
                                montoPapelera(
                                    monto.idmonto,
                                    monto.concepto,
                                    monto.valor,
                                    monto.fecha,
                                    monto.frecuencia,
                                    monto.etiqueta,
                                    monto.interes,
                                    monto.veces,
                                    monto.estado,
                                    monto.adddate
                                )
                            }
                            dialog.dismiss()
                            val verGrupo = idgrupo?.let { it1 -> grupoSearch(it1) }
                            if (verGrupo != null) {
                                parentFragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                    .replace(R.id.GruposContainer, verGrupo).addToBackStack(null)
                                    .commit()
                            }
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    confirmDialog.show()
                }
            }
            if (position == montos.size - 1){
                holder.itemView.setBackgroundResource(R.drawable.p1bottomcell)
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }
    }
}