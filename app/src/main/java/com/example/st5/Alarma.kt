package com.example.st5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class Alarma : BroadcastReceiver() {
    private val jsonArrayMonto = JSONArray()
    private val jsonArrayMontoGrupo = JSONArray()
    private val jsonArrayGrupos = JSONArray()
    private val jsonArrayLabels = JSONArray()
    private val jsonArrayEventos = JSONArray()
    private val jsonArrayConySug = JSONArray()

    private val currencyData = mutableMapOf<String, MutableList<Float>>()
    private val currencies = arrayOf("USD")
    private var notifActive = 0

    private lateinit var notificationHelper: notificationManager
    private lateinit var decoder: Decoder
    override fun onReceive(context: Context?, intent: Intent?) {
        runBlocking {
            if (context != null) {
                procesarMontos(context)
                respaldo(context)
                getDivisas(context)
            }
        }
    }

    private fun callDollarNotif(dolarA: Float, dolarH: Float) {
        Log.v("DOLARESHOY", "AYER: $dolarA, HOY: $dolarH")

        val dif = dolarA - dolarH
        val mindif = 0.05
        val percent = (dif / dolarA) * 100
        if (dif >= mindif && notifActive != 0) {
            notificationHelper.sendNotification(
                "General",
                R.drawable.logo1,
                "El dólar subió",
                "Subió $dif ($percent)",
                0,
                0,
                0
            )
        }
        if (dif <=  mindif && notifActive != 0) {
            notificationHelper.sendNotification(
                "General",
                R.drawable.logo1,
                "El dólar bajó",
                "Bajó $dif ($percent%)",
                0,
                0,
                0
            )
        }
    }

    private suspend fun procesarMontos(context: Context) {
        withContext(Dispatchers.IO) {
            delay(5000)
            val usuarioDao = Stlite.getInstance(context).getUsuarioDao()
            val montoDao = Stlite.getInstance(context).getMontoDao()
            val ingresoGastoDao = Stlite.getInstance(context).getIngresosGastosDao()
            val assetsDao = Stlite.getInstance(context).getAssetsDao()

            notificationHelper = notificationManager(context)
            notifActive = assetsDao.getNotif()
            decoder = Decoder(context)
            val fechaActual = LocalDate.now().toString()
            val today: Int = fechaActual.replace("-", "").toInt()
            val prev = assetsDao.getLastProcess()

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val truefecha = formatoFecha.parse(fechaActual)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dom = calendar.get(Calendar.DAY_OF_MONTH)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = 100
            when (w) {
                1 -> dow = 47
                2 -> dow = 41
                3 -> dow = 42
                4 -> dow = 43
                5 -> dow = 44
                6 -> dow = 45
                7 -> dow = 46
            }

            val addd: Int = today

            Log.i("DOM", dom.toString())
            Log.i("DOW", dow.toString())

            Log.i("todayyyy", today.toString())
            Log.i("prevvvvv", prev.toString())

            if (ingresoGastoDao.checkSummaryI() - ingresoGastoDao.checkSummaryG() < usuarioDao.checkMeta() && assetsDao.getNotif() != 0) {
                usuarioDao.updateDiasaho(usuarioDao.checkId(), 0L)
                notificationHelper.sendNotification(
                    "General",
                    R.drawable.logo1,
                    "No tienes lana we",
                    "Tienes ${decoder.format(usuarioDao.checkBalance())} pesos",
                    0,
                    0L,
                    0
                )
            } else {
                usuarioDao.updateDiasaho(usuarioDao.checkId(), usuarioDao.checkDiasaho() + 1L)
            }


            val montos = montoDao.getMontoXFecha(today, dom, dow, 100, addd)

            if (prev < today) {
                for (monto in montos) {
                    if (monto.cooldown == 0) {
                        val totalIngresos = ingresoGastoDao.checkSummaryI()

                        Log.i("MONTO PROCESADO", monto.toString())
                        val weekMonto = monto.fecha
                        Log.v("wek", weekMonto.toString())

                        if (monto.etiqueta > 10000) {
                            ingresoGastoDao.updateSummaryI(
                                monto.iduser.toInt(),
                                totalIngresos + monto.valor
                            )
                            monto.veces = monto.veces?.plus(1)
                            monto.sequence = monto.sequence + "1."
                            montoDao.updateMonto(monto)
                        } else {
                            var status = 0
                            var cooldown = 0
                            val sequence = monto.sequence + "0."
                            when (monto.estado) {
                                1 -> status = 0
                                4 -> status = 3
                                6 -> status = 5
                                9 -> status = 8
                            }
                            when (monto.frecuencia) {
                                14 -> cooldown = 1
                                61 -> cooldown = 1
                                91 -> cooldown = 2
                                122 -> cooldown = 3
                                183 -> cooldown = 5
                                365 -> cooldown = 11
                            }
                            val toCheckMonto = Monto(
                                idmonto = monto.idmonto,
                                iduser = monto.iduser,
                                concepto = monto.concepto,
                                valor = monto.valor,
                                valorfinal = monto.valorfinal,
                                fecha = monto.fecha,
                                frecuencia = monto.frecuencia,
                                etiqueta = monto.etiqueta,
                                interes = monto.interes,
                                tipointeres = monto.tipointeres,
                                veces = monto.veces,
                                estado = status,
                                adddate = monto.adddate,
                                enddate = monto.enddate,
                                cooldown = cooldown,
                                delay = monto.delay,
                                sequence = sequence
                            )
                            montoDao.updateMonto(toCheckMonto)
                        }
                    } else {
                        val newcool = monto.cooldown + 1

                        val toMeltMonto = Monto(
                            idmonto = monto.idmonto,
                            iduser = monto.iduser,
                            concepto = monto.concepto,
                            valor = monto.valor,
                            valorfinal = monto.valorfinal,
                            fecha = monto.fecha,
                            frecuencia = monto.frecuencia,
                            etiqueta = monto.etiqueta,
                            interes = monto.interes,
                            tipointeres = monto.tipointeres,
                            veces = monto.veces,
                            estado = monto.estado,
                            adddate = monto.adddate,
                            enddate = monto.enddate,
                            cooldown = newcool,
                            delay = monto.delay,
                            sequence = monto.sequence
                        )
                        montoDao.updateMonto(toMeltMonto)
                    }
                }
            }
            assetsDao.updateLastprocess(today)
        }
    }

    private suspend fun respaldo(context: Context) {
        val queue = Volley.newRequestQueue(context)
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(context).getUsuarioDao()
            val ingresosGastosDao = Stlite.getInstance(context).getIngresosGastosDao()
            val montoDao = Stlite.getInstance(context).getMontoDao()
            val montoGrupoDao = Stlite.getInstance(context).getMontoGrupoDao()
            val gruposDao = Stlite.getInstance(context).getGruposDao()
            val labelsDao = Stlite.getInstance(context).getLabelsDao()
            val assetsDao = Stlite.getInstance(context).getAssetsDao()
            val eventosDao = Stlite.getInstance(context).getEventosDao()
            val conySugDao = Stlite.getInstance(context).getConySugDao()

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
                iduser = iduser, summaryingresos = summaryingresos, summarygastos = summarygastos
            )
            val viejosAssets = Assets(
                idtheme = 0,
                theme = tema,
                lastprocess = lastprocess,
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
                val concept = montoDao.getConcepto(idmonto)

                if (concept != null) {
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
                }
            }

            // Tabla MontoGrupo
            for (idmonto in 0..perocuantosmontosg) {
                if (montoGrupoDao.getIdMonto(idmonto) != null && montoGrupoDao.getIdGrupo(idmonto) != null){
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
                if (gruposDao.getIdGrupo(Id) != null && gruposDao.getNameG(Id) != null){
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
                Log.v("Current idmonto", idlabel.toString())
                if (labelsDao.getPlabel(idlabel) != null) {
                    val viejaLabel = Labels(
                        idlabel = labelsDao.getIdLabel(idlabel),
                        plabel = labelsDao.getPlabel(idlabel),
                        color = labelsDao.getColor(idlabel),
                        estado = labelsDao.getEstado(idlabel)
                    )
                    Log.v("Current monto $idlabel", viejaLabel.toString())
                    val jsonObjectLabels = JSONObject()
                    jsonObjectLabels.put("idlabel", viejaLabel.idlabel)
                    jsonObjectLabels.put("plabel", viejaLabel.plabel)
                    jsonObjectLabels.put("color", viejaLabel.color)
                    jsonObjectLabels.put("estado", viejaLabel.estado)

                    jsonArrayLabels.put(jsonObjectLabels)

                    Log.v("Current object", jsonObjectLabels.toString())
                    Log.v("Current array", jsonArrayLabels.toString())
                }
            }

            for (idevento in 1..perocuantoseventos) {
                if (eventosDao.getIdevento(idevento) != null && eventosDao.getNombre(idevento) != null) {
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
                object : StringRequest(Method.PUT, uploadurl, Response.Listener { response ->
                    Log.d(
                        "response", response
                    )
                }, Response.ErrorListener { error ->
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
                object : StringRequest(Method.PUT, upload2url, Response.Listener { response ->
                    Log.d(
                        "response", response
                    )
                }, Response.ErrorListener { error ->
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
                object : StringRequest(Method.PUT, upload3url, Response.Listener { response ->
                    Log.d(
                        "response", response
                    )
                }, Response.ErrorListener { error ->
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
                object : StringRequest(Method.PUT, upload4url, Response.Listener { response ->
                    Log.d(
                        "response", response
                    )
                }, Response.ErrorListener { error ->
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
                object : StringRequest(Method.PUT, upload5url, Response.Listener { response ->
                    Log.d(
                        "response", response
                    )
                }, Response.ErrorListener { error ->
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

            val selectedafter = usuarioDao.getUserData()
            Log.v(
                "POST SELECTED USERS", selectedafter.toString()
            )
        }
    }

    private fun getDivisas(context: Context) {
        val baseUrl = "http://savetrack.com.mx/divisas.php?basecurrency=MXN"

        val today = LocalDate.now()
        val ago = today.minusDays(2)
        val daysInRange = ChronoUnit.DAYS.between(ago, today)
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val queue: RequestQueue = Volley.newRequestQueue(context)
        for (i in 0..daysInRange) {
            val date = ago.plusDays(i)
            val formattedDate = date.format(dateFormat)
            val apiUrl = "$baseUrl&date=$formattedDate&currencies=${currencies.joinToString(",")}"

            val checkDollar = StringRequest(
                Request.Method.GET, apiUrl,
                { response ->
                    Log.v("RES", response)
                    try {
                        val responseData = JSONObject(response)
                        Log.v("Try", "$responseData")
                        for (currency in currencies) {
                            val value = 1 / responseData.optDouble(currency)
                            Log.v("VALUES", "$value")
                            if (!currencyData.containsKey(currency)) {
                                currencyData[currency] = mutableListOf()
                            }
                            currencyData[currency]?.add(value.toFloat())
                            Log.v("currencyData", "$currencyData")
                            Log.v("currencyCURRENT", "$currency")
                            Log.v("currencyUSD", "${currencyData[currency]}")
                            if ((currencyData[currency]?.size ?: 0) > 1) {
                                callDollarNotif(
                                    currencyData[currency]?.get(0) ?: 0F,
                                    currencyData[currency]?.get(1) ?: 0F
                                )
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                { error ->
                    Toast.makeText(
                        context,
                        "No se ha podido conectar al valor del dólar hoy",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("error => $error", "SIE API ERROR")
                }
            )
            queue.add(checkDollar)
        }
    }
}