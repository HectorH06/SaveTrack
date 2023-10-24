package com.example.st5

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.models.Grupos
import com.example.st5.models.Labels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.nio.charset.Charset

class getGrupo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupos)

        val intentData: Uri? = intent.data
        if (intentData != null) {
            val localid = intentData.getQueryParameter("zxcd125s5d765e7wqa87sdftgh")
            val admin = intentData.getQueryParameter("mnhjkmnbg1yhb3vdrtgvc98swe")

            lifecycleScope.launch {
                try {
                    val grupoJson = withContext(Dispatchers.IO) { JSONObject(URL("http://savetrack.com.mx/grupoGet.php?localid=$localid&admin=$admin").readText()) }
                    val miembrosJSON = withContext(Dispatchers.IO) { JSONArray(URL("http://savetrack.com.mx/gruposMiembrosGet.php?localid=$localid&admin=$admin").readText()) }
                    val miembrosG = Array(miembrosJSON.length()) { miembrosJSON.getInt(it) }

                    if (grupoJson.getLong("idgrupoglobal") != null && grupoJson.getInt("tipo") != 2) {
                        val idori: Long = grupoJson.getLong("idgrupolocal")
                        val idadmin: Long = grupoJson.getLong("idadmin")
                        val nombre: String = grupoJson.optString("nombre")
                        val desc: String = grupoJson.optString("descripcion")
                        val tipo: Int = grupoJson.optInt("tipo")
                        val color: Int = grupoJson.optInt("color")
                        val enlace: Long = grupoJson.optLong("enlace")

                        grupoAdd(nombre, desc, tipo, color, idadmin, idori, enlace, miembrosG)
                    } else {
                        Log.v("Current grupo", "VACÍO")
                    }
                } catch (e: Exception) {
                    Log.e("NetworkError", "Error during network call", e)
                }
            }
        }
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(this@getGrupo)
            val intent = Intent(this@getGrupo, GruposActivity::class.java)
            intent.putExtra("isDarkMode", isDarkMode)
            startActivity(intent)
        }
        
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
    
    private suspend fun grupoAdd(
        nombre: String,
        descripcion: String,
        type: Int,
        color: Int,
        admin: Long,
        idori: Long,
        enlace: Long,
        miembros: Array<Int>
    ) {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(this@getGrupo).getGruposDao()
            val usuarioDao = Stlite.getInstance(this@getGrupo).getUsuarioDao()
            val labelsDao = Stlite.getInstance(this@getGrupo).getLabelsDao()

            val iduser = usuarioDao.checkId()
            if (!miembros.contains(iduser)) {
                val nuevosMiembros: MutableList<Int> = mutableListOf()
                for (element in miembros) {
                    nuevosMiembros.add(element)
                }

                val nuevoGrupo = Grupos(
                    nameg = nombre,
                    description = descripcion,
                    type = type,
                    admin = admin,
                    idori = idori,
                    color = color,
                    enlace = enlace
                )
                gruposDao.insertGrupo(nuevoGrupo)

                val nuevaLabel = Labels(
                    plabel = nombre,
                    color = color
                )
                labelsDao.insertLabel(nuevaLabel)

                val idlabel = labelsDao.getMaxLabel().toLong()
                val gId = gruposDao.getMaxGrupo().toLong()
                val grupoUp = Grupos(
                    Id = gId,
                    nameg = nombre,
                    description = descripcion,
                    type = type,
                    admin = admin,
                    idori = idori,
                    color = color,
                    enlace = idlabel
                )
                gruposDao.updateGrupo(grupoUp)

                val queue = Volley.newRequestQueue(this@getGrupo)
                var url = "http://savetrack.com.mx/gruposMiembrosPut.php?"
                nuevosMiembros.add(iduser)
                val uniqueMiembros = nuevosMiembros.toSet().toList()
                val jsonMiembros = JSONArray(uniqueMiembros)

                Log.v("JSONMIEMBROS", "$jsonMiembros")
                Log.v("MUTABLELISTMIEMBROS", "$nuevosMiembros")
                val requestBody = "localid=$idori&admin=$admin&miembros=$jsonMiembros"
                url += requestBody
                val stringReq: StringRequest =
                    object : StringRequest(
                        Method.PUT, url,
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
            } else {
                Toast.makeText(this@getGrupo, "Ya formas parte del grupo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
