package com.example.st5

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.buttonIniSes.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://savetrack.com.mx/queries/conexion.php"

            val email = binding.editTextTextPersonName.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            val params = HashMap<String, String>()
            params["correo"] = email
            params["contrasenia"] = password

            val stringRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    if (response.contains("Not found any rows")) {
                        binding.textviewIniSes.text = "Correo y/o contraseña incorrectos"
                    } else {
                        binding.textviewIniSes.text = "Inicio de sesión exitoso"
                    }
                },
                {
                    binding.textviewIniSes.text = "Error de conexión"
                })
            queue.add(stringRequest)
        }
    }
}