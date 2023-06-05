package com.example.st5

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Looper
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
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class perfileditar : Fragment() {
    private lateinit var binding: FragmentPerfileditarBinding

    private val pickImageRequest = 1
    private lateinit var username: String
    private var edadchanged = true
    private var chambachanged = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = perfilmain()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.perfil_container, prev)
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
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.perfil_container, back).addToBackStack(null).commit()
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
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.perfil_container, back).addToBackStack(null).commit()
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
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.perfil_container, back).addToBackStack(null).commit()
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == RESULT_OK && data != null && data.data != null) {
            val filePath: Uri? = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath)
                val fo = filePath.toString()

                val compressedBitmap = compressImage(bitmap)

                val image: String = getStringImage(compressedBitmap)
                Log.d("bytes", compressedBitmap.byteCount.toString())
                Log.d("actual image size", image.length.toString())

                lifecycleScope.launch{
                    sendImage(image, username)
                }
                lifecycleScope.launch{
                    cambiarFoto(fo)
                }
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
    private fun compressImage(image: Bitmap): Bitmap {
        val maxFileSize = 75000
        val tempo: String = getStringImage(image)
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        var compressedSize = tempo.length

        var quality = 90
        while (compressedSize > maxFileSize && quality > 10) {
            outputStream.reset()
            image.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            compressedSize = outputStream.toByteArray().toString().length
            Log.v("reducing size to", compressedSize.toString())
            quality -= 10
        }

        val compressedBitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
        outputStream.close()

        return compressedBitmap
    }
    private suspend fun cambiarFoto(nfo: String) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

            val id = usuarioDao.checkId()
            usuarioDao.updatePhoto(id, nfo)

            lifecycleScope.launch{
                bajarfoto(nfo)
            }
        }
    }
    private suspend fun sendImage(image: String, username: String) {
        withContext(Dispatchers.IO) {
            Looper.prepare()
            val baseUrl = "http://savetrack.com.mx/images/chunkpic.php"
            val chunkSize = 2000

            val chunks = image.chunked(chunkSize)
            Log.w("TAMAÑO", (chunks.size * chunkSize).toString())
            Log.w("CHUNKS", chunks.size.toString())

            if (chunks.size <= 200) {
                for ((index, i) in chunks.indices.withIndex()) {
                    val chunk = chunks[i]
                    val params = hashMapOf<String, String>()
                    params["username"] = username
                    params["chunkIndex"] = index.toString()
                    params["totalChunks"] = chunks.size.toString()
                    params["imageChunk"] = chunk
                    val url =
                        "$baseUrl?username=$username&chunkIndex=$i&totalChunks=${chunks.size}&imageChunk=$chunk"
                    Log.v("CurrURL", url)
                    val stringRequest = object : StringRequest(
                        Method.PUT, url,
                        Response.Listener { response ->
                            Log.d("UPLOAD SUCCESS", i.toString())
                            Log.i("response", response)
                        },
                        Response.ErrorListener { error ->
                            Log.e("UPLOAD API ERROR", error.toString())
                        }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            val paramo: MutableMap<String, String> = Hashtable()
                            paramo["image"] = image
                            return paramo
                        }
                    }

                    val socketTimeout = 45000
                    val policy: RetryPolicy = DefaultRetryPolicy(
                        socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
                    stringRequest.retryPolicy = policy
                    val requestQueue = Volley.newRequestQueue(requireContext())
                    requestQueue.add(stringRequest)

                    delay(10000)
                }
            } else {
                Toast.makeText(requireContext(), "La imagen es demasiado grande, intente con una más pequeña", Toast.LENGTH_SHORT).show()
            }

        }
    }

    @SuppressLint("IntentReset")
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
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

            val id = usuarioDao.checkId()
            binding.agregarfotobtn.load(link) {
                crossfade(true)
                placeholder(R.drawable.ic_person)
                transformations(CircleCropTransformation())
                scale(Scale.FILL)
            }
            usuarioDao.updatePhoto(id, link)
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

            if (cFinal == "000000"){
                chambachanged = false
            }

            val nuevaChamba = cFinal.toLong()

            val edadlong = nuevaEdad.toLong()
            if (edadlong <= 5 || edadlong >= 121) {
                edadchanged = false
                withContext(Dispatchers.Default) {
                    Looper.prepare()
                    Toast.makeText(requireContext(), "La edad ingresada no es válida", Toast.LENGTH_SHORT).show()
                }
                Dispatchers.Default.cancel()
            }

            if (edadchanged){
                usuarioDao.updateAge(idt, edadlong)
            }
            if (chambachanged){
                usuarioDao.updateChamba(idt, nuevaChamba)
            }


            withContext(Dispatchers.Main) {
                if (edadchanged || chambachanged){
                    Toast.makeText(
                        requireContext(),
                        "Cambios guardados correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Dispatchers.Main.cancel()
            }
        }
    }
}