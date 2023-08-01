package com.example.st5

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentLoginBinding
import com.example.st5.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.nio.charset.Charset
import java.util.*

class Login : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val actual = Login()
                    parentFragmentManager.beginTransaction().replace(R.id.FragContainer, actual)
                        .addToBackStack(null).commit()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSinCuenta.setOnClickListener {
            val fragmentregister = Register()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.FragContainer, fragmentregister).addToBackStack(null).commit()
        }

        binding.buttonOlviContra.setOnClickListener {
            val fragmentRecupeContra = Olvido()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.FragContainer, fragmentRecupeContra).addToBackStack(null).commit()
        }

        binding.buttonIniSes.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://savetrack.com.mx/usrlogin.php"

            val username = binding.editTextTextPersonName.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            if (username != "" && password != "") {
                val checkUserUrl = "$url?username=$username&password=$password"
                Log.d("checkUserUrl", checkUserUrl)
                /*
                CHECK USER REQUEST
                 */
                val checkUserReq = StringRequest(Request.Method.GET, checkUserUrl, { response ->
                    lifecycleScope.launch {
                        val strResp = response.toString()
                        Log.d("API checkuser", strResp)
                        if (response != "exist") {
                            Toast.makeText(
                                requireContext(),
                                "Usuario y/o contraseña incorrectos",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Usuario y contraseña correctos, redirigir a la actividad de perfil
                            Log.v("username", username)
                            Log.v("password", password)

                            // Verificar si existe un json con la base de datos nativa
                            // De no ser así, se crea a partir de los datos insertados y por construir, y luego se sube
                            // Si sí, se extrae e inserta en la room
                            var checkbackupurl = "http://savetrack.com.mx/backuget.php"
                            checkbackupurl += "?username=$username"
                            /*
                            CHECK BACKUP REQUEST
                            */
                            val backReq: StringRequest = object : StringRequest(Method.GET,
                                checkbackupurl,
                                Response.Listener { response ->
                                    val backReq2 = response.toString()
                                    Log.d("API backupreq", backReq2)
                                    var idurl = "http://savetrack.com.mx/idget.php"
                                    idurl += "?username=$username"
                                    /*
                                    CHECK ID REQUEST
                                    */
                                    if (response.toString() == "null") {
                                        // CHECK ID AND CREATE BACKUP
                                        val idReq: StringRequest =
                                            object : StringRequest(Method.GET,
                                                idurl,
                                                Response.Listener { response ->
                                                    val id: Long = response.toLong()
                                                    val idReq2 = response.toString()
                                                    Log.d("API id bnull", idReq2)

                                                    // CREATING BACKUP
                                                    suspend fun insertarNuevoUsuario(
                                                        id: Long, username: String
                                                    ) {
                                                        withContext(Dispatchers.IO) {
                                                            val usuarioDao =
                                                                Stlite.getInstance(requireContext())
                                                                    .getUsuarioDao()
                                                            val ingresosGastosDao =
                                                                Stlite.getInstance(requireContext())
                                                                    .getIngresosGastosDao()
                                                            val montoDao =
                                                                Stlite.getInstance(requireContext())
                                                                    .getMontoDao()
                                                            val montoGrupoDao =
                                                                Stlite.getInstance(requireContext())
                                                                    .getMontoGrupoDao()
                                                            val gruposDao =
                                                                Stlite.getInstance(requireContext())
                                                                    .getGruposDao()
                                                            val assetsDao =
                                                                Stlite.getInstance(requireContext())
                                                                    .getAssetsDao()
                                                            val labelsDao =
                                                                Stlite.getInstance(requireContext())
                                                                    .getLabelsDao()

                                                            val nuevoUsuario = Usuario(
                                                                iduser = id,
                                                                nombre = username,
                                                                edad = 0,
                                                                chamba = 0,
                                                                foto = "",
                                                                diasaho = 0,
                                                                balance = 0.0
                                                            )
                                                            val nuevosIG = IngresosGastos(
                                                                iduser = id,
                                                                summaryingresos = 0.0,
                                                                summarygastos = 0.0
                                                            )
                                                            val nuevoMontoGrupo = MontoGrupo(
                                                                idmonto = 0,
                                                                idgrupo = 0,
                                                                iduser = id,
                                                            )
                                                            val nuevoGrupo = Grupos(
                                                                Id = 0,
                                                                name = "",
                                                                description = "",
                                                                admin = id,
                                                                nmembers = 1,
                                                                enlace = ""
                                                            )
                                                            val alimentos = Labels(
                                                                plabel = "Alimentos",
                                                                color = 0xFDB813
                                                            )
                                                            val hogar = Labels(
                                                                plabel = "Hogar",
                                                                color = 0x3DADF2
                                                            )
                                                            val bienestar = Labels(
                                                                plabel = "Bienestar",
                                                                color = 0x3AA824
                                                            )
                                                            val necesidades = Labels(
                                                                plabel = "Otras Necesidades",
                                                                color = 0x124A88
                                                            )
                                                            val gastosh = Labels(
                                                                plabel = "Gastos Hormiga",
                                                                color = 0xDC9225
                                                            )
                                                            val ocio = Labels(
                                                                plabel = "Ocio y demás",
                                                                color = 0xDC3545
                                                            )
                                                            val obsequio = Labels(
                                                                plabel = "Obsequio",
                                                                color = 0x5700A5
                                                            )
                                                            val deuda = Labels(
                                                                plabel = "Deuda",
                                                                color = 0xA80B1A
                                                            )

                                                            val defaultAssets = Assets(
                                                                theme = 0,
                                                                lastprocess = 0
                                                            )

                                                            usuarioDao.clean()
                                                            ingresosGastosDao.clean()
                                                            montoDao.clean()
                                                            montoGrupoDao.clean()
                                                            gruposDao.clean()
                                                            assetsDao.clean()
                                                            labelsDao.clean()

                                                            usuarioDao.insertUsuario(nuevoUsuario)
                                                            ingresosGastosDao.insertIngresosGastos(nuevosIG)
                                                            montoGrupoDao.insertMontoG(nuevoMontoGrupo)
                                                            gruposDao.insertGrupo(nuevoGrupo)
                                                            labelsDao.insertLabel(alimentos)
                                                            labelsDao.insertLabel(hogar)
                                                            labelsDao.insertLabel(bienestar)
                                                            labelsDao.insertLabel(necesidades)
                                                            labelsDao.insertLabel(gastosh)
                                                            labelsDao.insertLabel(ocio)
                                                            labelsDao.insertLabel(obsequio)
                                                            labelsDao.insertLabel(deuda)
                                                            assetsDao.insertAsset(defaultAssets)

                                                            val selected = usuarioDao.getUserData()
                                                            Log.v(
                                                                "SELECTED USERS",
                                                                selected.toString()
                                                            )

                                                            /*
                                                            UPLOADING BACKUP
                                                            */

                                                            // Tabla Usuario
                                                            val jsonObjectUsuario = JSONObject()
                                                            jsonObjectUsuario.put(
                                                                "iduser",
                                                                nuevoUsuario.iduser
                                                            )
                                                            jsonObjectUsuario.put(
                                                                "edad",
                                                                nuevoUsuario.edad
                                                            )
                                                            jsonObjectUsuario.put(
                                                                "nombre",
                                                                nuevoUsuario.nombre
                                                            )
                                                            jsonObjectUsuario.put(
                                                                "chamba",
                                                                nuevoUsuario.chamba
                                                            )
                                                            jsonObjectUsuario.put(
                                                                "diasaho",
                                                                nuevoUsuario.diasaho
                                                            )
                                                            jsonObjectUsuario.put(
                                                                "balance",
                                                                nuevoUsuario.balance
                                                            )

                                                            // Tabla IngresosGastos
                                                            val jsonObjectIngresosGastos =
                                                                JSONObject()
                                                            jsonObjectIngresosGastos.put(
                                                                "iduser",
                                                                nuevosIG.iduser
                                                            )
                                                            jsonObjectIngresosGastos.put(
                                                                "summaryingresos",
                                                                nuevosIG.summaryingresos
                                                            )
                                                            jsonObjectIngresosGastos.put(
                                                                "summarygastos",
                                                                nuevosIG.summarygastos
                                                            )

                                                            // Tabla Monto
                                                            val jsonObjectMonto = JSONObject()

                                                            // Tabla MontoGrupo
                                                            val jsonObjectMontoGrupo = JSONObject()
                                                            jsonObjectMontoGrupo.put(
                                                                "idmonto",
                                                                nuevoMontoGrupo.idmonto
                                                            )
                                                            jsonObjectMontoGrupo.put(
                                                                "idgrupo",
                                                                nuevoMontoGrupo.idgrupo
                                                            )
                                                            jsonObjectMontoGrupo.put(
                                                                "iduser",
                                                                nuevoMontoGrupo.iduser
                                                            )

                                                            // Tabla Grupos
                                                            val jsonObjectGrupos = JSONObject()
                                                            jsonObjectGrupos.put(
                                                                "Id",
                                                                nuevoGrupo.Id
                                                            )
                                                            jsonObjectGrupos.put(
                                                                "name",
                                                                nuevoGrupo.name
                                                            )
                                                            jsonObjectGrupos.put(
                                                                "description",
                                                                nuevoGrupo.description
                                                            )
                                                            jsonObjectGrupos.put(
                                                                "admin",
                                                                nuevoGrupo.admin
                                                            )
                                                            jsonObjectGrupos.put(
                                                                "nmembers",
                                                                nuevoGrupo.nmembers
                                                            )
                                                            jsonObjectGrupos.put(
                                                                "enlace",
                                                                nuevoGrupo.enlace
                                                            )

                                                            // Tabla Labels
                                                            val jsonObjectLabels = JSONObject()

                                                            Log.v(
                                                                "jsonObjectUsuario",
                                                                jsonObjectUsuario.toString()
                                                            )

                                                            val uploadurl =
                                                                "http://savetrack.com.mx/backupput.php?username=$username&backup=$jsonObjectUsuario"
                                                            val uploadReq: StringRequest =
                                                                object : StringRequest(Method.PUT,
                                                                    uploadurl,
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
                                                                        return idurl.toByteArray(
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
                                                                object : StringRequest(Method.PUT,
                                                                    upload2url,
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
                                                                        return idurl.toByteArray(
                                                                            Charset.defaultCharset()
                                                                        )
                                                                    }
                                                                }
                                                            Log.d(
                                                                "uploadReq", upload2Req.toString()
                                                            )
                                                            queue.add(upload2Req)

                                                            val upload3url =
                                                                "http://savetrack.com.mx/backupput3.php?username=$username&backup=$jsonObjectMonto"
                                                            val upload3Req: StringRequest =
                                                                object : StringRequest(Method.PUT,
                                                                    upload3url,
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
                                                                        return idurl.toByteArray(
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
                                                                object : StringRequest(Method.PUT,
                                                                    upload4url,
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
                                                                        return idurl.toByteArray(
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
                                                                object : StringRequest(Method.PUT,
                                                                    upload5url,
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
                                                                        return idurl.toByteArray(
                                                                            Charset.defaultCharset()
                                                                        )
                                                                    }
                                                                }
                                                            Log.d(
                                                                "uploadReq", upload5Req.toString()
                                                            )
                                                            queue.add(upload5Req)

                                                            val upload6url =
                                                                "http://savetrack.com.mx/backupput6.php?username=$username&backup=$jsonObjectLabels"
                                                            val upload6Req: StringRequest =
                                                                object : StringRequest(Method.PUT,
                                                                    upload6url,
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
                                                                        return idurl.toByteArray(
                                                                            Charset.defaultCharset()
                                                                        )
                                                                    }
                                                                }
                                                            Log.d(
                                                                "uploadReq", upload6Req.toString()
                                                            )
                                                            queue.add(upload6Req)

                                                            val url7 =
                                                                "http://savetrack.com.mx/images/inipic.php?username=$username"
                                                            val stringRequest =
                                                                object : StringRequest(
                                                                    Method.POST, url7,
                                                                    Response.Listener { response ->
                                                                        try {
                                                                            Log.d(
                                                                                "UPLOAD SUCCESS",
                                                                                response
                                                                            )
                                                                        } catch (e: JSONException) {
                                                                            e.printStackTrace()
                                                                        }
                                                                    },
                                                                    Response.ErrorListener { error ->
                                                                        Log.e(
                                                                            "UPLOAD API ERROR",
                                                                            error.toString()
                                                                        )
                                                                        Toast.makeText(
                                                                            requireContext(),
                                                                            "No se ha podido establecer conexión a Internet",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                    }) {
                                                                    override fun getBody(): ByteArray {
                                                                        return idurl.toByteArray(
                                                                            Charset.defaultCharset()
                                                                        )
                                                                    }
                                                                }

                                                            val socketTimeout = 5000
                                                            val policy: RetryPolicy =
                                                                DefaultRetryPolicy(
                                                                    socketTimeout,
                                                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                                                )
                                                            stringRequest.retryPolicy = policy
                                                            val requestQueue =
                                                                Volley.newRequestQueue(
                                                                    requireContext()
                                                                )
                                                            requestQueue.add(stringRequest)

                                                        }
                                                    }
                                                    lifecycleScope.launch {
                                                        insertarNuevoUsuario(id, username)
                                                    }
                                                },
                                                Response.ErrorListener { error ->
                                                    Log.e("API error", "error => $error")
                                                }) {
                                                override fun getBody(): ByteArray {
                                                    return idurl.toByteArray(Charset.defaultCharset())
                                                }
                                            }
                                        Log.d("idReq", idReq.toString())
                                        queue.add(idReq)

                                        // CHECK ID AND RESTORE
                                    } else {

                                        val idReq: StringRequest =
                                            object : StringRequest(Method.GET,
                                                idurl,
                                                Response.Listener { response ->
                                                    val idReq2 = response.toString()
                                                    Log.d("API id bnotnull", idReq2)

                                                    // Restore
                                                    suspend fun extraerDatosBackup(username: String) {
                                                        withContext(Dispatchers.IO) {

                                                            val jsonObject1 =
                                                                JSONObject(URL("http://savetrack.com.mx/backupget.php?username=$username").readText())
                                                            val idu: Long =
                                                                jsonObject1.getLong("iduser")
                                                            val nombre: String =
                                                                jsonObject1.getString("nombre")
                                                            val edad: Long =
                                                                jsonObject1.optLong("edad")
                                                            val chamba: Long =
                                                                jsonObject1.optLong("chamba")
                                                            val diasaho: Long =
                                                                jsonObject1.optLong("diasaho")
                                                            val balance: Double =
                                                                jsonObject1.optDouble("balance")
                                                            val tema: Long =
                                                                jsonObject1.optLong("theme")
                                                            val lastprocess: Int =
                                                                jsonObject1.optInt("lastprocess")
                                                            val usuarioDao = Stlite.getInstance(
                                                                requireContext()
                                                            ).getUsuarioDao()

                                                            val jsonObject2 =
                                                                JSONObject(URL("http://savetrack.com.mx/backupget2.php?username=$username").readText())
                                                            val idus: Long =
                                                                jsonObject2.getLong("iduser")
                                                            val summaryingresos: Double =
                                                                jsonObject2.optDouble("summaryingresos")
                                                            val summarygastos: Double =
                                                                jsonObject2.optDouble("summarygastos")
                                                            val ingresosGastosDao =
                                                                Stlite.getInstance(
                                                                    requireContext()
                                                                ).getIngresosGastosDao()

                                                            val jsonArray3 =
                                                                JSONArray(URL("http://savetrack.com.mx/backupget3.php?username=$username").readText())
                                                            val montoDao =
                                                                Stlite.getInstance(requireContext())
                                                                    .getMontoDao()
                                                            montoDao.clean()
                                                            Log.v(
                                                                "jsonArray3",
                                                                jsonArray3.toString()
                                                            )
                                                            for (i in 0 until jsonArray3.length()) {
                                                                val jsonObject3 =
                                                                    jsonArray3.getJSONObject(i)
                                                                if (jsonObject3.getLong("idmonto") != null) {
                                                                    val idmonto: Long =
                                                                        jsonObject3.getLong("idmonto")
                                                                    val iduse: Long =
                                                                        jsonObject3.getLong("iduser")
                                                                    val concepto: String =
                                                                        jsonObject3.optString("concepto")
                                                                    val valor: Double =
                                                                        jsonObject3.optDouble("valor")
                                                                    val valorfinal: Double =
                                                                        jsonObject3.optDouble("valorfinal")
                                                                    val fecha: Int =
                                                                        jsonObject3.optInt("fecha")
                                                                    val fechafinal: Int =
                                                                        jsonObject3.optInt("fechafinal")
                                                                    val frecuencia: Int =
                                                                        jsonObject3.optInt("frecuencia")
                                                                    val etiqueta: Int =
                                                                        jsonObject3.optInt("etiqueta")
                                                                    val interes: Double =
                                                                        jsonObject3.optDouble("interes")
                                                                    val veces: Long =
                                                                        jsonObject3.optLong("veces")
                                                                    val estado: Int =
                                                                        jsonObject3.optInt("estado")
                                                                    val adddate: Int =
                                                                        jsonObject3.optInt("adddate")

                                                                    val nuevoMonto = Monto(
                                                                        idmonto = idmonto,
                                                                        iduser = iduse,
                                                                        concepto = concepto,
                                                                        valor = valor,
                                                                        valorfinal = valorfinal,
                                                                        fecha = fecha,
                                                                        fechafinal = fechafinal,
                                                                        frecuencia = frecuencia,
                                                                        etiqueta = etiqueta,
                                                                        interes = interes,
                                                                        veces = veces,
                                                                        estado = estado,
                                                                        adddate = adddate
                                                                    )
                                                                    Log.v(
                                                                        "Current monto $i",
                                                                        nuevoMonto.toString()
                                                                    )

                                                                    montoDao.insertMonto(nuevoMonto)
                                                                } else {
                                                                    Log.v(
                                                                        "Current monto $i",
                                                                        "VACÍO"
                                                                    )
                                                                }
                                                            }

                                                            val jsonObject4 =
                                                                JSONObject(URL("http://savetrack.com.mx/backupget4.php?username=$username").readText())
                                                            val idmontog: Long =
                                                                jsonObject4.getLong("idmonto")
                                                            val idg: Long =
                                                                jsonObject4.getLong("idgrupo")
                                                            val idusemg: Long =
                                                                jsonObject4.getLong("iduser")
                                                            val montoGrupoDao = Stlite.getInstance(
                                                                requireContext()
                                                            ).getMontoGrupoDao()

                                                            val jsonObject5 =
                                                                JSONObject(URL("http://savetrack.com.mx/backupget5.php?username=$username").readText())
                                                            val idgru: Long =
                                                                jsonObject5.getLong("Id")
                                                            val nameg: String =
                                                                jsonObject5.getString("name")
                                                            val description: String =
                                                                jsonObject5.optString("description")
                                                            val admin: Long =
                                                                jsonObject5.getLong("admin")
                                                            val nmembers: Long =
                                                                jsonObject5.optLong("nmembers")
                                                            val enlace: String =
                                                                jsonObject5.getString("enlace")
                                                            val gruposDao = Stlite.getInstance(
                                                                requireContext()
                                                            ).getGruposDao()

                                                            val jsonArray6 =
                                                                JSONArray(URL("http://savetrack.com.mx/backupget6.php?username=$username").readText())
                                                            val labelsDao =
                                                                Stlite.getInstance(requireContext())
                                                                    .getLabelsDao()
                                                            labelsDao.clean()
                                                            Log.v(
                                                                "jsonArray6",
                                                                jsonArray6.toString()
                                                            )
                                                            for (i in 0 until jsonArray6.length()) {
                                                                val jsonObject6 =
                                                                    jsonArray6.getJSONObject(i)
                                                                if (jsonObject6.getLong("idlabel") != null) {
                                                                    val idlabel: Long =
                                                                        jsonObject6.getLong("idlabel")
                                                                    val plabel: String =
                                                                        jsonObject6.getString("plabel")
                                                                    val color: Int =
                                                                        jsonObject6.optInt("color")

                                                                    val nuevasLabels = Labels(
                                                                        idlabel = idlabel,
                                                                        plabel = plabel,
                                                                        color = color,
                                                                    )
                                                                    Log.v(
                                                                        "Current monto $i",
                                                                        nuevasLabels.toString()
                                                                    )

                                                                    labelsDao.insertLabel(
                                                                        nuevasLabels
                                                                    )
                                                                } else {
                                                                    Log.v(
                                                                        "Current monto $i",
                                                                        "VACÍO"
                                                                    )
                                                                }
                                                            }

                                                            val assetsDao =
                                                                Stlite.getInstance(requireContext())
                                                                    .getAssetsDao()

                                                            val nuevoUsuario = Usuario(
                                                                iduser = idu,
                                                                nombre = nombre,
                                                                edad = edad,
                                                                chamba = chamba,
                                                                foto = "",
                                                                diasaho = diasaho,
                                                                balance = balance
                                                            )

                                                            val nuevosIG = IngresosGastos(
                                                                iduser = idus,
                                                                summaryingresos = summaryingresos,
                                                                summarygastos = summarygastos
                                                            )

                                                            val nuevoMontoGrupo = MontoGrupo(
                                                                idmonto = idmontog,
                                                                idgrupo = idg,
                                                                iduser = idusemg,
                                                            )
                                                            val nuevoGrupo = Grupos(
                                                                Id = idgru,
                                                                name = nameg,
                                                                description = description,
                                                                admin = admin,
                                                                nmembers = nmembers,
                                                                enlace = enlace
                                                            )
                                                            val defaultAssets = Assets(
                                                                theme = tema,
                                                                lastprocess = lastprocess
                                                            )

                                                            usuarioDao.clean()
                                                            ingresosGastosDao.clean()
                                                            montoGrupoDao.clean()
                                                            gruposDao.clean()
                                                            assetsDao.clean()

                                                            usuarioDao.insertUsuario(nuevoUsuario)
                                                            ingresosGastosDao.insertIngresosGastos(
                                                                nuevosIG
                                                            )
                                                            montoGrupoDao.insertMontoG(
                                                                nuevoMontoGrupo
                                                            )
                                                            gruposDao.insertGrupo(nuevoGrupo)
                                                            assetsDao.insertAsset(defaultAssets)

                                                            val selected = usuarioDao.getUserData()
                                                            Log.v(
                                                                "SELECTED USERS",
                                                                selected.toString()
                                                            )

                                                        }
                                                    }
                                                    lifecycleScope.launch {
                                                        extraerDatosBackup(username)
                                                    }
                                                },
                                                Response.ErrorListener { error ->
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "No se ha podido conectar a la red",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    Log.e("API id", "error => $error")
                                                }) {
                                                override fun getBody(): ByteArray {
                                                    return idurl.toByteArray(Charset.defaultCharset())
                                                }
                                            }
                                        Log.d("idReq", idReq.toString())
                                        queue.add(idReq)
                                    }
                                },
                                Response.ErrorListener { error ->
                                    Log.e("API backupreq", "error => $error")
                                }) {
                                override fun getBody(): ByteArray {
                                    return checkbackupurl.toByteArray(Charset.defaultCharset())
                                }
                            }
                            Log.d("backReq", backReq.toString())
                            queue.add(backReq)

                            Toast.makeText(
                                requireContext(), "Bienvenido, $username", Toast.LENGTH_SHORT
                            ).show()

                            delay(3000)
                            val intent = Intent(activity, Index::class.java)
                            startActivity(intent)
                        }
                    }
                }, { error ->
                    Toast.makeText(
                        requireContext(), "No se ha podido conectar a la red", Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API", "error => $error")
                })
                Log.d("checkUserReq", checkUserReq.toString())
                queue.add(checkUserReq)
            } else {
                Toast.makeText(
                    requireContext(), "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}