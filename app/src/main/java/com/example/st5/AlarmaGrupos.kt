package com.example.st5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.st5.database.Stlite
import com.example.st5.models.Grupos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class AlarmaGrupos : BroadcastReceiver() {
    private var notifActive = 0

    private lateinit var notificationHelper: notificationManager
    private lateinit var decoder: Decoder
    override fun onReceive(context: Context?, intent: Intent?) {
        decoder = (context?.let { Decoder(it) } ?: return)
        runBlocking {
            if (context != null && decoder.hayNet()) {
                procesarGrupos(context)
            }
        }
    }

    private suspend fun procesarGrupos(context: Context) {
        withContext(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                val gruposDao = Stlite.getInstance(context).getGruposDao()
                val grupos = gruposDao.getAllGrupos()

                for ((Id, nameg, description, type, admin, idori, color, enlace) in grupos) {
                    if (Id != null && nameg != null) {
                        val viejoGrupo = Grupos(
                            Id = Id,
                            nameg = nameg,
                            description = description,
                            type = type,
                            admin = admin,
                            idori = idori,
                            color = color,
                            enlace = enlace
                        )

                        val grupoJson = withContext(Dispatchers.IO) { JSONObject(URL("http://savetrack.com.mx/grupoGet.php?localid=${viejoGrupo.idori}&admin=${viejoGrupo.admin}").readText()) }

                        if (grupoJson.getLong("idgrupoglobal") != null) {
                            val idoriA: Long = grupoJson.getLong("idgrupolocal")
                            val adminA: Long = grupoJson.getLong("idadmin")
                            val namegA: String = grupoJson.optString("nombre")
                            val descA: String = grupoJson.optString("descripcion")
                            val tipoA: Int = grupoJson.optInt("tipo")
                            val colorA: Int = grupoJson.optInt("color")

                            val grupoActualizado = Grupos(
                                Id = Id,
                                nameg = namegA,
                                description = descA,
                                type = tipoA,
                                admin = adminA,
                                idori = idoriA,
                                color = colorA,
                                enlace = enlace
                            )

                            gruposDao.updateGrupo(grupoActualizado)
                        } else {
                            Log.v("Current grupo", "VACÍO")
                        }
                        val grupos = gruposDao.getAllGrupos()
                        Log.i("ALL GRUPOS ALARMA", grupos.toString())
                    }
                }
            }
        }
    }
}