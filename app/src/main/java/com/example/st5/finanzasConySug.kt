package com.example.st5

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentFinanzasconsejosysugBinding
import com.example.st5.models.ConySug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class finanzasConySug : Fragment(){
    private lateinit var binding: FragmentFinanzasconsejosysugBinding
    private lateinit var consejos: List<ConySug>
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
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.finanzas_container, finanzasmain()).addToBackStack(null).commit()
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
        binding = FragmentFinanzasconsejosysugBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            consejos = getConsejos()
            binding.displayCS.adapter = ConsejoAdapter(consejos)
        }
        return binding.root
    }

    private suspend fun getConsejos(): List<ConySug> {
        withContext(Dispatchers.IO) {
            val conySugDao = Stlite.getInstance(requireContext()).getConySugDao()

            consejos = conySugDao.getAllActiveCon()
        }
        return consejos
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val back = finanzasmain();

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.finanzas_container, back).addToBackStack(null).commit()
        }

        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, Configuracion()).addToBackStack(null).commit()
        }
    }

    private inner class ConsejoAdapter(private val consejos: List<ConySug>) :
        RecyclerView.Adapter<ConsejoAdapter.ConsejoViewHolder>() {
        inner class ConsejoViewHolder(
            itemView: View,
            val cardView: CardView,
            val consejo: TextView,
            val descripcion: TextView,
            val accept: Button,
            val ignore: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsejoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_consejo, parent, false)
            val cardView = itemView.findViewById<CardView>(R.id.CS)
            val consejo = itemView.findViewById<TextView>(R.id.consejo)
            val descripcion = itemView.findViewById<TextView>(R.id.descripcion)
            val accept = itemView.findViewById<Button>(R.id.aceptar)
            val ignore = itemView.findViewById<Button>(R.id.ignorar)
            return ConsejoViewHolder(
                itemView,
                cardView,
                consejo,
                descripcion,
                accept,
                ignore
            )
        }


        override fun onBindViewHolder(holder: ConsejoViewHolder, position: Int) {
            val consejo = consejos[position]
            holder.cardView.setCardBackgroundColor(when (consejo.style) {
                1 -> R.color.Y1
                2 -> R.color.O2
                3 -> R.color.R2
                else -> R.color.G4
            })
            holder.consejo.text = consejo.nombre
            holder.descripcion.text = consejo.contenido
            holder.accept.setOnClickListener {

            }
            holder.ignore.setOnClickListener {

            }
        }


        override fun getItemCount(): Int {
            Log.v("size de Consejossss", consejos.size.toString())
            return consejos.size
        }
    }
}