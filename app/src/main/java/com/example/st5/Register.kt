package com.example.st5

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.databinding.FragmentRegisterBinding
import java.nio.charset.Charset

class Register : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

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
            val fragment_login = Login()
            parentFragmentManager.beginTransaction()
                .replace(R.id.FragContainer, fragment_login)
                .addToBackStack(null)
                .commit()
        }

        binding.buttonRegister.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            var url = "http://savetrack.com.mx/usrpost.php?"

            var username = binding.RegCorreo.text.toString()
            var password = binding.RegPass.text.toString()
            var password2 = binding.RegConPass.text.toString()

            if (password == password2 && password != "" && username != "" )
            {
                Log.e("username", username)
                Log.e("password", password)
                //val jsonObject = JSONObject(params as Map<*, *>?)
                var requestBody = "username=$username&password=$password"
                url = url + requestBody

                val stringReq: StringRequest =
                    object : StringRequest(Method.POST, url,
                        Response.Listener { response ->
                            // response
                            var strResp = response.toString()
                            Log.d("API", strResp)

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
            } else {
                if (binding.RegCorreo.text.isEmpty() || binding.RegPass.text.isEmpty() || binding.RegConPass.text.isEmpty()) {
                    Toast.makeText(requireContext(), "No puede haber campos vacíos", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}