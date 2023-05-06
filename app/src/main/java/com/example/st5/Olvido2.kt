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
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.Olvido.Companion.truepin
import com.example.st5.databinding.FragmentOlvido2Binding
import java.nio.charset.Charset

class Olvido2 : Fragment() {
    private lateinit var binding: FragmentOlvido2Binding
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
        binding = FragmentOlvido2Binding.inflate(inflater, container, false)
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

        binding.buttonReestablecer.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            var url = "http://savetrack.com.mx/passupdate.php?"

            val truepinval = truepin.toString()
            val pin = binding.textPin.text.toString()
            val password = binding.RegPass.text.toString()
            val password2 = binding.RegConPass.text.toString()

            if (password == password2 && password != "" && pin == truepinval) {
                if (password.length in 4..18) {
                                Log.d("Pin", truepin.toString())
                                Log.d("password", password)
                                val email = arguments?.getString("Email")
                                val requestBody = "email=$email&newpassword=$password"
                                url += requestBody

                                val stringReq: StringRequest =
                                    object : StringRequest(Method.PUT, url,
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
                                    "Contraseña actualizada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val fragmentlogin = Login()
                                parentFragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                    .replace(R.id.FragContainer, fragmentlogin)
                                    .addToBackStack(null)
                                    .commit()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Contraseña: entre 4 y 18 caracteres",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (binding.textPin.text.isEmpty() || binding.RegPass.text.isEmpty() || binding.RegConPass.text.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "No puede haber campos vacíos",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Las contraseñas no coinciden o el pin es incorrecto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}