package com.example.st5

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPerfileditarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class perfileditar : Fragment() {
    private lateinit var binding: FragmentPerfileditarBinding
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private var actualBitmap: Bitmap? = null

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

        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let { selectedImageUri ->
                    val picturePath: String =
                        getPath(requireActivity().applicationContext, selectedImageUri)
                    binding.agregarfotobtn.load(selectedImageUri) {
                        crossfade(true)
                        placeholder(R.drawable.ic_add_24)
                        transformations(CircleCropTransformation())
                        scale(Scale.FILL)
                    }
                    Log.v("Path", picturePath)

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    val bm = BitmapFactory.decodeFile(picturePath)
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
                    val imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                    Log.v("Image", imageString)

                }
            }

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

        suspend fun getU() {
            withContext(Dispatchers.IO) {
                val usuarioDao = Stlite.getInstance(
                    requireContext()
                ).getUsuarioDao()
                val user = usuarioDao.checkName()
                binding.agregarfotobtn.setOnClickListener {
                    galleryLauncher.launch("image/*")
                    actualBitmap?.let { it1 -> uploadImage(it1, user) }
                }
            }
        }

        lifecycleScope.launch {
            getU()
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


    }

    private fun getPath(context: Context, uri: Uri?): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
            uri?.let { context.contentResolver.query(it, proj, null, null, null) }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnindex: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(columnindex)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }

    private fun uploadImage(imageBitmap: Bitmap, username: String) {
        val url = "http://www.savetrack.com.mx/images/$username.jpg"

        val requestQueue = Volley.newRequestQueue(requireContext())

        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()


        fun getByteData(): MutableMap<String, DataPart> {
            val params = HashMap<String, DataPart>()
            params["image"] = DataPart("$username.jpg", imageBytes, "image/jpeg")
            return params
        }

        // Crear la solicitud POST para subir la imagen
        val request = object : MultipartRequest(
            Method.POST, url,
            params = getByteData(),
            Response.Listener { response ->
                Toast.makeText(
                    requireContext(),
                    "La imagen se ha subido correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                Log.v("Response", response.toString())
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    requireContext(),
                    "Error subiendo la imagen: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {}
        requestQueue.add(request)
    }

    private suspend fun mostrarDatos() {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(
                requireContext()
            ).getUsuarioDao()

            val nombre = usuarioDao.checkName()
            val edad = usuarioDao.checkAge()
            val chamba = usuarioDao.checkChamba()

            Log.v("Name", nombre)
            Log.v("Age", edad.toString())
            Log.v("Chamba", chamba.toString())

            binding.UsernameeditperfTV.text = nombre
            binding.AgeeditperfTV.setText(edad.toString())
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