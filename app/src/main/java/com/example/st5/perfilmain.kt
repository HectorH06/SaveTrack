package com.example.st5

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPerfilmainBinding
import com.example.st5.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

class perfilmain : Fragment() {
    private lateinit var binding: FragmentPerfilmainBinding

    private val jsonArrayMonto = JSONArray()
    private val jsonArrayLabels = JSONArray()
    private val jsonArrayEventos = JSONArray()
    private val jsonArrayConySug = JSONArray()

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilmainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        suspend fun bajarfoto(link: String) {
            withContext(Dispatchers.IO) {
                binding.ProfilePicture.load(link) {
                    crossfade(true)
                    placeholder(R.drawable.ic_person)
                    transformations(CircleCropTransformation())
                    scale(Scale.FILL)
                }
            }
        }

        binding.cerrarsesionperfilmainbtn.setOnClickListener {

            suspend fun cerrarSesion() {
                val queue = Volley.newRequestQueue(requireContext())
                withContext(Dispatchers.IO) {
                    val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                    val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()
                    val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                    val montoGrupoDao = Stlite.getInstance(requireContext()).getMontoGrupoDao()
                    val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
                    val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()
                    val assetsDao = Stlite.getInstance(requireContext()).getAssetsDao()
                    val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()
                    val conySugDao = Stlite.getInstance(requireContext()).getConySugDao()

                    val perocuantosmontos = montoDao.getMaxMonto()
                    val perocuantaslabels = labelsDao.getMaxLabel()
                    val perocuantoseventos = eventosDao.getMaxEvento()
                    val perocuantosconsejos = conySugDao.getMaxConsejo()
                    Log.v("CUANTOS MONTOS", perocuantosmontos.toString())
                    Log.v("CUANTOS MONTOS", perocuantosmontos.toString())

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

                    val viejoMontoGrupo = MontoGrupo(
                        idmonto = 0,
                        idgrupo = 0,
                        iduser = iduser,
                    )
                    val viejoGrupo = Grupos(
                        Id = 0,
                        name = "",
                        description = "",
                        admin = iduser,
                        nmembers = 1,
                        enlace = ""
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
                    for (idmonto in 1..perocuantosmontos) {
                        if (montoDao.getConcepto(idmonto) != null){
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
                    val jsonObjectMontoGrupo = JSONObject()
                    jsonObjectMontoGrupo.put("idmonto", viejoMontoGrupo.idmonto)
                    jsonObjectMontoGrupo.put("idgrupo", viejoMontoGrupo.idgrupo)
                    jsonObjectMontoGrupo.put("iduser", viejoMontoGrupo.iduser)

                    // Tabla Grupos
                    val jsonObjectGrupos = JSONObject()
                    jsonObjectGrupos.put("Id", viejoGrupo.Id)
                    jsonObjectGrupos.put("name", viejoGrupo.name)
                    jsonObjectGrupos.put("description", viejoGrupo.description)
                    jsonObjectGrupos.put("admin", viejoGrupo.admin)
                    jsonObjectGrupos.put("nmembers", viejoGrupo.nmembers)
                    jsonObjectGrupos.put("enlace", viejoGrupo.enlace)

                    // Tabla Labels
                    for (idlabel in 1..perocuantaslabels) {
                        if (labelsDao.getIdLabel(idlabel) != null && labelsDao.getPlabel(idlabel) != null){
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
                        if (eventosDao.getIdevento(idevento) != null && eventosDao.getNombre(idevento) != null){
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
                        if (conySugDao.getIdcon(idcon) != null && conySugDao.getNombre(idcon) != null){
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
                            jsonObjectCon.put("idlabel", viejoConsejo.idcon)
                            jsonObjectCon.put("plabel", viejoConsejo.nombre)
                            jsonObjectCon.put("color", viejoConsejo.contenido)
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
                        "http://savetrack.com.mx/backupput4.php?username=$username&backup=$jsonObjectMontoGrupo"
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
                        "http://savetrack.com.mx/backupput5.php?username=$username&backup=$jsonObjectGrupos"
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
                        object : StringRequest(Method.PUT,
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
                        object : StringRequest(Method.PUT,
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
        }

        binding.EditProfileButton.setOnClickListener {
            val edit = perfileditar()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.perfil_container, edit).addToBackStack(null).commit()
        }

        binding.EditProfileButton2.setOnClickListener {
            val edit = perfileditar()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.perfil_container, edit).addToBackStack(null).commit()
        }

        binding.Config.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.perfil_container, Configuracion()).addToBackStack(null).commit()
        }

        suspend fun mostrarDatos() {
            withContext(Dispatchers.IO) {
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

                val totalIngresos = ingresosGastosDao.checkSummaryI()
                val totalGastos = ingresosGastosDao.checkSummaryG()
                val totalisimo = totalIngresos - totalGastos
                val decimalFormat = DecimalFormat("#.##")
                val balance = "${decimalFormat.format(totalisimo)}$"
                val nombre = usuarioDao.checkName()
                val edad = usuarioDao.checkAge()
                val lachamba = usuarioDao.checkChamba()
                val foto = usuarioDao.checkFoto()
                val diasaho = usuarioDao.checkDiasaho()
                usuarioDao.updateBalance(usuarioDao.checkId(), totalisimo)

                var chamba = ""
                val c = String.format("%06d", lachamba).toCharArray()
                if (c[0] == '1') {
                    chamba += "asalariado, "
                }
                if (c[1] == '2') {
                    chamba += "vendedor, "
                }
                if (c[2] == '3') {
                    chamba += "pensionado, "
                }
                if (c[3] == '4') {
                    chamba += "becado, "
                }
                if (c[4] == '5') {
                    chamba += "mantenido, "
                }
                if (c[5] == '6') {
                    chamba += "inversionista, "
                }

                if (chamba.isNotEmpty()) {
                    chamba = chamba.dropLast(2)
                    chamba = chamba.replaceFirstChar { it.uppercaseChar() }
                }

                Log.v("Name", nombre)
                Log.v("Age", edad.toString())
                Log.v("Código de Chamba", lachamba.toString())
                Log.v("Descripción de Chamba", chamba)
                Log.v("Foto ", foto)
                Log.v("Diasaho", diasaho.toString())
                Log.v("Balance", balance.toString())

                binding.UsernameTV.text = nombre
                binding.AgeTV.text = buildString {
                    append(edad.toString())
                    append(" años")
                }
                binding.OcupationTV.text = buildString {
                    append(chamba) // HACER EL CONVERTIDOR SEGÚN EL ÁRBOL ESE
                }
                binding.DaysSavingButton.text = buildString {
                    append("¡")
                    append(diasaho.toString())
                    append(" días ahorrando!")
                }
                binding.BalanceTV.text = buildString {
                    append("Balance: ")
                    append(balance.toString())
                }

                val linkfoto = "http://savetrack.com.mx/images/$nombre.jpg"
                lifecycleScope.launch {
                    bajarfoto(linkfoto)
                }
            }
        }
        lifecycleScope.launch {
            mostrarDatos()
        }

    }
}