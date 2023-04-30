package com.example.st5

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.databinding.FragmentLoginBinding

class Login : Fragment() {
    private lateinit var binding: FragmentLoginBinding

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
            val fragment_register = Register()
            parentFragmentManager.beginTransaction()
                .replace(R.id.FragContainer, fragment_register)
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