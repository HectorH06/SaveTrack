package com.example.st5

import android.content.Context
import android.icu.text.DecimalFormat
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
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPerfilmainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class perfilmain : Fragment() {
    private lateinit var binding: FragmentPerfilmainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_perfil2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_perfil)
            }

            Log.i("MODO", isDarkMode.toString())
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
    }

    private suspend fun isDarkModeEnabled(context: Context): Boolean {
        var komodo: Boolean

        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(context).getAssetsDao()

            val mode = assetsDao.getTheme()
            komodo = mode != 0
        }

        return komodo
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

        binding.EditProfileButton.setOnClickListener {
            val edit = perfileditar()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.perfil_container, edit).addToBackStack(null).commit()
        }

        binding.EditProfileButton2.setOnClickListener {
            val edit = perfileditar()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.perfil_container, edit).addToBackStack(null).commit()
        }

        binding.Config.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.perfil_container, Configuracion()).addToBackStack(null).commit()
        }

        suspend fun mostrarDatos() {
            withContext(Dispatchers.IO) {
                val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()
                val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()

                val totalIngresos = ingresosGastosDao.checkSummaryI()
                val totalGastos = ingresosGastosDao.checkSummaryG()
                val totalisimo = totalIngresos - totalGastos
                val decimalFormat = DecimalFormat("#.##")
                val balance = "${decimalFormat.format(totalisimo)}$"
                val nombre = usuarioDao.checkName()
                val edad = usuarioDao.checkAge()
                val lachamba = usuarioDao.checkChamba()
                val foto = usuarioDao.checkFoto()
                val diasaho = usuarioDao.checkDiasaho()
                usuarioDao.updateBalance(usuarioDao.checkId(), totalisimo)

                var chamba = ""
                val c = String.format("%06d", lachamba).toCharArray()
                if (c[0] == '1') {
                    chamba += "asalariado, "
                }
                if (c[1] == '2') {
                    chamba += "vendedor, "
                }
                if (c[2] == '3') {
                    chamba += "pensionado, "
                }
                if (c[3] == '4') {
                    chamba += "becado, "
                }
                if (c[4] == '5') {
                    chamba += "mantenido, "
                }
                if (c[5] == '6') {
                    chamba += "inversionista, "
                }

                if (chamba.isNotEmpty()) {
                    chamba = chamba.dropLast(2)
                    chamba = chamba.replaceFirstChar { it.uppercaseChar() }
                }

                Log.v("Name", nombre)
                Log.v("Age", edad.toString())
                Log.v("Código de Chamba", lachamba.toString())
                Log.v("Descripción de Chamba", chamba)
                Log.v("Foto ", foto)
                Log.v("Diasaho", diasaho.toString())
                Log.v("Balance", balance)

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
                    append(balance)
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