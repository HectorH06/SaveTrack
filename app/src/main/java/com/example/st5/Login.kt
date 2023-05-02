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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentLoginBinding
import com.example.st5.models.Usuario
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
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.FragContainer, actual)
                        .addToBackStack(null)
                        .commit()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
                .replace(R.id.FragContainer, fragmentregister)
                .addToBackStack(null)
                .commit()
        }

        binding.buttonIniSes.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://savetrack.com.mx/usrlogin.php"

            val username = binding.editTextTextPersonName.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            if (username != "" && password != "") {
                val checkUserUrl = "$url?username=$username&password=$password"
                Log.e("checkUserUrl", checkUserUrl)
                /*
                CHECK USER REQUEST
                 */
                val checkUserReq = StringRequest(
                    Request.Method.GET, checkUserUrl,
                    { response ->
                        val strResp = response.toString()
                        Log.d("API", strResp)
                        if (response != "exist") {
                            Toast.makeText(
                                requireContext(),
                                "Usuario y/o contraseña incorrectos",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Usuario y contraseña correctos, redirigir a la actividad de perfil
                            Log.e("username", username)
                            Log.e("password", password)

                            // Verificar si existe un json con la base de datos nativa
                                // De no ser así, se crea a partir de los datos insertados y por construir, y luego se sube
                                // Si sí, se extrae e inserta en la room
                            var checkbackupurl = "http://savetrack.com.mx/backuget.php"
                            checkbackupurl += "username=$username"
                            /*
                            CHECK BACKUP REQUEST
                            */
                            val backReq: StringRequest =
                                object : StringRequest(Method.GET, checkbackupurl,
                                    Response.Listener { response ->

                                        var idurl = "http://savetrack.com.mx/idget.php"
                                        idurl += "username=$username"
                                        /*
                                        CHECK ID REQUEST
                                        */
                                        val idReq: StringRequest =
                                            object : StringRequest(Method.GET, idurl,
                                                Response.Listener { response ->
                                                    val id: Long = response.toLong()
                                                    val idReq2 = response.toString()
                                                    Log.d("API", idReq2)
                                                    if (response == null){

                                                        // Create and backup
                                                        val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                                                        val nuevoUsuario = Usuario(iduser = id, nombre = username, edad = null, chamba = null, foto = null, diasaho = 0, balance = 0)
                                                        usuarioDao.insertUsuario(nuevoUsuario)

                                                    } else {

                                                        // Restore

                                                        //CREAR CLASE PARA HACER BACKUP EN CASO DE CERRAR SESIÓN O QUE SE CIERRE SÚBITAMENTE LA APP
                                                        //CREAR MÉTODOS PARA MANTENER LA SESIÓN INICIADA

                                                    }
                                                },
                                                Response.ErrorListener { error ->
                                                    Log.d("API", "error => $error")
                                                }
                                            ) {
                                                override fun getBody(): ByteArray {
                                                    return idurl.toByteArray(Charset.defaultCharset())
                                                }
                                            }
                                        Log.e("idReq", idReq.toString())
                                        queue.add(idReq)
                                    },
                                    Response.ErrorListener { error ->
                                        Log.d("API", "error => $error")
                                    }
                                ) {
                                    override fun getBody(): ByteArray {
                                        return checkbackupurl.toByteArray(Charset.defaultCharset())
                                    }
                                }
                            Log.e("backReq", backReq.toString())
                            queue.add(backReq)

                            Toast.makeText(
                                requireContext(),
                                "Bienvenido, $username",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(activity, Perfil::class.java)
                            startActivity(intent)
                        }
                    },
                    { error ->
                        Log.d("API", "error => $error")
                    }
                )
                Log.e("checkUserReq", checkUserReq.toString())
                queue.add(checkUserReq)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Los campos no pueden estar vacíos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}