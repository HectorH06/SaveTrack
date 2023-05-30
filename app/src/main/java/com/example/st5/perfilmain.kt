package com.example.st5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPerfilmainBinding
import com.example.st5.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.Charset

class perfilmain : Fragment() {
    private lateinit var binding: FragmentPerfilmainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilmainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        suspend fun bajarfoto(link: String) {
            withContext(Dispatchers.IO) {
                binding.ProfilePicture.load(link) {
                    crossfade(true)
                    placeholder(R.drawable.ic_person)
                    transformations(CircleCropTransformation())
                    scale(Scale.FILL)
                }
            }
        }

        binding.cerrarsesionperfilmainbtn.setOnClickListener {

            suspend fun cerrarSesion() {
                val queue = Volley.newRequestQueue(requireContext())
                withContext(Dispatchers.IO) {
                    val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                    val ingresosGastosDao =
                        Stlite.getInstance(requireContext()).getIngresosGastosDao()
                    val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
                    val montoGrupoDao = Stlite.getInstance(requireContext()).getMontoGrupoDao()
                    val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()



                    val iduser = usuarioDao.checkId().toLong()
                    val username = usuarioDao.checkName()
                    val edad = usuarioDao.checkAge().toLong()
                    val lachamba = usuarioDao.checkChamba().toLong()
                    val diasaho = usuarioDao.checkDiasaho().toLong()
                    val balance = usuarioDao.checkBalance().toLong()
                    val foto = usuarioDao.checkFoto()
                    val summaryingresos = ingresosGastosDao.checkSummaryI()
                    val summarygastos = ingresosGastosDao.checkSummaryG()


                    val viejoUsuario = Usuario(
                        iduser = iduser,
                        nombre = username,
                        edad = edad,
                        chamba = lachamba,
                        foto = foto,
                        diasaho = diasaho,
                        balance = balance
                    )
                    val viejosIG = IngresosGastos(
                        iduser = iduser,
                        summaryingresos = summaryingresos,
                        summarygastos = summarygastos
                    )
                    val viejoMonto = Monto(
                        idmonto = 0,
                        iduser = iduser,
                        concepto = "",
                        valor = 0.0,
                        fecha = "",
                        frecuencia = null,
                        etiqueta = 0,
                        interes = 0.0
                    )
                    val viejoMontoGrupo = MontoGrupo(
                        idmonto = 0,
                        idgrupo = 0,
                        iduser = iduser,
                    )
                    val viejoGrupo = Grupos(
                        Id = 0,
                        name = "",
                        description = "",
                        admin = iduser,
                        nmembers = 1,
                        enlace = ""
                    )


                    val selectedbefore = usuarioDao.getUserData()
                    Log.v("PRE SELECTED USERS", selectedbefore.toString())

                    /*
                    UPLOADING BACKUP
                    */

                    // Tabla Usuario
                    val jsonObjectUsuario = JSONObject()
                    jsonObjectUsuario.put("iduser", viejoUsuario.iduser)
                    jsonObjectUsuario.put("edad", viejoUsuario.edad)
                    jsonObjectUsuario.put("nombre", viejoUsuario.nombre)
                    jsonObjectUsuario.put("chamba", viejoUsuario.chamba)
                    jsonObjectUsuario.put("foto", viejoUsuario.foto)
                    jsonObjectUsuario.put("diasaho", viejoUsuario.diasaho)
                    jsonObjectUsuario.put("balance", viejoUsuario.balance)

                    // Tabla IngresosGastos
                    val jsonObjectIngresosGastos = JSONObject()
                    jsonObjectIngresosGastos.put("iduser", viejosIG.iduser)
                    jsonObjectIngresosGastos.put("summaryingresos", viejosIG.summaryingresos)
                    jsonObjectIngresosGastos.put("summarygastos", viejosIG.summarygastos)

                    // Tabla Monto
                    // TODO meterlo en un jsonobjectarray
                    val jsonObjectMonto = JSONObject()
                    jsonObjectMonto.put("idmonto", viejoMonto.idmonto)
                    jsonObjectMonto.put("iduser", viejoMonto.iduser)
                    jsonObjectMonto.put("concepto", viejoMonto.concepto)
                    jsonObjectMonto.put("valor", viejoMonto.valor)
                    jsonObjectMonto.put("fecha", viejoMonto.fecha)
                    jsonObjectMonto.put("frecuencia", viejoMonto.frecuencia)
                    jsonObjectMonto.put("etiqueta", viejoMonto.etiqueta)
                    jsonObjectMonto.put("interes", viejoMonto.interes)

                    // Tabla MontoGrupo
                    val jsonObjectMontoGrupo = JSONObject()
                    jsonObjectMontoGrupo.put("idmonto", viejoMontoGrupo.idmonto)
                    jsonObjectMontoGrupo.put("idgrupo", viejoMontoGrupo.idgrupo)
                    jsonObjectMontoGrupo.put("iduser", viejoMontoGrupo.iduser)

                    // Tabla Grupos
                    val jsonObjectGrupos = JSONObject()
                    jsonObjectGrupos.put("Id", viejoGrupo.Id)
                    jsonObjectGrupos.put("name", viejoGrupo.name)
                    jsonObjectGrupos.put("description", viejoGrupo.description)
                    jsonObjectGrupos.put("admin", viejoGrupo.admin)
                    jsonObjectGrupos.put("nmembers", viejoGrupo.nmembers)
                    jsonObjectGrupos.put("enlace", viejoGrupo.enlace)



                    Log.v("jsonObjectUsuario", jsonObjectUsuario.toString())

                    val uploadurl =
                        "http://savetrack.com.mx/backupput.php?username=$username&backup=$jsonObjectUsuario"
                    val uploadReq: StringRequest =
                        object : StringRequest(
                            Method.PUT,
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

                    val upload2url =
                        "http://savetrack.com.mx/backupput2.php?username=$username&backup=$jsonObjectIngresosGastos"
                    val upload2Req: StringRequest =
                        object : StringRequest(
                            Method.PUT,
                            upload2url,
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
                                return upload2url.toByteArray(
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
                        object : StringRequest(
                            Method.PUT,
                            upload3url,
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
                                return upload3url.toByteArray(
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
                        object : StringRequest(
                            Method.PUT,
                            upload4url,
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
                                return upload4url.toByteArray(
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
                        object : StringRequest(
                            Method.PUT,
                            upload5url,
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
                                return upload5url.toByteArray(
                                    Charset.defaultCharset()
                                )
                            }
                        }
                    Log.d(
                        "uploadReq", upload5Req.toString()
                    )
                    queue.add(upload5Req)



                    usuarioDao.clean()
                    ingresosGastosDao.clean()
                    montoDao.clean()
                    montoGrupoDao.clean()
                    gruposDao.clean()

                    val selectedafter = usuarioDao.getUserData()
                    Log.v(
                        "POST SELECTED USERS", selectedafter.toString()
                    )
                }
            }

            lifecycleScope.launch {
                cerrarSesion()
            }
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.EditProfileButton.setOnClickListener {
            val edit = perfileditar()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.perfil_container, edit).addToBackStack(null).commit()
        }

        suspend fun mostrarDatos() {
            withContext(Dispatchers.IO) {
                val usuarioDao = Stlite.getInstance(
                    requireContext()
                ).getUsuarioDao()


                val nombre = usuarioDao.checkName()
                val edad = usuarioDao.checkAge()
                val lachamba = usuarioDao.checkChamba()
                val foto = usuarioDao.checkFoto()
                val diasaho = usuarioDao.checkDiasaho()
                val balance = usuarioDao.checkBalance()

                var chamba = ""
                val c = String.format("%06d", lachamba).toCharArray()
                if (c[0] == '1') {
                    chamba += "asalariado "
                }
                if (c[1] == '2') {
                    chamba += "vendedor "
                }
                if (c[2] == '3') {
                    chamba += "pensionado "
                }
                if (c[3] == '4') {
                    chamba += "becado "
                }
                if (c[4] == '5') {
                    chamba += "mantenido "
                }
                if (c[5] == '6') {
                    chamba += "inversionista "
                }

                chamba = chamba.replaceFirstChar { it.uppercaseChar() }

                Log.v("Name", nombre)
                Log.v("Age", edad.toString())
                Log.v("Código de Chamba", lachamba.toString())
                Log.v("Descripción de Chamba", chamba)
                Log.v("Foto ", foto)
                Log.v("Diasaho", diasaho.toString())
                Log.v("Balance", balance.toString())

                binding.UsernameTV.text = nombre
                binding.AgeTV.text = buildString {
                    append(edad.toString())
                    append(" años")
                }
                binding.OcupationTV.text = buildString {
                    append(chamba) // HACER EL CONVERTIDOR SEGÚN EL ÁRBOL ESE
                }
                binding.DaysSavingButton.text = buildString {
                    append("¡")
                    append(diasaho.toString())
                    append(" días ahorrando!")
                }
                binding.BalanceTV.text = buildString {
                    append("Balance: ")
                    append(balance.toString())
                }

                val linkfoto = "http://savetrack.com.mx/images/$nombre.jpg"
                lifecycleScope.launch {
                    bajarfoto(linkfoto)
                }
            }
        }
        lifecycleScope.launch {
            mostrarDatos()
        }

    }
}