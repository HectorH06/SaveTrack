package com.example.st5

import android.annotation.SuppressLint
import android.app.Activity
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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.databinding.FragmentOlvidoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*
import javax.mail.internet.*

class Olvido : Fragment() {
    private lateinit var binding: FragmentOlvidoBinding
    companion object {
        var truepin = (1000..9999).random()
    }
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
        binding = FragmentOlvidoBinding.inflate(inflater, container, false)
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

        suspend fun correoo(strEmail: String, strUsername: String){
            withContext(Dispatchers.IO){
                val props = Properties()
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.starttls.enable"] = "true"
                props["mail.smtp.host"] = "smtp.ionos.mx"
                props["mail.smtp.port"] = "587"

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("pingenerator@savetrack.com.mx", "c#zeWjRbk42uZG#")
                    }
                })

                try {
                    val message = MimeMessage(session)
                    message.setFrom(InternetAddress("pingenerator@savetrack.com.mx"))
                    message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(strEmail)
                    )
                    message.subject = "Confirmación de cambio de contraseña"
                    message.setText("Tu pin de confirmación es $truepin")

                    Transport.send(message)

                    (context as Activity).runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Se ha enviado un pin de confirmación, revise su Correo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    val fragmentrecupe = Olvido2()
                    val bundle = Bundle()
                    bundle.putString("Username", strUsername)
                    fragmentrecupe.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.FragContainer, fragmentrecupe)
                        .addToBackStack(null)
                        .commit()

                } catch (e: MessagingException) {
                    throw RuntimeException(e)
                }
            }
        }

        binding.buttonOlviContra.setOnClickListener {
            lifecycleScope.launch {
            val queue = Volley.newRequestQueue(requireContext())

            val username = binding.Usuario.text.toString()
            Log.d("Username", username)
            if (username != "") {
                    val checkUserUrl = "http://savetrack.com.mx/usrget.php?username=$username"
                    val checkUserReq = StringRequest(
                        Request.Method.GET, checkUserUrl,
                        { response ->
                            val strResp = response.toString()
                            Log.d("API", strResp)
                            if (response == "exist") {
                                val checkEmailUrl = "http://savetrack.com.mx/emailget.php?username=$username"
                                val checkEmailReq = StringRequest(
                                    Request.Method.GET, checkEmailUrl,
                                    { response2 ->
                                        Log.d("Pin", truepin.toString())
                                        val strEmail = response2.toString()
                                        Log.d("Email", strEmail)

                                        // Enviar correo electrónico al usuario con su pin de confirmación
                                        lifecycleScope.launch {
                                            correoo(strEmail, username)
                                        }
                                    },
                                    { error ->
                                        Toast.makeText(
                                            requireContext(), "No se ha podido conectar a la red", Toast.LENGTH_SHORT
                                        ).show()
                                        Log.e("API error", "error => $error")
                                    }
                                )
                                Log.d("checkEmailReq", checkEmailReq.toString())
                                queue.add(checkEmailReq)
                            } else {
                                // Usuario no existe
                                Toast.makeText(
                                    requireContext(),
                                    "El usuario ingresado no existe",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        { error ->
                            Toast.makeText(
                                requireContext(), "No se ha podido conectar a la red", Toast.LENGTH_SHORT
                            ).show()
                            Log.e("API error", "error => $error")
                        }
                    )
                    Log.d("checkUserReq", checkUserReq.toString())
                    queue.add(checkUserReq)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "El campo no puede estar vacío",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}