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

            val fechaActual = LocalDate.now()
            val today = fechaActual.toString()
            val prev = assetsDao.getLastProcess()

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val truefecha = formatoFecha.parse(today)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dm = calendar.get(Calendar.DAY_OF_MONTH)
            val dom = String.format("%02d", dm)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = "Diario"
            when (w) {
                1 -> dow = "Sunday"
                2 -> dow = "Monday"
                3 -> dow = "Tuesday"
                4 -> dow = "Wednesday"
                5 -> dow = "Thursday"
                6 -> dow = "Friday"
                7 -> dow = "Saturday"
            }

            val addd: Int = today.replace("-", "").toInt()

            Log.i("DOM", dom)
            Log.i("DOW", dow)

            Log.i("todayyyy", today)
            Log.i("prevvvvv", prev)

            val montos = montoDao.getMontoXFecha(today, dom, dow, "Diario", addd)

            if (prev != today) {
                for (monto in montos) {
                    val totalIngresos = ingresoGastoDao.checkSummaryI()

                    Log.i("MONTO PROCESADO", monto.toString())
                    val weekMonto = monto.fecha.uppercase()
                    Log.v("wek", weekMonto)

                    if (monto.valor > 0) {
                        ingresoGastoDao.updateSummaryI(
                            monto.iduser.toInt(), totalIngresos + monto.valor
                        )
                    } else {
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
                            estado = 0,
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

            // Tabla IngresosGastos
            val jsonObjectIngresosGastos = JSONObject()
            jsonObjectIngresosGastos.put("iduser", viejosIG.iduser)
            jsonObjectIngresosGastos.put("summaryingresos", viejosIG.summaryingresos)
            jsonObjectIngresosGastos.put("summarygastos", viejosIG.summarygastos)

            // Tabla Monto
            for (idmonto in 1..perocuantosmontos) {
                Log.v("Current idmonto", idmonto.toString())
                val viejoMonto = Monto(
                    idmonto = montoDao.getIdmonto(idmonto),
                    iduser = montoDao.getIduser(idmonto),
                    concepto = montoDao.getConcepto(idmonto),
                    valor = montoDao.getValor(idmonto),
                    fecha = montoDao.getFecha(idmonto),
                    frecuencia = montoDao.getFrecuencia(idmonto),
                    etiqueta = montoDao.getEtiqueta(idmonto),
                    interes = montoDao.getInteres(idmonto),
                    veces = montoDao.getVeces(idmonto),
                    adddate = montoDao.getAdded(idmonto)
                )
                Log.v("Current monto $idmonto", viejoMonto.toString())
                val jsonObjectMonto = JSONObject()
                jsonObjectMonto.put("idmonto", viejoMonto.idmonto)
                jsonObjectMonto.put("iduser", viejoMonto.iduser)
                jsonObjectMonto.put("concepto", viejoMonto.concepto)
                jsonObjectMonto.put("valor", viejoMonto.valor)
                jsonObjectMonto.put("fecha", viejoMonto.fecha)
                jsonObjectMonto.put("frecuencia", viejoMonto.frecuencia)
                jsonObjectMonto.put("etiqueta", viejoMonto.etiqueta)
                jsonObjectMonto.put("interes", viejoMonto.interes)
                jsonObjectMonto.put("veces", viejoMonto.veces)
                jsonObjectMonto.put("adddate", viejoMonto.adddate)

                jsonArrayMonto.put(jsonObjectMonto)

                Log.v("Current object", jsonObjectMonto.toString())
                Log.v("Current array", jsonArrayMonto.toString())
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