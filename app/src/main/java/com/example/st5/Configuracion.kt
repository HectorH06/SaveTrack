package com.example.st5

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.example.st5.databinding.FragmentConfiguracionBinding
import com.example.st5.models.*
import com.polyak.iconswitch.IconSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

class Configuracion : Fragment() {
    private lateinit var binding: FragmentConfiguracionBinding

    private var isDarkMode = false
    private var notifActive = false

    private val jsonArrayMonto = JSONArray()
    private val jsonArrayMontoGrupo = JSONArray()
    private val jsonArrayGrupos = JSONArray()
    private val jsonArrayLabels = JSONArray()
    private val jsonArrayEventos = JSONArray()
    private val jsonArrayConySug = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(activity, Index::class.java)
                    intent.putExtra("isDarkMode", !isDarkMode)
                    intent.putExtra("currentView", 0)
                    startActivity(intent)
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

    private suspend fun areNotifEnabled(context: Context): Boolean {
        var modo: Boolean
        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(context).getAssetsDao()
            val mode = assetsDao.getNotif()
            modo = mode != 0
        }
        return modo
    }

    private suspend fun updateTheme(context: Context, komodo: Long){
        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(context).getAssetsDao()
            assetsDao.updateTheme(komodo)
        }
    }

    private suspend fun updateNotif(context: Context, modo: Long){
        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(context).getAssetsDao()
            assetsDao.updateNotif(modo)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfiguracionBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            isDarkMode = isDarkModeEnabled(requireContext())
            notifActive = areNotifEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
                binding.claroscuro.checked = IconSwitch.Checked.RIGHT
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
                binding.claroscuro.checked = IconSwitch.Checked.LEFT
                isDarkMode = true
            }
            if (notifActive) {
                binding.notificame.checked = IconSwitch.Checked.LEFT
            } else {
                binding.notificame.checked = IconSwitch.Checked.RIGHT
            }

            Log.i("MODO", isDarkMode.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goback.setOnClickListener {
            val intent = Intent(activity, Index::class.java)
            intent.putExtra("isDarkMode", !isDarkMode)
            intent.putExtra("currentView", 0)
            startActivity(intent)
        }

        binding.claroscuro.setCheckedChangeListener {
            when (binding.claroscuro.checked) {
                IconSwitch.Checked.LEFT -> {
                    binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
                    binding.bar.setTextColor(resources.getColor(R.color.X0))
                    binding.Modo.setTextColor(resources.getColor(R.color.X0))
                    binding.Notificaciones.setTextColor(resources.getColor(R.color.X0))
                    binding.faq.setTextColor(resources.getColor(R.color.X0))
                    binding.logout.setTextColor(resources.getColor(R.color.X0))
                    binding.by.setTextColor(resources.getColor(R.color.X0))
                    binding.email.setTextColor(resources.getColor(R.color.X0))
                    binding.goback.setBackgroundResource(R.drawable.ic_back_dark)
                    lifecycleScope.launch{
                        updateTheme(requireContext(), 0)
                    }
                    isDarkMode = true
                }
                IconSwitch.Checked.RIGHT -> {
                    binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
                    binding.bar.setTextColor(resources.getColor(R.color.X4))
                    binding.Modo.setTextColor(resources.getColor(R.color.X4))
                    binding.Notificaciones.setTextColor(resources.getColor(R.color.X4))
                    binding.faq.setTextColor(resources.getColor(R.color.X4))
                    binding.logout.setTextColor(resources.getColor(R.color.X4))
                    binding.by.setTextColor(resources.getColor(R.color.X4))
                    binding.email.setTextColor(resources.getColor(R.color.X4))
                    binding.goback.setBackgroundResource(R.drawable.ic_back_light)
                    lifecycleScope.launch{
                        updateTheme(requireContext(), 1)
                    }
                    isDarkMode = false
                }
                else -> {}
            }
        }

        binding.notificame.setCheckedChangeListener {
            when (binding.notificame.checked) {
                IconSwitch.Checked.LEFT -> {
                    lifecycleScope.launch{
                        updateNotif(requireContext(), 1)
                    }
                    notifActive = true
                }
                IconSwitch.Checked.RIGHT -> {
                    lifecycleScope.launch{
                        updateNotif(requireContext(), 0)
                    }
                    notifActive = false
                }
                else -> {}
            }
        }

        binding.manu.setOnClickListener {
            val decoder = Decoder(requireContext())
            if (decoder.hayNet()) {
                manu()
            } else {
                Toast.makeText(requireContext(), "No hay acceso a internet", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cerrarsesion.setOnClickListener {
            val decoder = Decoder(requireContext())
            binding.perame.alpha = 1f
            if (decoder.hayNet()) {
                suspend fun cerrarSesion() {
                    val queue = Volley.newRequestQueue(requireContext())
                    withContext(Dispatchers.IO) {
                        val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                        val ingresosGastosDao =
                            Stlite.getInstance(requireContext()).getIngresosGastosDao()
                        val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                        val montoGrupoDao = Stlite.getInstance(requireContext()).getMontoGrupoDao()
                        val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
                        val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()
                        val assetsDao = Stlite.getInstance(requireContext()).getAssetsDao()
                        val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()
                        val conySugDao = Stlite.getInstance(requireContext()).getConySugDao()

                        val perocuantosmontos = montoDao.getMaxMonto()
                        val perocuantosmontosg = montoGrupoDao.getMaxMontoGrupo()
                        val perocuantosgrupos = gruposDao.getMaxGrupo()
                        val perocuantaslabels = labelsDao.getMaxLabel()
                        val perocuantoseventos = eventosDao.getMaxEvento()
                        val perocuantosconsejos = conySugDao.getMaxConsejo()

                        val iduser = usuarioDao.checkId().toLong()
                        val username = usuarioDao.checkName()
                        val edad = usuarioDao.checkAge().toLong()
                        val lachamba = usuarioDao.checkChamba().toLong()
                        val diasaho = usuarioDao.checkDiasaho().toLong()
                        val balance = usuarioDao.checkBalance()
                        val foto = usuarioDao.checkFoto()
                        val meta = usuarioDao.checkMeta()
                        val summaryingresos = ingresosGastosDao.checkSummaryI()
                        val summarygastos = ingresosGastosDao.checkSummaryG()
                        val tema = assetsDao.getTheme().toLong()
                        val lastprocess = assetsDao.getLastProcess()

                        val viejoUsuario = Usuario(
                            iduser = iduser,
                            nombre = username,
                            edad = edad,
                            chamba = lachamba,
                            foto = foto,
                            diasaho = diasaho,
                            balance = balance,
                            meta = meta
                        )
                        val viejosIG = IngresosGastos(
                            iduser = iduser,
                            summaryingresos = summaryingresos,
                            summarygastos = summarygastos
                        )
                        val viejosAssets = Assets(
                            idtheme = 0,
                            theme = tema,
                            lastprocess = lastprocess
                        )


                        val selectedbefore = usuarioDao.getUserData()
                        Log.v("PRE SELECTED USERS", selectedbefore.toString())

                        /*
                    UPLOADING BACKUP
                    */

                        // Tabla Usuario
                        val jsonObjectUsuario = JSONObject()
                        jsonObjectUsuario.put("iduser", viejoUsuario.iduser)
                        jsonObjectUsuario.put("edad", viejoUsuario.edad)
                        jsonObjectUsuario.put("nombre", viejoUsuario.nombre)
                        jsonObjectUsuario.put("chamba", viejoUsuario.chamba)
                        jsonObjectUsuario.put("foto", viejoUsuario.foto)
                        jsonObjectUsuario.put("diasaho", viejoUsuario.diasaho)
                        jsonObjectUsuario.put("balance", viejoUsuario.balance)
                        jsonObjectUsuario.put("meta", viejoUsuario.meta)
                        jsonObjectUsuario.put("theme", viejosAssets.theme)
                        jsonObjectUsuario.put("lastprocess", viejosAssets.lastprocess)

                        // Tabla IngresosGastos
                        val jsonObjectIngresosGastos = JSONObject()
                        jsonObjectIngresosGastos.put("iduser", viejosIG.iduser)
                        jsonObjectIngresosGastos.put("summaryingresos", viejosIG.summaryingresos)
                        jsonObjectIngresosGastos.put("summarygastos", viejosIG.summarygastos)

                        // Tabla Monto
                        for (idmonto in 0..perocuantosmontos) {
                            if (montoDao.getConcepto(idmonto) != null) {
                                Log.v("Current idmonto", idmonto.toString())
                                val viejoMonto = Monto(
                                    idmonto = montoDao.getIdmonto(idmonto),
                                    iduser = montoDao.getIduser(idmonto),
                                    concepto = montoDao.getConcepto(idmonto),
                                    valor = montoDao.getValor(idmonto),
                                    valorfinal = montoDao.getValorFinal(idmonto),
                                    fecha = montoDao.getFecha(idmonto),
                                    frecuencia = montoDao.getFrecuencia(idmonto),
                                    etiqueta = montoDao.getEtiqueta(idmonto),
                                    interes = montoDao.getInteres(idmonto),
                                    tipointeres = montoDao.getTipoInteres(idmonto),
                                    veces = montoDao.getVeces(idmonto),
                                    estado = montoDao.getEstado(idmonto),
                                    adddate = montoDao.getAdded(idmonto),
                                    enddate = montoDao.getEnded(idmonto),
                                    cooldown = montoDao.getCooldown(idmonto),
                                    delay = montoDao.getDelay(idmonto),
                                    sequence = montoDao.getSequence(idmonto)
                                )
                                Log.v("Current monto $idmonto", viejoMonto.toString())
                                val jsonObjectMonto = JSONObject()
                                jsonObjectMonto.put("idmonto", viejoMonto.idmonto)
                                jsonObjectMonto.put("iduser", viejoMonto.iduser)
                                jsonObjectMonto.put("concepto", viejoMonto.concepto)
                                jsonObjectMonto.put("valor", viejoMonto.valor)
                                jsonObjectMonto.put("valorfinal", viejoMonto.valorfinal)
                                jsonObjectMonto.put("fecha", viejoMonto.fecha)
                                jsonObjectMonto.put("frecuencia", viejoMonto.frecuencia)
                                jsonObjectMonto.put("etiqueta", viejoMonto.etiqueta)
                                jsonObjectMonto.put("interes", viejoMonto.interes)
                                jsonObjectMonto.put("tipointeres", viejoMonto.tipointeres)
                                jsonObjectMonto.put("veces", viejoMonto.veces)
                                jsonObjectMonto.put("estado", viejoMonto.estado)
                                jsonObjectMonto.put("adddate", viejoMonto.adddate)
                                jsonObjectMonto.put("enddate", viejoMonto.enddate)
                                jsonObjectMonto.put("cooldown", viejoMonto.cooldown)
                                jsonObjectMonto.put("delay", viejoMonto.delay)
                                jsonObjectMonto.put("sequence", viejoMonto.sequence)

                                jsonArrayMonto.put(jsonObjectMonto)

                                Log.v("Current object", jsonObjectMonto.toString())
                                Log.v("Current array", jsonArrayMonto.toString())
                            } else {
                                Log.v("Current monto $idmonto", "VACÍO")
                            }
                        }

                        // Tabla MontoGrupo
                        for (idmonto in 0..perocuantosmontosg) {
                            if (montoGrupoDao.getIdMonto(idmonto) != null && montoGrupoDao.getIdGrupo(
                                    idmonto
                                ) != null
                            ) {
                                Log.v("Current idmonto", idmonto.toString())
                                val viejoMontoGrupo = MontoGrupo(
                                    idmonto = montoGrupoDao.getIdMonto(idmonto),
                                    idgrupo = montoGrupoDao.getIdGrupo(idmonto),
                                    iduser = montoGrupoDao.getIdUser(idmonto),
                                )
                                Log.v("Current label $idmonto", viejoMontoGrupo.toString())
                                val jsonObjectMontoGrupo = JSONObject()
                                jsonObjectMontoGrupo.put("idmonto", viejoMontoGrupo.idmonto)
                                jsonObjectMontoGrupo.put("idgrupo", viejoMontoGrupo.idgrupo)
                                jsonObjectMontoGrupo.put("iduser", viejoMontoGrupo.iduser)

                                jsonArrayMontoGrupo.put(jsonObjectMontoGrupo)

                                Log.v("Current object", jsonObjectMontoGrupo.toString())
                                Log.v("Current array", jsonArrayMontoGrupo.toString())
                            } else {
                                Log.v("Current montogrupo $idmonto", "VACÍO")
                            }
                        }

                        // Tabla Grupos
                        for (Id in 0..perocuantosgrupos) {
                            if (gruposDao.getIdGrupo(Id) != null && gruposDao.getNameG(Id) != null) {
                                Log.v("Current idlabel", Id.toString())
                                val viejoGrupo = Grupos(
                                    Id = gruposDao.getIdGrupo(Id),
                                    nameg = gruposDao.getNameG(Id),
                                    description = gruposDao.getDescription(Id),
                                    type = gruposDao.getType(Id),
                                    admin = gruposDao.getAdmin(Id),
                                    idori = gruposDao.getIdori(Id),
                                    color = gruposDao.getColor(Id),
                                    enlace = gruposDao.getEnlace(Id)
                                )
                                Log.v("Current label $Id", viejoGrupo.toString())
                                val jsonObjectGrupos = JSONObject()
                                jsonObjectGrupos.put("Id", viejoGrupo.Id)
                                jsonObjectGrupos.put("name", viejoGrupo.nameg)
                                jsonObjectGrupos.put("description", viejoGrupo.description)
                                jsonObjectGrupos.put("type", viejoGrupo.type)
                                jsonObjectGrupos.put("admin", viejoGrupo.admin)
                                jsonObjectGrupos.put("idori", viejoGrupo.idori)
                                jsonObjectGrupos.put("color", viejoGrupo.color)
                                jsonObjectGrupos.put("enlace", viejoGrupo.enlace)

                                jsonArrayGrupos.put(jsonObjectGrupos)

                                Log.v("Current object", jsonObjectGrupos.toString())
                                Log.v("Current array", jsonArrayGrupos.toString())
                            } else {
                                Log.v("Current grupo $Id", "VACÍO")
                            }
                        }

                        // Tabla Labels
                        for (idlabel in 1..perocuantaslabels) {
                            if (labelsDao.getIdLabel(idlabel) != null && labelsDao.getPlabel(idlabel) != null) {
                                Log.v("Current idlabel", idlabel.toString())
                                val viejaLabel = Labels(
                                    idlabel = labelsDao.getIdLabel(idlabel),
                                    plabel = labelsDao.getPlabel(idlabel),
                                    color = labelsDao.getColor(idlabel),
                                    estado = labelsDao.getEstado(idlabel)
                                )
                                Log.v("Current label $idlabel", viejaLabel.toString())
                                val jsonObjectLabels = JSONObject()
                                jsonObjectLabels.put("idlabel", viejaLabel.idlabel)
                                jsonObjectLabels.put("plabel", viejaLabel.plabel)
                                jsonObjectLabels.put("color", viejaLabel.color)
                                jsonObjectLabels.put("estado", viejaLabel.estado)

                                jsonArrayLabels.put(jsonObjectLabels)

                                Log.v("Current object", jsonObjectLabels.toString())
                                Log.v("Current array", jsonArrayLabels.toString())
                            } else {
                                Log.v("Current label $idlabel", "VACÍO")
                            }
                        }

                        for (idevento in 1..perocuantoseventos) {
                            if (eventosDao.getIdevento(idevento) != null && eventosDao.getNombre(
                                    idevento
                                ) != null
                            ) {
                                Log.v("Current evento", idevento.toString())
                                val viejoEvento = Eventos(
                                    idevento = eventosDao.getIdevento(idevento),
                                    nombre = eventosDao.getNombre(idevento),
                                    fecha = eventosDao.getFecha(idevento),
                                    frecuencia = eventosDao.getFrecuencia(idevento),
                                    etiqueta = eventosDao.getEtiqueta(idevento),
                                    estado = eventosDao.getEstado(idevento),
                                    adddate = eventosDao.getAddDate(idevento)
                                )
                                Log.v("Current evento $idevento", viejoEvento.toString())
                                val jsonObjectEventos = JSONObject()
                                jsonObjectEventos.put("idevento", viejoEvento.idevento)
                                jsonObjectEventos.put("nombre", viejoEvento.nombre)
                                jsonObjectEventos.put("fecha", viejoEvento.fecha)
                                jsonObjectEventos.put("frecuencia", viejoEvento.frecuencia)
                                jsonObjectEventos.put("etiqueta", viejoEvento.etiqueta)
                                jsonObjectEventos.put("estado", viejoEvento.estado)
                                jsonObjectEventos.put("adddate", viejoEvento.adddate)

                                jsonArrayEventos.put(jsonObjectEventos)

                                Log.v("Current object", jsonObjectEventos.toString())
                                Log.v("Current array", jsonArrayEventos.toString())
                            } else {
                                Log.v("Current evento $idevento", "VACÍO")
                            }
                        }

                        for (idcon in 1..perocuantosconsejos) {
                            if (conySugDao.getIdcon(idcon) != null && conySugDao.getNombre(idcon) != null) {
                                Log.v("Current idcon", idcon.toString())
                                val viejoConsejo = ConySug(
                                    idcon = conySugDao.getIdcon(idcon),
                                    nombre = conySugDao.getNombre(idcon),
                                    contenido = conySugDao.getContenido(idcon),
                                    estado = conySugDao.getEstado(idcon),
                                    flag = conySugDao.getFlag(idcon),
                                    type = conySugDao.getType(idcon),
                                    style = conySugDao.getStyle(idcon)
                                )
                                Log.v("Current consejo $idcon", viejoConsejo.toString())
                                val jsonObjectCon = JSONObject()
                                jsonObjectCon.put("idcon", viejoConsejo.idcon)
                                jsonObjectCon.put("nombre", viejoConsejo.nombre)
                                jsonObjectCon.put("contenido", viejoConsejo.contenido)
                                jsonObjectCon.put("estado", viejoConsejo.estado)
                                jsonObjectCon.put("flag", viejoConsejo.flag)
                                jsonObjectCon.put("type", viejoConsejo.type)
                                jsonObjectCon.put("style", viejoConsejo.style)

                                jsonArrayConySug.put(jsonObjectCon)

                                Log.v("Current object", jsonObjectCon.toString())
                                Log.v("Current array", jsonArrayConySug.toString())
                            } else {
                                Log.v("Current consejo $idcon", "VACÍO")
                            }
                        }

                        Log.v("jsonObjectUsuario", jsonObjectUsuario.toString())

                        val uploadurl =
                            "http://savetrack.com.mx/backupput.php?username=$username&backup=$jsonObjectUsuario"
                        val uploadReq: StringRequest =
                            object : StringRequest(
                                Method.PUT,
                                uploadurl,
                                Response.Listener { response ->
                                    Log.d(
                                        "response", response
                                    )
                                },
                                Response.ErrorListener { error ->
                                    Log.e(
                                        "API error", "error => $error"
                                    )
                                }) {
                                override fun getBody(): ByteArray {
                                    return uploadurl.toByteArray(
                                        Charset.defaultCharset()
                                    )
                                }
                            }
                        Log.d(
                            "uploadReq", uploadReq.toString()
                        )
                        queue.add(uploadReq)

                        val upload2url =
                            "http://savetrack.com.mx/backupput2.php?username=$username&backup=$jsonObjectIngresosGastos"
                        val upload2Req: StringRequest =
                            object : StringRequest(
                                Method.PUT,
                                upload2url,
                                Response.Listener { response ->
                                    Log.d(
                                        "response", response
                                    )
                                },
                                Response.ErrorListener { error ->
                                    Log.e(
                                        "API error", "error => $error"
                                    )
                                }) {
                                override fun getBody(): ByteArray {
                                    return upload2url.toByteArray(
                                        Charset.defaultCharset()
                                    )
                                }
                            }
                        Log.d(
                            "uploadReq", upload2Req.toString()
                        )
                        queue.add(upload2Req)

                        val upload3url =
                            "http://savetrack.com.mx/backupput3.php?username=$username&backup=$jsonArrayMonto"
                        val upload3Req: StringRequest =
                            object : StringRequest(
                                Method.PUT,
                                upload3url,
                                Response.Listener { response ->
                                    Log.d(
                                        "response", response
                                    )
                                },
                                Response.ErrorListener { error ->
                                    Log.e(
                                        "API error", "error => $error"
                                    )
                                }) {
                                override fun getBody(): ByteArray {
                                    return upload3url.toByteArray(
                                        Charset.defaultCharset()
                                    )
                                }
                            }
                        Log.d(
                            "uploadReq", upload3Req.toString()
                        )
                        queue.add(upload3Req)

                        val upload4url =
                            "http://savetrack.com.mx/backupput4.php?username=$username&backup=$jsonArrayMontoGrupo"
                        val upload4Req: StringRequest =
                            object : StringRequest(
                                Method.PUT,
                                upload4url,
                                Response.Listener { response ->
                                    Log.d(
                                        "response", response
                                    )
                                },
                                Response.ErrorListener { error ->
                                    Log.e(
                                        "API error", "error => $error"
                                    )
                                }) {
                                override fun getBody(): ByteArray {
                                    return upload4url.toByteArray(
                                        Charset.defaultCharset()
                                    )
                                }
                            }
                        Log.d(
                            "uploadReq", upload4Req.toString()
                        )
                        queue.add(upload4Req)

                        val upload5url =
                            "http://savetrack.com.mx/backupput5.php?username=$username&backup=$jsonArrayGrupos"
                        val upload5Req: StringRequest =
                            object : StringRequest(
                                Method.PUT,
                                upload5url,
                                Response.Listener { response ->
                                    Log.d(
                                        "response", response
                                    )
                                },
                                Response.ErrorListener { error ->
                                    Log.e(
                                        "API error", "error => $error"
                                    )
                                }) {
                                override fun getBody(): ByteArray {
                                    return upload5url.toByteArray(
                                        Charset.defaultCharset()
                                    )
                                }
                            }
                        Log.d(
                            "uploadReq", upload5Req.toString()
                        )
                        queue.add(upload5Req)

                        val upload6url =
                            "http://savetrack.com.mx/backupput6.php?username=$username&backup=$jsonArrayLabels"
                        val upload6Req: StringRequest =
                            object : StringRequest(
                                Method.PUT,
                                upload6url,
                                Response.Listener { response ->
                                    Log.d(
                                        "response", response
                                    )
                                },
                                Response.ErrorListener { error ->
                                    Log.e(
                                        "API error", "error => $error"
                                    )
                                }) {
                                override fun getBody(): ByteArray {
                                    return upload6url.toByteArray(
                                        Charset.defaultCharset()
                                    )
                                }
                            }
                        Log.d(
                            "uploadReq", upload6Req.toString()
                        )
                        queue.add(upload6Req)

                        val upload7url =
                            "http://savetrack.com.mx/backupput7.php?username=$username&backup=$jsonArrayEventos"
                        val upload7Req: StringRequest =
                            object : StringRequest(
                                Method.PUT,
                                upload7url,
                                Response.Listener { response ->
                                    Log.d(
                                        "response", response
                                    )
                                },
                                Response.ErrorListener { error ->
                                    Log.e(
                                        "API error",
                                        "error => $error"
                                    )
                                }) {
                                override fun getBody(): ByteArray {
                                    return upload7url.toByteArray(
                                        Charset.defaultCharset()
                                    )
                                }
                            }
                        Log.d(
                            "uploadReq", upload7Req.toString()
                        )
                        queue.add(upload7Req)

                        val upload8url =
                            "http://savetrack.com.mx/backupput8.php?username=$username&backup=$jsonArrayConySug"
                        val upload8Req: StringRequest =
                            object : StringRequest(
                                Method.PUT,
                                upload8url,
                                Response.Listener { response ->
                                    Log.d(
                                        "response", response
                                    )
                                },
                                Response.ErrorListener { error ->
                                    Log.e(
                                        "API error",
                                        "error => $error"
                                    )
                                }) {
                                override fun getBody(): ByteArray {
                                    return upload8url.toByteArray(
                                        Charset.defaultCharset()
                                    )
                                }
                            }
                        Log.d(
                            "uploadReq", upload8Req.toString()
                        )
                        queue.add(upload8Req)

                        usuarioDao.clean()
                        ingresosGastosDao.clean()
                        montoDao.clean()
                        montoGrupoDao.clean()
                        gruposDao.clean()
                        labelsDao.clean()

                        val selectedafter = usuarioDao.getUserData()
                        Log.v(
                            "POST SELECTED USERS", selectedafter.toString()
                        )
                    }
                }

                lifecycleScope.launch {
                    cerrarSesion()
                }
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "No hay acceso a internet", Toast.LENGTH_SHORT).show()
                binding.perame.alpha = 0f
            }
        }
    }

    private fun manu() {
        val pdfUrl = "http://savetrack.com.mx/Manu.pdf"
        val request = DownloadManager.Request(Uri.parse(pdfUrl))
        request.setTitle("Descarga de Manual para el Usuario")
        request.setDescription("Descargando archivo PDF")
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ManualDeUsuarioSavetrack07092023.pdf")
        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(pdfUrl)
        startActivity(intent)
    }
}