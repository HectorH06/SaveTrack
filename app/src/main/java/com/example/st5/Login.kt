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
import java.time.LocalDate
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
                                                            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                                                            val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()
                                                            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                                                            val montoGrupoDao = Stlite.getInstance(requireContext()).getMontoGrupoDao()
                                                            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
                                                            val assetsDao = Stlite.getInstance(requireContext()).getAssetsDao()
                                                            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()
                                                            val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()
                                                            val conySugDao = Stlite.getInstance(requireContext()).getConySugDao()

                                                            val nuevoUsuario = Usuario(
                                                                iduser = id,
                                                                nombre = username,
                                                                edad = 0,
                                                                chamba = 0,
                                                                foto = "",
                                                                diasaho = 0,
                                                                balance = 0.0,
                                                                meta = 0.0
                                                            )
                                                            val nuevosIG = IngresosGastos(
                                                                iduser = id,
                                                                summaryingresos = 0.0,
                                                                summarygastos = 0.0
                                                            )
                                                            val alimentos = Labels(
                                                                plabel = "Alimentos",
                                                                color = -149484
                                                            )
                                                            val hogar = Labels(
                                                                plabel = "Hogar",
                                                                color = -12734989
                                                            )
                                                            val bienestar = Labels(
                                                                plabel = "Bienestar",
                                                                color = -12933083
                                                            )
                                                            val necesidades = Labels(
                                                                plabel = "Otras Necesidades",
                                                                color = -15578487
                                                            )
                                                            val gastosh = Labels(
                                                                plabel = "Gastos Hormiga",
                                                                color = -2321882
                                                            )
                                                            val ocio = Labels(
                                                                plabel = "Ocio y demás",
                                                                color = -2345658
                                                            )
                                                            val obsequio = Labels(
                                                                plabel = "Obsequio",
                                                                color = -11075418
                                                            )

                                                            val adddateStr: String = LocalDate.now().toString()
                                                            val adddate = adddateStr.replace("-", "").toInt()

                                                            val navidad = Eventos(
                                                                nombre = "Navidad",
                                                                fecha = 51225,
                                                                frecuencia = 365,
                                                                etiqueta = 7,
                                                                estado = 0,
                                                                adddate = adddate
                                                            )

                                                            val anuevo = Eventos(
                                                                nombre = "Año nuevo",
                                                                fecha = 50101,
                                                                frecuencia = 365,
                                                                etiqueta = 6,
                                                                estado = 0,
                                                                adddate = adddate
                                                            )

                                                            val starter = "Estrena tu cuenta de SaveTrack"
                                                            val cs = hashMapOf(
                                                                "A" to arrayOf(ConySug(), ConySug()),
                                                                "C" to arrayOf(ConySug(), ConySug()),
                                                                "E" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                                                                "I" to arrayOf(ConySug(), ConySug()),
                                                                "L" to arrayOf(ConySug(), ConySug(), ConySug()),
                                                                "M" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                                                                "D" to arrayOf(ConySug()),
                                                                "U" to arrayOf(ConySug(), ConySug(), ConySug())
                                                            )
                                                            cs["A"]?.set(0, ConySug(idcon = 0, nombre = starter, contenido = "Prueba el modo oscuro", estado = 1, flag = 0, type = 1, style = 0))
                                                            cs["A"]?.set(1, ConySug(idcon = 1, nombre = starter, contenido = "Es posible desactivar las notificaciones", estado = 1, flag = 0, type = 1, style = 0))
                                                            cs["C"]?.set(0, ConySug(idcon = 100, nombre = "Se están ignorando los consejos", contenido = "Dales una oportunidad", estado = 0, flag = 1, type = 3, style = 0))
                                                            cs["C"]?.set(1, ConySug(idcon = 101, nombre = "Se están rechazando demasiados consejos", contenido = "Dales una oportunidad", estado = 0, flag = 1, type = 1, style = 0))
                                                            cs["E"]?.set(0, ConySug(idcon = 200, nombre = "Hay pocos eventos", contenido = "Dales una oportunidad", estado = 0, flag = 1, type = 3, style = 0))
                                                            cs["E"]?.set(1, ConySug(idcon = 201, nombre = "Hay demasiados eventos", contenido = "Podrías tener problemas al visualizarlos", estado = 0, flag = 1, type = 4, style = 1))
                                                            cs["E"]?.set(2, ConySug(idcon = 202, nombre = "Pocos eventos tienen notificaciones", contenido = "Prueba activarlos para evitar olvidarlos", estado = 0, flag = 1, type = 7, style = 1))
                                                            cs["E"]?.set(3, ConySug(idcon = 203, nombre = "No se te notificará de eventos", contenido = "Tienes las notificaciones desactivadas", estado = 0, flag = 0, type = 7, style = 1))
                                                            cs["E"]?.set(4, ConySug(idcon = 204, nombre = "No se ha creado un evento en mucho tiempo", contenido = "Prueba a agregar alguno", estado = 0, flag = 2, type = 5, style = 0))
                                                            cs["E"]?.set(5, ConySug(idcon = 205, nombre = starter, contenido = "Prueba a crear tu primer evento", estado = 1, flag = 0, type = 1, style = 0))
                                                            cs["I"]?.set(0, ConySug(idcon = 400, nombre = "Los egresos son similares a los ingresos", contenido = "Hay un margen de menos del 5%", estado = 0, flag = 1, type = 7, style = 2))
                                                            cs["I"]?.set(1, ConySug(idcon = 401, nombre = "Los egresos son superiores a los ingresos", contenido = "Es importante tomar medidas", estado = 0, flag = 1, type = 7, style = 3))
                                                            cs["L"]?.set(0, ConySug(idcon = 500, nombre = "No hay etiquetas", contenido = "Es necesario crear al menos una para insertar montos", estado = 0, flag = 1, type = 5, style = 2))
                                                            cs["L"]?.set(1, ConySug(idcon = 501, nombre = "Hay pocas etiquetas", contenido = "Considera crear más", estado = 0, flag = 0, type = 5, style = 1))
                                                            cs["L"]?.set(2, ConySug(idcon = 502, nombre = "Hay demasiadas etiquetas", contenido = "Considera eliminar algunas", estado = 0, flag = 0, type = 6, style = 1))
                                                            cs["M"]?.set(0, ConySug(idcon = 600, nombre = starter, contenido = "Prueba a registrar tu primer monto", estado = 1, flag = 0, type = 1, style = 0))
                                                            cs["M"]?.set(1, ConySug(idcon = 601, nombre = starter, contenido = "Prueba a registrar un ingreso", estado = 1, flag = 0, type = 1, style = 0))
                                                            cs["M"]?.set(2, ConySug(idcon = 602, nombre = starter, contenido = "Prueba a registrar un egreso", estado = 1, flag = 0, type = 1, style = 0))
                                                            cs["M"]?.set(3, ConySug(idcon = 603, nombre = "Hay pocos montos", contenido = "Considera registrar más", estado = 0, flag = 0, type = 3, style = 1))
                                                            cs["M"]?.set(4, ConySug(idcon = 604, nombre = "Hay demasiados montos", contenido = "Considera reducirlos", estado = 0, flag = 0, type = 2, style = 1))
                                                            cs["D"]?.set(0, ConySug(idcon = 700, nombre = "Demasiados montos son deudas", contenido = "Prueba a reducirlas", estado = 0, flag = 1, type = 4, style = 3))
                                                            cs["U"]?.set(0, ConySug(idcon = 800, nombre = "La meta de ahorro supera los ingresos totales", contenido = "Prueba reducirla", estado = 0, flag = 1, type = 4, style = 1))
                                                            cs["U"]?.set(1, ConySug(idcon = 801, nombre = "La meta de ahorro es menor al 10% de los ingresos totales", contenido = "Prueba ahorrar más incrementándola", estado = 0, flag = 0, type = 3, style = 0))
                                                            cs["U"]?.set(2, ConySug(idcon = 802, nombre = starter, contenido = "Prueba a crear una meta de ahorro", estado = 1, flag = 0, type = 1, style = 0))


                                                            val defaultAssets = Assets(
                                                                theme = 0,
                                                                lastprocess = 0,
                                                                notificaciones = 1
                                                            )

                                                            usuarioDao.clean()
                                                            ingresosGastosDao.clean()
                                                            montoDao.clean()
                                                            montoGrupoDao.clean()
                                                            gruposDao.clean()
                                                            assetsDao.clean()
                                                            labelsDao.clean()
                                                            eventosDao.clean()
                                                            conySugDao.clean()

                                                            usuarioDao.insertUsuario(nuevoUsuario)
                                                            ingresosGastosDao.insertIngresosGastos(nuevosIG)
                                                            labelsDao.insertLabel(alimentos)
                                                            labelsDao.insertLabel(hogar)
                                                            labelsDao.insertLabel(bienestar)
                                                            labelsDao.insertLabel(necesidades)
                                                            labelsDao.insertLabel(gastosh)
                                                            labelsDao.insertLabel(ocio)
                                                            labelsDao.insertLabel(obsequio)
                                                            eventosDao.insertEvento(navidad)
                                                            eventosDao.insertEvento(anuevo)
                                                            for ((_, value) in cs) {
                                                                for (i in value.indices) {
                                                                    conySugDao.insertCon(value[i])
                                                                }
                                                            }
                                                            assetsDao.insertAsset(defaultAssets)

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
                                                            jsonObjectUsuario.put("diasaho", nuevoUsuario.diasaho)
                                                            jsonObjectUsuario.put("balance", nuevoUsuario.balance)

                                                            // Tabla IngresosGastos
                                                            val jsonObjectIngresosGastos = JSONObject()
                                                            jsonObjectIngresosGastos.put("iduser", nuevosIG.iduser)
                                                            jsonObjectIngresosGastos.put("summaryingresos", nuevosIG.summaryingresos)
                                                            jsonObjectIngresosGastos.put("summarygastos", nuevosIG.summarygastos)

                                                            // Tablas de varios items
                                                            val jsonObjectMonto = JSONObject()
                                                            val jsonObjectMontoGrupo = JSONObject()
                                                            val jsonObjectGrupos = JSONObject()
                                                            val jsonObjectLabels = JSONObject()
                                                            val jsonObjectEventos = JSONObject()
                                                            val jsonObjectConySug = JSONObject()

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

                                                            val upload7url =
                                                                "http://savetrack.com.mx/backupput7.php?username=$username&backup=$jsonObjectEventos"
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
                                                                        return idurl.toByteArray(
                                                                            Charset.defaultCharset()
                                                                        )
                                                                    }
                                                                }
                                                            Log.d(
                                                                "uploadReq", upload7Req.toString()
                                                            )
                                                            queue.add(upload7Req)

                                                            val upload8url =
                                                                "http://savetrack.com.mx/backupput8.php?username=$username&backup=$jsonObjectConySug"
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
                                                                        return idurl.toByteArray(
                                                                            Charset.defaultCharset()
                                                                        )
                                                                    }
                                                                }
                                                            Log.d(
                                                                "uploadReq", upload8Req.toString()
                                                            )
                                                            queue.add(upload8Req)

                                                            val url9 =
                                                                "http://savetrack.com.mx/images/inipic.php?username=$username"
                                                            val stringRequest =
                                                                object : StringRequest(
                                                                    Method.POST, url9,
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
                                                            val meta: Double =
                                                                jsonObject1.optDouble("meta")
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
                                                                    val frecuencia: Int =
                                                                        jsonObject3.optInt("frecuencia")
                                                                    val etiqueta: Int =
                                                                        jsonObject3.optInt("etiqueta")
                                                                    val interes: Double =
                                                                        jsonObject3.optDouble("interes")
                                                                    val tipointeres: Int =
                                                                        jsonObject3.optInt("tipointeres")
                                                                    val veces: Long =
                                                                        jsonObject3.optLong("veces")
                                                                    val estado: Int =
                                                                        jsonObject3.optInt("estado")
                                                                    val adddate: Int =
                                                                        jsonObject3.optInt("adddate")
                                                                    val enddate: Int =
                                                                        jsonObject3.optInt("enddate")
                                                                    val cooldown: Int =
                                                                        jsonObject3.optInt("cooldown")
                                                                    val delay: Int =
                                                                        jsonObject3.optInt("delay")
                                                                    val sequence: String =
                                                                        jsonObject3.optString("sequence")

                                                                    val nuevoMonto = Monto(
                                                                        idmonto = idmonto,
                                                                        iduser = iduse,
                                                                        concepto = concepto,
                                                                        valor = valor,
                                                                        valorfinal = valorfinal,
                                                                        fecha = fecha,
                                                                        frecuencia = frecuencia,
                                                                        etiqueta = etiqueta,
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

                                                            val jsonArray4 = JSONArray(URL("http://savetrack.com.mx/backupget4.php?username=$username").readText())
                                                            val montoGrupoDao = Stlite.getInstance(requireContext()).getMontoGrupoDao()
                                                            montoGrupoDao.clean()
                                                            Log.v("jsonArray4", jsonArray4.toString())
                                                            for (i in 0 until jsonArray4.length()) {
                                                                val jsonObject4 = jsonArray4.getJSONObject(i)
                                                                if (jsonObject4.getLong("idmonto") != null) {
                                                                    val idmontog: Long = jsonObject4.getLong("idmonto")
                                                                    val idg: Long = jsonObject4.getLong("idgrupo")
                                                                    val idusemg: Long = jsonObject4.getLong("iduser")

                                                                    val nuevosMG = MontoGrupo(
                                                                        idmonto = idmontog,
                                                                        idgrupo = idg,
                                                                        iduser = idusemg
                                                                    )
                                                                    Log.v(
                                                                        "Current montoGrupo $i",
                                                                        nuevosMG.toString()
                                                                    )

                                                                    montoGrupoDao.insertMontoG(nuevosMG)
                                                                } else {
                                                                    Log.v(
                                                                        "Current montoGrupo $i",
                                                                        "VACÍO"
                                                                    )
                                                                }
                                                            }

                                                            val jsonArray5 = JSONArray(URL("http://savetrack.com.mx/backupget5.php?username=$username").readText())
                                                            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
                                                            gruposDao.clean()
                                                            Log.v("jsonArray6", jsonArray5.toString())
                                                            for (i in 0 until jsonArray5.length()) {
                                                                val jsonObject5 = jsonArray5.getJSONObject(i)
                                                                if (jsonObject5.getLong("Id") != null) {
                                                                    val idgru: Long = jsonObject5.getLong("Id")
                                                                    val nameg: String = jsonObject5.getString("name")
                                                                    val description: String = jsonObject5.optString("description")
                                                                    val type: Int = jsonObject5.getInt("type")
                                                                    val admin: Long = jsonObject5.getLong("admin")
                                                                    val idori: Long = jsonObject5.getLong("idori")
                                                                    val color: Int = jsonObject5.optInt("color")
                                                                    val enlace: Long = jsonObject5.getLong("enlace")

                                                                    val nuevosGrupos = Grupos(
                                                                        Id = idgru,
                                                                        nameg = nameg,
                                                                        description = description,
                                                                        type = type,
                                                                        admin = admin,
                                                                        idori = idori,
                                                                        color = color,
                                                                        enlace = enlace
                                                                    )
                                                                    Log.v(
                                                                        "Current grupo $i",
                                                                        nuevosGrupos.toString()
                                                                    )

                                                                    gruposDao.insertGrupo(nuevosGrupos)
                                                                } else {
                                                                    Log.v(
                                                                        "Current grupo $i",
                                                                        "VACÍO"
                                                                    )
                                                                }
                                                            }

                                                            val jsonArray6 = JSONArray(URL("http://savetrack.com.mx/backupget6.php?username=$username").readText())
                                                            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()
                                                            labelsDao.clean()
                                                            Log.v("jsonArray6", jsonArray6.toString())
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
                                                                    val estado: Int =
                                                                        jsonObject6.optInt("estado")

                                                                    val nuevasLabels = Labels(
                                                                        idlabel = idlabel,
                                                                        plabel = plabel,
                                                                        color = color,
                                                                        estado = estado
                                                                    )
                                                                    Log.v(
                                                                        "Current label $i",
                                                                        nuevasLabels.toString()
                                                                    )

                                                                    labelsDao.insertLabel(
                                                                        nuevasLabels
                                                                    )
                                                                } else {
                                                                    Log.v(
                                                                        "Current label $i",
                                                                        "VACÍO"
                                                                    )
                                                                }
                                                            }

                                                            val jsonArray7 = JSONArray(URL("http://savetrack.com.mx/backupget7.php?username=$username").readText())
                                                            val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()
                                                            eventosDao.clean()
                                                            Log.v("jsonArray7", jsonArray7.toString())
                                                            for (i in 0 until jsonArray7.length()) {
                                                                val jsonObject7 =
                                                                    jsonArray7.getJSONObject(i)
                                                                if (jsonObject7.getLong("idevento") != null) {
                                                                    val idevento: Long = jsonObject7.getLong("idevento")
                                                                    val nombre: String = jsonObject7.getString("nombre")
                                                                    val fecha: Int = jsonObject7.optInt("fecha")
                                                                    val frecuencia: Int = jsonObject7.optInt("frecuencia")
                                                                    val etiqueta: Int = jsonObject7.optInt("etiqueta")
                                                                    val estado: Int = jsonObject7.optInt("estado")
                                                                    val adddate: Int = jsonObject7.optInt("adddate")

                                                                    val nuevosEventos = Eventos(
                                                                        idevento = idevento,
                                                                        nombre = nombre,
                                                                        fecha = fecha,
                                                                        frecuencia = frecuencia,
                                                                        etiqueta = etiqueta,
                                                                        estado = estado,
                                                                        adddate = adddate
                                                                    )
                                                                    Log.v(
                                                                        "Current evento $i",
                                                                        nuevosEventos.toString()
                                                                    )

                                                                    eventosDao.insertEvento(nuevosEventos)
                                                                } else {
                                                                    Log.v(
                                                                        "Current evento $i",
                                                                        "VACÍO"
                                                                    )
                                                                }
                                                            }

                                                            val jsonArray8 = JSONArray(URL("http://savetrack.com.mx/backupget8.php?username=$username").readText())
                                                            val conySugDao = Stlite.getInstance(requireContext()).getConySugDao()
                                                            conySugDao.clean()
                                                            Log.v("jsonArray8", jsonArray8.toString())
                                                            for (i in 0 until jsonArray8.length()) {
                                                                val jsonObject8 =
                                                                    jsonArray8.getJSONObject(i)
                                                                if (jsonObject8.getLong("idcon") != null) {
                                                                    val idcon: Long = jsonObject8.getLong("idcon")
                                                                    val nombre: String = jsonObject8.getString("nombre")
                                                                    val contenido: String = jsonObject8.optString("contenido")
                                                                    val estado: Int = jsonObject8.optInt("estado")
                                                                    val flag: Int = jsonObject8.optInt("flag")
                                                                    val type: Int = jsonObject8.optInt("type")
                                                                    val style: Int = jsonObject8.optInt("style")

                                                                    val nuevosConySug = ConySug(
                                                                        idcon = idcon,
                                                                        nombre = nombre,
                                                                        contenido = contenido,
                                                                        estado = estado,
                                                                        flag = flag,
                                                                        type = type,
                                                                        style = style
                                                                    )
                                                                    Log.v(
                                                                        "Current consejo $i",
                                                                        nuevosConySug.toString()
                                                                    )

                                                                    conySugDao.insertCon(nuevosConySug)
                                                                } else {
                                                                    Log.v(
                                                                        "Current consejo $i",
                                                                        "VACÍO"
                                                                    )
                                                                }
                                                            }

                                                            val assetsDao = Stlite.getInstance(requireContext()).getAssetsDao()

                                                            val nuevoUsuario = Usuario(
                                                                iduser = idu,
                                                                nombre = nombre,
                                                                edad = edad,
                                                                chamba = chamba,
                                                                foto = "",
                                                                diasaho = diasaho,
                                                                balance = balance,
                                                                meta = meta
                                                            )
                                                            val nuevosIG = IngresosGastos(
                                                                iduser = idus,
                                                                summaryingresos = summaryingresos,
                                                                summarygastos = summarygastos
                                                            )
                                                            val defaultAssets = Assets(
                                                                theme = tema,
                                                                lastprocess = lastprocess
                                                            )

                                                            usuarioDao.clean()
                                                            ingresosGastosDao.clean()
                                                            //montoGrupoDao.clean()
                                                            //gruposDao.clean()
                                                            assetsDao.clean()

                                                            usuarioDao.insertUsuario(nuevoUsuario)
                                                            ingresosGastosDao.insertIngresosGastos(nuevosIG)
                                                            //montoGrupoDao.insertMontoG(nuevoMontoGrupo)
                                                            //gruposDao.insertGrupo(nuevoGrupo)
                                                            assetsDao.insertAsset(defaultAssets)

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