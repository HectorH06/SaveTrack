package com.example.st5

import android.annotation.SuppressLint
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
import com.example.st5.databinding.FragmentRegisterBinding
import java.nio.charset.Charset

class Register : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val prev = Login()
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                    .replace(R.id.FragContainer, prev)
                    .addToBackStack(null)
                    .commit()
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonConCuenta.setOnClickListener {
            val fragmentlogin = Login()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.FragContainer, fragmentlogin)
                .addToBackStack(null)
                .commit()
        }

        binding.buttonRegister.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            var url = "http://savetrack.com.mx/usrpost.php?"

            val username = binding.RegCorreo.text.toString()
            val password = binding.RegPass.text.toString()
            val password2 = binding.RegConPass.text.toString()

            if (password == password2 && password != "" && username != "") {
                if (username.length in 6..32 && password.length in 4..18) {
                    val checkUserUrl = "http://savetrack.com.mx/usrget.php?username=$username"

                    val checkUserReq = StringRequest(
                        Request.Method.GET, checkUserUrl,
                        { response ->
                            val strResp = response.toString()
                            Log.d("API", strResp)
                            if (response == "exist") {
                                Toast.makeText(
                                    requireContext(),
                                    "El usuario ya existe",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Usuario no existe, registrar nuevo usuario
                                Log.e("username", username)
                                Log.e("password", password)
                                val requestBody = "username=$username&password=$password"
                                url += requestBody

                                val stringReq: StringRequest =
                                    object : StringRequest(Method.POST, url,
                                        Response.Listener { response ->
                                            // response
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
                                Toast.makeText(
                                    requireContext(),
                                    "Usuario creado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val fragmentlogin = Login()
                                parentFragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                    .replace(R.id.FragContainer, fragmentlogin)
                                    .addToBackStack(null)
                                    .commit()
                            }
                        },
                        { error ->
                            Toast.makeText(
                                requireContext(), "No se ha podido conectar a la red", Toast.LENGTH_SHORT
                            ).show()
                            Log.d("API", "error => $error")
                        }
                    )
                    Log.e("checkUserReq", checkUserReq.toString())
                    queue.add(checkUserReq)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Nombre: entre 6 y 32 caracteres, Cotraseña: entre 4 y 18 caracteres",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (binding.RegCorreo.text.isEmpty() || binding.RegPass.text.isEmpty() || binding.RegConPass.text.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "No puede haber campos vacíos",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Las contraseñas no coinciden",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}