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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentLoginBinding
import com.example.st5.models.Usuario
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
                                                    val usuarioDao = Stlite.getInstance(
                                                        requireContext()
                                                    ).getUsuarioDao()
                                                    val nuevoUsuario = Usuario(
                                                        iduser = id,
                                                        nombre = username,
                                                        edad = 0,
                                                        chamba = 0,
                                                        foto = "",
                                                        diasaho = 0,
                                                        balance = 0
                                                    )
                                                    usuarioDao.clean()
                                                    usuarioDao.insertUsuario(
                                                        nuevoUsuario
                                                    )

                                                    val selected = usuarioDao.getUserData()
                                                    Log.v(
                                                        "SELECTED USERS", selected.toString()
                                                    )

                                                    /*
                                                    UPLOADING BACKUP
                                                    */

                                                    val jsonObjectUsuario = JSONObject()
                                                    jsonObjectUsuario.put(
                                                        "iduser", nuevoUsuario.iduser
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "edad", nuevoUsuario.edad
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "nombre", nuevoUsuario.nombre
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "chamba", nuevoUsuario.chamba
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "foto", nuevoUsuario.foto
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "diasaho", nuevoUsuario.diasaho
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "balance", nuevoUsuario.balance
                                                    )

                                                    Log.d(
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
                                                    val jsonObject =
                                                        JSONObject(URL("http://savetrack.com.mx/backupget.php?username=$username").readText())
                                                    val idu: Long = jsonObject.getLong("iduser")
                                                    val nombre: String = jsonObject.getString("nombre")
                                                    val edad: Long = jsonObject.optLong("edad")
                                                    val chamba: Long = jsonObject.optLong("chamba")
                                                    val foto: String? = jsonObject.optString("foto")
                                                    val diasaho: Long = jsonObject.optLong("diasaho")
                                                    val balance: Long = jsonObject.optLong("balance")
                                                    val usuarioDao = Stlite.getInstance(
                                                        requireContext()
                                                    ).getUsuarioDao()

                                                    val nuevoUsuario = Usuario(
                                                        iduser = idu,
                                                        nombre = nombre,
                                                        edad = edad,
                                                        chamba = chamba,
                                                        foto = foto,
                                                        diasaho = diasaho,
                                                        balance = balance
                                                    )
                                                    usuarioDao.clean()
                                                    usuarioDao.insertUsuario(
                                                        nuevoUsuario
                                                    )

                                                    val selected = usuarioDao.getUserData()
                                                    Log.v(
                                                        "SELECTED USERS", selected.toString()
                                                    )

                                                    /*
                                                    UPLOADING BACKUP
                                                    */

                                                    val jsonObjectUsuario = JSONObject()
                                                    jsonObjectUsuario.put(
                                                        "iduser", nuevoUsuario.iduser
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "edad", nuevoUsuario.edad
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "nombre", nuevoUsuario.nombre
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "chamba", nuevoUsuario.chamba
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "foto", nuevoUsuario.foto
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "diasaho", nuevoUsuario.diasaho
                                                    )
                                                    jsonObjectUsuario.put(
                                                        "balance", nuevoUsuario.balance
                                                    )

                                                    Log.d(
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