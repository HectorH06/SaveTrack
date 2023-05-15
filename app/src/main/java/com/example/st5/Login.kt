package com.example.st5

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentLoginBinding
import com.example.st5.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.nio.charset.Charset

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

        @SuppressLint("SimpleDateFormat")
        fun JSONObject.optDate(name: String): java.util.Date? {
            val dateString = this.optString(name)
            return if (dateString.isNullOrEmpty()) {
                null
            } else {
                SimpleDateFormat("yyyy-MM-dd").parse(dateString)
            }
        }

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
                                    val idReq: StringRequest = object : StringRequest(Method.GET,
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
                                                    val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                                                    val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()
                                                    val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                                                    val montoGrupoDao = Stlite.getInstance(requireContext()).getMontoGrupoDao()
                                                    val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()

                                                    val nuevoUsuario = Usuario(
                                                        iduser = id,
                                                        nombre = username,
                                                        edad = 0,
                                                        chamba = 0,
                                                        foto = "",
                                                        diasaho = 0,
                                                        balance = 0
                                                    )
                                                    val nuevosIG = IngresosGastos(
                                                        iduser = id,
                                                        summaryingresos = 0.0,
                                                        summarygastos = 0.0
                                                    )
                                                    val nuevoMonto = Monto(
                                                        idmonto = 0,
                                                        iduser = id,
                                                        concepto = "",
                                                        valor = 0.0,
                                                        fecha = null,
                                                        frecuencia = 0,
                                                        tipo = "",
                                                        etiqueta = 0
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

                                                    usuarioDao.clean()
                                                    ingresosGastosDao.clean()
                                                    montoDao.clean()
                                                    montoGrupoDao.clean()
                                                    gruposDao.clean()

                                                    usuarioDao.insertUsuario(nuevoUsuario)
                                                    ingresosGastosDao.insertIngresosGastos(nuevosIG)
                                                    montoDao.insertMonto(nuevoMonto)
                                                    montoGrupoDao.insertMontoG(nuevoMontoGrupo)
                                                    gruposDao.insertGrupo(nuevoGrupo)

                                                    val selected = usuarioDao.getUserData()
                                                    Log.v("SELECTED USERS", selected.toString())

                                                    /*
                                                    UPLOADING BACKUP
                                                    */

                                                    // Tabla Usuario
                                                    val jsonObjectUsuario = JSONObject()
                                                    jsonObjectUsuario.put("iduser", nuevoUsuario.iduser)
                                                    jsonObjectUsuario.put("edad", nuevoUsuario.edad)
                                                    jsonObjectUsuario.put("nombre", nuevoUsuario.nombre)
                                                    jsonObjectUsuario.put("chamba", nuevoUsuario.chamba)
                                                    jsonObjectUsuario.put("foto", nuevoUsuario.foto)
                                                    jsonObjectUsuario.put("diasaho", nuevoUsuario.diasaho)
                                                    jsonObjectUsuario.put("balance", nuevoUsuario.balance)

                                                    // Tabla IngresosGastos
                                                    val jsonObjectIngresosGastos = JSONObject()
                                                    jsonObjectIngresosGastos.put("iduser", nuevosIG.iduser)
                                                    jsonObjectIngresosGastos.put("summaryingresos", nuevosIG.summaryingresos)
                                                    jsonObjectIngresosGastos.put("summarygastos", nuevosIG.summarygastos)

                                                    // Tabla Monto
                                                    val jsonObjectMonto = JSONObject()
                                                    jsonObjectMonto.put("idmonto", nuevoMonto.idmonto)
                                                    jsonObjectMonto.put("iduser", nuevoMonto.iduser)
                                                    jsonObjectMonto.put("concepto", nuevoMonto.concepto)
                                                    jsonObjectMonto.put("valor", nuevoMonto.valor)
                                                    jsonObjectMonto.put("fecha", nuevoMonto.fecha)
                                                    jsonObjectMonto.put("frecuencia", nuevoMonto.frecuencia)
                                                    jsonObjectMonto.put("tipo", nuevoMonto.tipo)
                                                    jsonObjectMonto.put("etiqueta", nuevoMonto.etiqueta)

                                                    // Tabla MontoGrupo
                                                    val jsonObjectMontoGrupo = JSONObject()
                                                    jsonObjectMontoGrupo.put("idmonto", nuevoMontoGrupo.idmonto)
                                                    jsonObjectMontoGrupo.put("idgrupo", nuevoMontoGrupo.idgrupo)
                                                    jsonObjectMontoGrupo.put("iduser", nuevoMontoGrupo.iduser)

                                                    // Tabla Grupos
                                                    val jsonObjectGrupos = JSONObject()
                                                    jsonObjectGrupos.put("Id", nuevoGrupo.Id)
                                                    jsonObjectGrupos.put("name", nuevoGrupo.name)
                                                    jsonObjectGrupos.put("description", nuevoGrupo.description)
                                                    jsonObjectGrupos.put("admin", nuevoGrupo.admin)
                                                    jsonObjectGrupos.put("nmembers", nuevoGrupo.nmembers)
                                                    jsonObjectGrupos.put("enlace", nuevoGrupo.enlace)



                                                    Log.v("jsonObjectUsuario", jsonObjectUsuario.toString())

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
                                                                    "API error", "error => $error"
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
                                                                    "API error", "error => $error"
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
                                                                    "API error", "error => $error"
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
                                                                    "API error", "error => $error"
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
                                                                    "API error", "error => $error"
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

                                    val idReq: StringRequest = object : StringRequest(Method.GET,
                                        idurl,
                                        Response.Listener { response ->
                                            val idReq2 = response.toString()
                                            Log.d("API id bnotnull", idReq2)

                                            // Restore
                                            suspend fun extraerDatosBackup(username: String) {
                                                withContext(Dispatchers.IO) {

                                                    val jsonObject1 =
                                                        JSONObject(URL("http://savetrack.com.mx/backupget.php?username=$username").readText())
                                                    val idu: Long = jsonObject1.getLong("iduser")
                                                    val nombre: String = jsonObject1.getString("nombre")
                                                    val edad: Long = jsonObject1.optLong("edad")
                                                    val chamba: Long = jsonObject1.optLong("chamba")
                                                    val foto: String? = jsonObject1.optString("foto")
                                                    val diasaho: Long = jsonObject1.optLong("diasaho")
                                                    val balance: Long = jsonObject1.optLong("balance")
                                                    val usuarioDao = Stlite.getInstance(
                                                        requireContext()
                                                    ).getUsuarioDao()

                                                    val jsonObject2 =
                                                        JSONObject(URL("http://savetrack.com.mx/backupget2.php?username=$username").readText())
                                                    val idus: Long = jsonObject2.getLong("iduser")
                                                    val summaryingresos: Double = jsonObject2.optDouble("summaryingresos")
                                                    val summarygastos: Double = jsonObject2.optDouble("summarygastos")
                                                    val ingresosGastosDao = Stlite.getInstance(
                                                        requireContext()
                                                    ).getIngresosGastosDao()

                                                    val jsonObject3 =
                                                        JSONObject(URL("http://savetrack.com.mx/backupget3.php?username=$username").readText())
                                                    val idmonto: Long = jsonObject3.getLong("idmonto")
                                                    val iduse: Long = jsonObject3.getLong("iduser")
                                                    val concepto: String = jsonObject3.optString("concepto")
                                                    val valor: Double = jsonObject3.optDouble("valor")
                                                    val fecha: String = jsonObject3.optString("fecha")
                                                    val frecuencia: Long = jsonObject3.optLong("frecuencia")
                                                    val tipo: String = jsonObject3.optString("tipo")
                                                    val etiqueta: Long = jsonObject3.optLong("etiqueta")
                                                    val montoDao = Stlite.getInstance(
                                                        requireContext()
                                                    ).getMontoDao()

                                                    val jsonObject4 =
                                                        JSONObject(URL("http://savetrack.com.mx/backupget4.php?username=$username").readText())
                                                    val idmontog: Long = jsonObject4.getLong("idmonto")
                                                    val idg: Long = jsonObject4.getLong("idgrupo")
                                                    val idusemg: Long = jsonObject4.getLong("iduser")
                                                    val montoGrupoDao = Stlite.getInstance(
                                                        requireContext()
                                                    ).getMontoGrupoDao()

                                                    val jsonObject5 =
                                                        JSONObject(URL("http://savetrack.com.mx/backupget5.php?username=$username").readText())
                                                    val idgru: Long = jsonObject5.getLong("Id")
                                                    val nameg: String = jsonObject5.getString("name")
                                                    val description: String = jsonObject5.optString("description")
                                                    val admin: Long = jsonObject5.getLong("admin")
                                                    val nmembers: Long = jsonObject5.optLong("nmembers")
                                                    val enlace: String = jsonObject5.getString("enlace")
                                                    val gruposDao = Stlite.getInstance(
                                                        requireContext()
                                                    ).getGruposDao()

                                                    val nuevoUsuario = Usuario(
                                                        iduser = idu,
                                                        nombre = nombre,
                                                        edad = edad,
                                                        chamba = chamba,
                                                        foto = foto,
                                                        diasaho = diasaho,
                                                        balance = balance
                                                    )

                                                    val nuevosIG = IngresosGastos(
                                                        iduser = idus,
                                                        summaryingresos = summaryingresos,
                                                        summarygastos = summarygastos
                                                    )
                                                    val nuevoMonto = Monto(
                                                        idmonto = idmonto,
                                                        iduser = iduse,
                                                        concepto = concepto,
                                                        valor = valor,
                                                        fecha = fecha,
                                                        frecuencia = frecuencia,
                                                        tipo = tipo,
                                                        etiqueta = etiqueta
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

                                                    usuarioDao.clean()
                                                    ingresosGastosDao.clean()
                                                    montoDao.clean()
                                                    montoGrupoDao.clean()
                                                    gruposDao.clean()

                                                    usuarioDao.insertUsuario(nuevoUsuario)
                                                    ingresosGastosDao.insertIngresosGastos(nuevosIG)
                                                    montoDao.insertMonto(nuevoMonto)
                                                    montoGrupoDao.insertMontoG(nuevoMontoGrupo)
                                                    gruposDao.insertGrupo(nuevoGrupo)

                                                    val selected = usuarioDao.getUserData()
                                                    Log.v("SELECTED USERS", selected.toString())

                                                }
                                            }
                                            lifecycleScope.launch {
                                                extraerDatosBackup(username)
                                            }
                                        },
                                        Response.ErrorListener { error ->
                                            Toast.makeText(
                                                requireContext(), "No se ha podido conectar a la red", Toast.LENGTH_SHORT
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

                        val intent = Intent(activity, Index::class.java)
                        startActivity(intent)
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