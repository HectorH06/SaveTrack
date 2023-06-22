package com.example.st5

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPlanesdeahorromainBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class planesdeahorromain : Fragment() {
    private lateinit var binding: FragmentPlanesdeahorromainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_planes_de_ahorro2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_planes_de_ahorro)
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanesdeahorromainBinding.inflate(inflater, container, false)
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


    }

    private inner class MontoAdapter(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val conceptoTextView: TextView,
            val valorTextView: TextView,
            val vecesTextView: TextView,
            val etiquetaTextView: TextView,
            val updateM: Button,
            val deleteM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.MConcepto)
            val valorTextView = itemView.findViewById<TextView>(R.id.MValor)
            val vecesTextView = itemView.findViewById<TextView>(R.id.MVeces)
            val etiquetaTextView = itemView.findViewById<TextView>(R.id.MEtiqueta)
            val updateM = itemView.findViewById<Button>(R.id.editMonto)
            val deleteM = itemView.findViewById<Button>(R.id.deleteMonto)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                vecesTextView,
                etiquetaTextView,
                updateM,
                deleteM
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = monto.valor.toString()
            holder.vecesTextView.text = monto.veces.toString()
            holder.etiquetaTextView.text = monto.etiqueta.toString()
            val upup = indexmontoupdate.sendMonto(monto.idmonto, monto.concepto, monto.valor, monto.fecha, monto.frecuencia, monto.etiqueta, monto.interes, monto.veces, monto.adddate)
            holder.updateM.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                    .replace(R.id.historial_container, upup).addToBackStack(null).commit()
            }
            holder.deleteM.setOnClickListener {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Seguro que quieres eliminar el monto ${monto.concepto}? Esta acción no se puede deshacer")
                    .setPositiveButton("Guardar") { dialog, _ ->

                        Log.v("Id del monto actualizado", monto.idmonto.toString())
                        Log.v("Concepto", monto.concepto)
                        Log.v("Valor", monto.valor.toString())
                        Log.v("Fecha", monto.fecha)
                        Log.v("Frecuencia", monto.frecuencia.toString())
                        Log.v("Etiqueta", monto.etiqueta.toString())
                        Log.v("Interes", monto.interes.toString())
                        Log.v("Veces", monto.veces.toString())

                        dialog.dismiss()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                            .replace(R.id.index_container, indexmain()).addToBackStack(null).commit()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                confirmDialog.show()
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de montossss", montos.size.toString())
            return montos.size
        }
    }
}