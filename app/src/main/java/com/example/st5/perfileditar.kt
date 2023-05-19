package com.example.st5

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPerfileditarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class perfileditar : Fragment() {
    private lateinit var binding: FragmentPerfileditarBinding
    private val pickImageRequest = 1
    private lateinit var username: String
    var fotochanged = false
    var edadchanged = false
    var chambachanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = perfilmain()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.ViewContainer, prev)
                        .addToBackStack(null).commit()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfileditarBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val back = perfilmain()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.ViewContainer, back).addToBackStack(null).commit()
        }

        binding.Confirm.setOnClickListener {
            val confirmDialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Seguro que quieres guardar cambios?")
                .setPositiveButton("Guardar") { dialog, _ ->
                    lifecycleScope.launch {
                        guardarCambios()
                    }
                    dialog.dismiss()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.ViewContainer, back).addToBackStack(null).commit()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            confirmDialog.show()
        }

        binding.Cancel.setOnClickListener {
            val cancelDialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Seguro que quieres descartar cambios?")
                .setPositiveButton("Descartar") { dialog, _ ->
                    dialog.dismiss()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.ViewContainer, back).addToBackStack(null).commit()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            cancelDialog.show()
        }

        lifecycleScope.launch {
            mostrarDatos()
        }

        binding.agregarfotobtn.setOnClickListener {
            showFileChooser()
        }


    }

    // TODO: IMAGELOAD REQUEST PARA SUBIR DIRECTAMENTE COMO IMAGEN, si no se puede intentar con chunks y que sea lo que Dios quiera

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == RESULT_OK && data != null && data.data != null) {
            val filePath: Uri? = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath)
                var lastBitmap: Bitmap?
                lastBitmap = bitmap

                val image: String = getStringImage(lastBitmap)
                Log.d("image", image)


                sendImage(image, username)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun getStringImage(bmp: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }


    private fun sendImage(image: String, username: String) {
        binding.agregarfotobtn.load(image) {
            crossfade(true)
            placeholder(R.drawable.ic_add_24)
            transformations(CircleCropTransformation())
            scale(Scale.FILL)
        }

        val baseUrl = "http://savetrack.com.mx/chunkpic.php"
        val chunkSize = 1000 // Tamaño máximo de cada chunk en caracteres

        val chunks = image.chunked(chunkSize)

        for (i in chunks.indices) {
            val chunk = chunks[i]
            val params = hashMapOf<String, String>()
            params["username"] = username
            params["chunkIndex"] = i.toString()
            params["totalChunks"] = chunks.size.toString()
            params["imageChunk"] = chunk
            val url =
                "$baseUrl?username=$username&chunkIndex=$i&totalChunks=${chunks.size}&imageChunk=$chunk"
            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    Log.d("UPLOAD SUCCESS", response)
                    try {
                        val jsonObject = JSONObject(response)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("UPLOAD API ERROR", error.toString())
                    Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_LONG).show()
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val paramo: MutableMap<String, String> = Hashtable()
                    paramo["image"] = image
                    return paramo
                }
            }


            val socketTimeout = 30000
            val policy: RetryPolicy = DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            stringRequest.retryPolicy = policy
            val requestQueue = Volley.newRequestQueue(requireContext())
            requestQueue.add(stringRequest)
        }
    }

    private fun showFileChooser() {
        val pickImageIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickImageIntent.type = "image/*"
        pickImageIntent.putExtra("aspectX", 1)
        pickImageIntent.putExtra("aspectY", 1)
        pickImageIntent.putExtra("scale", true)
        pickImageIntent.putExtra(
            "outputFormat",
            Bitmap.CompressFormat.JPEG.toString()
        )
        startActivityForResult(pickImageIntent, pickImageRequest)
    }
    private suspend fun bajarfoto(link: String) {
        withContext(Dispatchers.IO) {
            binding.agregarfotobtn.load(link) {
                crossfade(true)
                placeholder(R.drawable.ic_add_24)
                transformations(CircleCropTransformation())
                scale(Scale.FILL)
            }
        }
    }

    private suspend fun mostrarDatos() {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(
                requireContext()
            ).getUsuarioDao()

            val nombre = usuarioDao.checkName()
            val edad = usuarioDao.checkAge()
            val chamba = usuarioDao.checkChamba()
            username = nombre
            Log.v("Name", nombre)
            Log.v("Age", edad.toString())
            Log.v("Chamba", chamba.toString())

            binding.UsernameeditperfTV.text = nombre
            binding.AgeeditperfTV.setText(edad.toString())

            val linkfoto = "http://savetrack.com.mx/images/$nombre.jpg"
            lifecycleScope.launch {
                bajarfoto(linkfoto)
            }
        }
    }

    private suspend fun guardarCambios() {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

            val idt = usuarioDao.checkId()
            val nuevoNombre = binding.UsernameeditperfTV.text.toString()
            val nuevaEdad = binding.AgeeditperfTV.text.toString()

            // ARBOL DE DECISIONES PARA CADA CASO DE CHAMBA
            // AUTÓMATA FINITO o algo así
            /*
            CHAMBAS
                1. Salario
                2. Vendedor
                3. Pensionado
                4. Becado
                5. Mantenido
                6. Inversionista
            */

            val c = arrayOf("0", "0", "0", "0", "0", "0")

            if (binding.chamba1.isChecked) {
                c[0] = "1"
            }
            if (binding.chamba2.isChecked) {
                c[1] = "2"
            }
            if (binding.chamba3.isChecked) {
                c[2] = "3"
            }
            if (binding.chamba4.isChecked) {
                c[3] = "4"
            }
            if (binding.chamba5.isChecked) {
                c[4] = "5"
            }
            if (binding.chamba6.isChecked) {
                c[5] = "6"
            }

            var cFinal = ""
            for (v in c) {
                cFinal += v
            }

            val nuevaChamba = cFinal.toLong()

            if (nuevoNombre.isEmpty() || nuevaEdad.isEmpty() || cFinal.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Todos los campos deben ser completados",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@withContext
            }

            //actualBitmap?.let { uploadImage(nuevoNombre, it) }
            usuarioDao.updateAge(idt, nuevaEdad.toLong())
            usuarioDao.updateChamba(idt, nuevaChamba)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Cambios guardados correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}