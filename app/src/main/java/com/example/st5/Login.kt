package com.example.st5

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.databinding.FragmentLoginBinding
import java.nio.charset.Charset

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
            var url = "http://savetrack.com.mx/usrget.php?"

            var username = binding.editTextTextPersonName.text.toString()
            var password = binding.editTextTextPassword.text.toString()
            Log.e("username", username)
            Log.e("password", password)
            //val jsonObject = JSONObject(params as Map<*, *>?)
            var requestBody = "username=$username&password=$password"
            url = url + requestBody

            val stringReq: StringRequest =
                object : StringRequest(Method.GET, url,
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
        }
    }
}