package com.example.st5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.time.LocalDate
import java.util.*


class Alarma : BroadcastReceiver() {
    private val jsonArrayMonto = JSONArray()
    private val jsonArrayLabels = JSONArray()

    override fun onReceive(context: Context?, intent: Intent?) {
        runBlocking {
            if (context != null) {
                procesarMontos(context)
                respaldo(context)
            }
        }
    }

    private suspend fun procesarMontos(context: Context) {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(context).getMontoDao()
            val ingresoGastoDao = Stlite.getInstance(context).getIngresosGastosDao()
            val assetsDao = Stlite.getInstance(context).getAssetsDao()

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

            val montos = montoDao.getMontoXFecha(today, dom, dow, 100, addd)

            if (prev != today) {
                for (monto in montos) {
                    val totalIngresos = ingresoGastoDao.checkSummaryI()

                    Log.i("MONTO PROCESADO", monto.toString())
                    val weekMonto = monto.fecha
                    Log.v("wek", weekMonto.toString())

                    if (monto.etiqueta > 100) {
                        ingresoGastoDao.updateSummaryI(monto.iduser.toInt(), totalIngresos + monto.valor)
                    } else {
                        var status = 0
                        if (monto.estado == 1){
                            status = 0
                        }
                        if (monto.estado == 4){
                            status = 3
                        }
                        if (monto.estado == 6){
                            status = 5
                        }
                        if (monto.estado == 9){
                            status = 8
                        }
                        val toCheckMonto = Monto(
                            idmonto = monto.idmonto,
                            iduser = monto.iduser,
                            concepto = monto.concepto,
                            valor = monto.valor,
                            fecha = monto.fecha,
                            frecuencia = monto.frecuencia,
                            etiqueta = monto.etiqueta,
                            interes = monto.interes,
                            veces = monto.veces,
                            estado = status,
                            adddate = monto.adddate
                        )
                        montoDao.updateMonto(toCheckMonto)
                    }

                    monto.veces = monto.veces?.plus(1)
                    montoDao.updateMonto(monto)
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

            val perocuantosmontos = montoDao.getMaxMonto()
            val perocuantaslabels = labelsDao.getMaxLabel()
            Log.v("CUANTOS MONTOS", perocuantosmontos.toString())
            Log.v("CUANTOS MONTOS", perocuantosmontos.toString())

            val iduser = usuarioDao.checkId().toLong()
            val username = usuarioDao.checkName()
            val edad = usuarioDao.checkAge().toLong()
            val lachamba = usuarioDao.checkChamba().toLong()
            val diasaho = usuarioDao.checkDiasaho().toLong()
            val balance = usuarioDao.checkBalance()
            val foto = usuarioDao.checkFoto()
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
                balance = balance
            )
            val viejosIG = IngresosGastos(
                iduser = iduser, summaryingresos = summaryingresos, summarygastos = summarygastos
            )

            val viejoMontoGrupo = MontoGrupo(
                idmonto = 0,
                idgrupo = 0,
                iduser = iduser,
            )
            val viejoGrupo = Grupos(
                Id = 0, name = "", description = "", admin = iduser, nmembers = 1, enlace = ""
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
                        fechafinal = montoDao.getFechaFinal(idmonto),
                        frecuencia = montoDao.getFrecuencia(idmonto),
                        etiqueta = montoDao.getEtiqueta(idmonto),
                        interes = montoDao.getInteres(idmonto),
                        veces = montoDao.getVeces(idmonto),
                        estado = montoDao.getEstado(idmonto),
                        adddate = montoDao.getAdded(idmonto)
                    )
                    Log.v("Current monto $idmonto", viejoMonto.toString())
                    val jsonObjectMonto = JSONObject()
                    jsonObjectMonto.put("idmonto", viejoMonto.idmonto)
                    jsonObjectMonto.put("iduser", viejoMonto.iduser)
                    jsonObjectMonto.put("concepto", viejoMonto.concepto)
                    jsonObjectMonto.put("valor", viejoMonto.valor)
                    jsonObjectMonto.put("valorfinal", viejoMonto.valorfinal)
                    jsonObjectMonto.put("fecha", viejoMonto.fecha)
                    jsonObjectMonto.put("fechafinal", viejoMonto.fechafinal)
                    jsonObjectMonto.put("frecuencia", viejoMonto.frecuencia)
                    jsonObjectMonto.put("etiqueta", viejoMonto.etiqueta)
                    jsonObjectMonto.put("interes", viejoMonto.interes)
                    jsonObjectMonto.put("veces", viejoMonto.veces)
                    jsonObjectMonto.put("estado", viejoMonto.estado)
                    jsonObjectMonto.put("adddate", viejoMonto.adddate)

                    jsonArrayMonto.put(jsonObjectMonto)

                    Log.v("Current object", jsonObjectMonto.toString())
                    Log.v("Current array", jsonArrayMonto.toString())
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
                Log.v("Current idmonto", idlabel.toString())
                if (labelsDao.getPlabel(idlabel) != null) {
                    val viejaLabel = Labels(
                        idlabel = labelsDao.getIdLabel(idlabel),
                        plabel = labelsDao.getPlabel(idlabel),
                        color = labelsDao.getColor(idlabel)
                    )
                    Log.v("Current monto $idlabel", viejaLabel.toString())
                    val jsonObjectLabels = JSONObject()
                    jsonObjectLabels.put("idlabel", viejaLabel.idlabel)
                    jsonObjectLabels.put("plabel", viejaLabel.plabel)
                    jsonObjectLabels.put("color", viejaLabel.color)

                    jsonArrayLabels.put(jsonObjectLabels)

                    Log.v("Current object", jsonObjectLabels.toString())
                    Log.v("Current array", jsonArrayLabels.toString())
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
                "http://savetrack.com.mx/backupput4.php?username=$username&backup=$jsonObjectMontoGrupo"
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
                "http://savetrack.com.mx/backupput5.php?username=$username&backup=$jsonObjectGrupos"
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

            val selectedafter = usuarioDao.getUserData()
            Log.v(
                "POST SELECTED USERS", selectedafter.toString()
            )
        }
    }
}