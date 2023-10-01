package com.example.st5

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentGruposlistBinding
import com.example.st5.models.Grupos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class gruposList : Fragment(){
    private lateinit var binding: FragmentGruposlistBinding
    private lateinit var grupos: List<Grupos>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            var isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
            }
            Log.i("MODO", isDarkMode.toString())
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(activity, Index::class.java)
                    intent.putExtra("currentView", 0)
                    startActivity(intent)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGruposlistBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            grupos = getGrupos()
            binding.displayGrupos.adapter = GrupoAdapter(grupos)
        }
        return binding.root
    }

    private suspend fun getGrupos(): List<Grupos> {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()

            grupos = gruposDao.getAllGrupos()
            Log.v("ALL GRUPOS", "$grupos")
        }
        return grupos
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goback.setOnClickListener {
            val intent = Intent(activity, Index::class.java)
            intent.putExtra("currentView", 0)
            startActivity(intent)
        }

        binding.AgregarGrupoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.GruposContainer, gruposAdd()).addToBackStack(null).commit()
        }
    }

    private inner class GrupoAdapter(private val grupos: List<Grupos>) :
        RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder>() {
        inner class GrupoViewHolder(
            itemView: View,
            val grupo: TextView,
            val tipo: TextView,
            val color: ImageView,
            val verGrupo: Button
        ) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_grupo, parent, false)
            val grupo = itemView.findViewById<TextView>(R.id.GNombre)
            val tipo = itemView.findViewById<TextView>(R.id.GTipo)
            val color = itemView.findViewById<ImageView>(R.id.GColor)
            val verGrupo = itemView.findViewById<Button>(R.id.viewGrupo)
            return GrupoViewHolder(
                itemView,
                grupo,
                tipo,
                color,
                verGrupo
            )
        }


        override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
            val grupo = grupos[position]
            holder.grupo.text = grupo.nameg
            holder.tipo.text = when (grupo.type) {
                1 -> "Fijo"
                2 -> "Temporal"
                else -> "Eliminado"
            }
            holder.color.setBackgroundColor(grupo.color)

            holder.verGrupo.setOnClickListener {

            }
        }


        override fun getItemCount(): Int {
            Log.v("size de Grupossss", grupos.size.toString())
            return grupos.size
        }
    }
}