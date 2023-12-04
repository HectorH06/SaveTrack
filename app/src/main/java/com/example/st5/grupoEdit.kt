package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentEditgrupoBinding
import com.example.st5.models.Grupos
import com.example.st5.models.Labels
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.ByteArrayOutputStream
import java.net.URL
import java.nio.charset.Charset
import java.util.*


class grupoEdit : Fragment() {
    private var color: Int = 0xffffff
    private var idori: Long = 0
    private var admin: Long = 0
    private var iduser: Long = 0
    private var enlace: Long = 0
    private var ngrupo: String = ""

    private lateinit var binding: FragmentEditgrupoBinding

    companion object {
        private const val idv = "idg"
        fun sendGrupo(
            idg: Long
        ): grupoEdit {
            val fragment = grupoEdit()
            val args = Bundle()
            args.putLong(idv, idg)
            Log.i("idv", idv)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = gruposList()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.GruposContainer, prev)
                        .addToBackStack(null).commit()
                }
            })
    }

    private suspend fun isDarkModeEnabled(context: Context): Boolean {
        var komodo: Boolean

        withContext(Dispatchers.IO) {
            val assetsDao = Stlite.getInstance(context).getAssetsDao()

            val mode = assetsDao.getTheme()
            komodo = mode != 0
        }

        return komodo
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditgrupoBinding.inflate(inflater, container, false)
        colorPicker()
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_perfil2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_perfil)
            }

            Log.i("MODO", isDarkMode.toString())

            getGrupo()
            try {
                val jsonObjectG =
                    withContext(Dispatchers.IO) { JSONObject(URL("http://savetrack.com.mx/grupoGet.php?localid=$idori&admin=$admin").readText()) }
                val miembrosJSON =
                    withContext(Dispatchers.IO) { JSONArray(URL("http://savetrack.com.mx/gruposMiembrosGet.php?localid=$idori&admin=$admin").readText()) }
                val miembrosG = Array(miembrosJSON.length()) { miembrosJSON.getInt(it) }
                //val idgglobal: Long = jsonObjectG.getLong("idgrupoglobal")
                //val idglocal: String = jsonObjectG.getString("idgrupolocal")
                //val idadmin: Long = jsonObjectG.optLong("idadmin")

                val nombre: String = jsonObjectG.optString("nombre")
                val tipo: Int = jsonObjectG.optInt("tipo")
                val desc: String = jsonObjectG.optString("descripcion")
                color = jsonObjectG.optInt("color")
                val created: String = jsonObjectG.optString("created")
                val createdString = "Creado el $created"
                val adminName =
                    withContext(Dispatchers.IO) { URL("http://savetrack.com.mx/usernameget.php?id=$admin").readText() }

                if (!miembrosG.contains(iduser.toInt())) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.GruposContainer, gruposList()).addToBackStack(null).commit()
                    Toast.makeText(
                        requireContext(),
                        "Ya no formas parte del grupo",
                        Toast.LENGTH_SHORT
                    ).show()

                    deleteGrupo()
                } else {
                    binding.bar.text = nombre
                    binding.NombreField.setText(nombre)
                    binding.ColorField.setBackgroundColor(color)
                    binding.DescripcionField.setText(desc)
                    binding.TypeField.text = when (tipo) {
                        0 -> "Fijo"
                        1 -> "Temporal"
                        else -> "Eliminado"
                    }
                    binding.AdminField.text = adminName
                    binding.CreatedField.text = createdString
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Error during network call", e)
            }
        }
        return binding.root
    }

    private fun colorPicker() {
        binding.ColorField.setBackgroundColor(color)
    }

    private suspend fun getGrupo() {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

            val idg = arguments?.getLong(idv)
            if (idg != null) {
                idori = gruposDao.getIdori(idg.toInt())
                admin = gruposDao.getAdmin(idg.toInt())
                iduser = usuarioDao.checkId().toLong()
                ngrupo = gruposDao.getNameG(idg.toInt())
                enlace = gruposDao.getEnlace(idg.toInt())
            }
        }
    }

    private suspend fun deleteGrupo() {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val idg = arguments?.getLong(idv)
            if (idg != null) {
                val muertoGrupo = Grupos(
                    Id = idg,
                    nameg = gruposDao.getNameG(idg.toInt()),
                    description = gruposDao.getDescription(idg.toInt()),
                    type = 2,
                    admin = gruposDao.getAdmin(idg.toInt()),
                    idori = gruposDao.getIdori(idg.toInt()),
                    color = gruposDao.getColor(idg.toInt()),
                    enlace = gruposDao.getEnlace(idg.toInt())
                )
                gruposDao.updateGrupo(muertoGrupo)

                val plabel = labelsDao.getPlabel(idg.toInt())
                val muertaLabel = Labels(
                    idlabel = enlace,
                    plabel = plabel,
                    color = color,
                    estado = 1
                )

                labelsDao.updateLabel(muertaLabel)
                val labelss = labelsDao.getAllLabels()
                Log.i("ALL LABELS", labelss.toString())

                parentFragmentManager.beginTransaction()
                    .replace(R.id.GruposContainer, gruposList()).addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = gruposList()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.GruposContainer, back).addToBackStack(null).commit()
        }

        fun colorPicker(supportsAlpha: Boolean) {
            val dialog = AmbilWarnaDialog(
                requireContext(),
                color,
                supportsAlpha,
                object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                        this@grupoEdit.color = color
                        binding.ColorField.setBackgroundColor(color)
                    }

                    override fun onCancel(dialog: AmbilWarnaDialog) {
                        Toast.makeText(requireContext(), "cancel", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            dialog.show()
        }

        binding.ColorField.setOnClickListener {
            colorPicker(false)
        }

        binding.Confirm.setOnClickListener {
            val nombre = binding.NombreField.text.toString()
            val descrip = binding.DescripcionField.text.toString()

            if (nombre != "" && color != 0xffffff && descrip.length <= 100) {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres guardar cambios?")
                    .setPositiveButton("Guardar") { dialog, _ ->
                        Log.v("Nombre", nombre)
                        Log.v("Descripcion", descrip)
                        Log.v("Color", color.toString())

                        lifecycleScope.launch {
                            grupoEdit(nombre, descrip)
                        }
                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                            .replace(R.id.GruposContainer, back).addToBackStack(null).commit()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                confirmDialog.show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "No pueden haber campos vacíos",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.Cancel.setOnClickListener {
            val cancelDialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Seguro que quieres descartar cambios?")
                .setPositiveButton("Descartar") { dialog, _ ->
                    dialog.dismiss()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.GruposContainer, back).addToBackStack(null).commit()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            cancelDialog.show()
        }

        binding.Share.setOnClickListener {
            val linkToShare =
                "http://savetrack.com.mx/joingroup.php?zxcd125s5d765e7wqa87sdftgh=$idori&mnhjkmnbg1yhb3vdrtgvc98swe=$admin"

            val bitmap: Bitmap = generateQRCode(linkToShare)

            val whatsappIntent = Intent(Intent.ACTION_SEND)
            whatsappIntent.type = "image/jpeg"
            whatsappIntent.setPackage("com.whatsapp")
            whatsappIntent.putExtra(
                Intent.EXTRA_TEXT,
                "¡Únete a mi grupo de SaveTrack: $linkToShare!"
            )
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(requireContext(), bitmap))

            val gmailIntent = Intent(Intent.ACTION_SEND)
            gmailIntent.type = "image/jpeg"
            gmailIntent.setPackage("com.google.android.gm")
            gmailIntent.putExtra(Intent.EXTRA_SUBJECT, "¡Únete a mi grupo de SaveTrack!")
            gmailIntent.putExtra(
                Intent.EXTRA_TEXT,
                "¡Haz click en el enlace para unirte: $linkToShare!"
            )
            gmailIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(requireContext(), bitmap))

            val chooserIntent = Intent.createChooser(gmailIntent, "Compartir a través de:")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(whatsappIntent))

            try {
                startActivity(chooserIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    "No se encontraron aplicaciones para compartir",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.DeleteGrupo.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro de que deseas salir del grupo $ngrupo?")
            builder.setPositiveButton("Sí") { dialog, _ ->
                lifecycleScope.launch {
                    killGrupo(idori, admin, iduser)
                    deleteGrupo()
                }
                dialog.dismiss()
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.GruposContainer, back).addToBackStack(null).commit()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private suspend fun grupoEdit(
        nombre: String,
        descripcion: String,
    ) {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()

            val id = arguments?.getLong(idv)

            if (id != null) {
                val viejoGrupo = Grupos(
                    Id = id,
                    nameg = nombre,
                    description = descripcion,
                    type = gruposDao.getType(id.toInt()),
                    admin = admin,
                    idori = gruposDao.getIdori(id.toInt()),
                    color = color,
                    enlace = enlace
                )

                val queue = Volley.newRequestQueue(requireContext())
                val grupoJson =
                    withContext(Dispatchers.IO) { JSONObject(URL("http://savetrack.com.mx/grupoGet.php?localid=${viejoGrupo.idori}&admin=$admin").readText()) }

                if (grupoJson.getLong("idgrupoglobal") != null) {
                    lifecycleScope.launch {
                        val idori: Long = grupoJson.getLong("idgrupolocal")
                        val idadmin: Long = grupoJson.getLong("idadmin")
                        val tipo: Int = grupoJson.optInt("tipo")

                        var url = "http://savetrack.com.mx/grupoUpdate.php?"

                        val nombre = binding.NombreField.text
                        val desc = binding.DescripcionField.text

                        val dialog = AmbilWarnaDialog(
                            requireContext(),
                            color,
                            false,
                            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                                override fun onOk(dialog: AmbilWarnaDialog, colorin: Int) {
                                    color = colorin
                                    binding.ColorField.setBackgroundColor(color)
                                }

                                override fun onCancel(dialog: AmbilWarnaDialog) {
                                    Toast.makeText(requireContext(), "cancel", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        )
                        
                        val requestBody =
                            "localid=$idori&admin=$idadmin&nameg=$nombre&type=$tipo&desc=$desc&color=$color"
                        url += requestBody
                        val stringReq: StringRequest =
                            object : StringRequest(
                                Method.PUT, url,
                                Response.Listener { response ->
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
                    }
                } else {
                    Log.v("Current monto", "VACÍO")
                }

                gruposDao.updateGrupo(viejoGrupo)
                val grupos = gruposDao.getAllGrupos()
                Log.i("ALL GRUPOS", grupos.toString())
            }
        }
    }

    private fun killGrupo(idori: Long, admin: Long, iduser: Long) {
        val queue = Volley.newRequestQueue(requireContext())
        var url = "http://savetrack.com.mx/grupoDelete.php?"
        val jsonMiembros = JSONArray()

        Log.v("JSONMIEMBROS", "$jsonMiembros")
        val requestBody = "localid=$idori&admin=$admin&miembros=$jsonMiembros"
        url += requestBody
        val stringReq: StringRequest =
            object : StringRequest(
                Method.PUT, url,
                Response.Listener { response ->
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
    }

    private fun generateQRCode(text: String): Bitmap {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix: BitMatrix =
                multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500)
            return Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
                .apply {
                    for (x in 0 until bitMatrix.width) {
                        for (y in 0 until bitMatrix.height) {
                            setPixel(x, y, if (bitMatrix[x, y]) color else antiColor(color))
                        }
                    }
                }
        } catch (e: WriterException) {
            e.printStackTrace()
            throw RuntimeException("Error al generar el código QR", e)
        }
    }

    private fun getImageUri(context: Context, bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "steqr", null)
        return Uri.parse(path)
    }

    private fun antiColor(colorDado: Int): Int {
        val luminancia =
            (0.299 * Color.red(colorDado) + 0.587 * Color.green(colorDado) + 0.114 * Color.blue(
                colorDado
            )) / 255.0

        val luminanciaFondo = if (luminancia > 0.5) {
            luminancia - 0.5
        } else {
            luminancia + 0.5
        }

        val r = (luminanciaFondo * 255).toInt().coerceIn(0, 255)
        val g = (luminanciaFondo * 255).toInt().coerceIn(0, 255)
        val b = (luminanciaFondo * 255).toInt().coerceIn(0, 255)

        return Color.rgb(r, g, b)
    }
}