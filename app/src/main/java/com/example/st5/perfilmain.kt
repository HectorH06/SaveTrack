package com.example.st5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.example.st5.databinding.FragmentPerfilmainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class perfilmain : Fragment() {
    private lateinit var binding: FragmentPerfilmainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /*val actual = perfilmain()
                    parentFragmentManager.beginTransaction().replace(R.id.FragContainer, actual)
                        .addToBackStack(null).commit()*/
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
                    placeholder(R.drawable.ic_add_24)
                    transformations(CircleCropTransformation())
                    scale(Scale.FILL)
                }
            }
        }
        binding.cerrarsesionperfilmainbtn.setOnClickListener {
            val init = Login()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.ContainerPerfil, init).addToBackStack(null).commit()
        }

        binding.EditProfileButton.setOnClickListener {
            val edit = perfileditar()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.ContainerPerfil, edit).addToBackStack(null).commit()
        }

        val linkfoto = "https://i.pinimg.com/474x/8f/cf/e4/8fcfe4e3ef67949eac587526db013da9.jpg"
        lifecycleScope.launch {
            bajarfoto(linkfoto)
        }

    }
    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = context?.let {
            Room.databaseBuilder(
                it.applicationContext,
                Stlite::class.java, "Stlite"
            ).build()
        }

// Llamar a un método de consulta en la instancia de la base de datos para obtener los datos que deseas mostrar
        val nombre = db.UsuarioDao(requireContext()).getUserById(id)
        super.onViewCreated(view, savedInstanceState)
        binding.UsernameTV.setText(Usuario.nombre)
    }
    */
}