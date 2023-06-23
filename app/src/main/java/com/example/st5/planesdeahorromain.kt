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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPlanesdeahorromainBinding
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class planesdeahorromain : Fragment() {
    private lateinit var binding: FragmentPlanesdeahorromainBinding

    private lateinit var pda: List<Monto>
    private var dolarValue: String = ""
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
        binding = FragmentPlanesdeahorromainBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            pda = montosget()
            binding.displayPda.adapter = MontoAdapter(pda)
        }
        return binding.root
    }

    private suspend fun montosget(): List<Monto> {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val owe = 8

            pda = montoDao.getGastos(owe)
            Log.i("ALL MONTOS", pda.toString())
        }
        return pda
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = perfilmain()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.perfil_container, back).addToBackStack(null).commit()
        }

        suspend fun mostrarDatos() {
            withContext(Dispatchers.IO) {
                val usuarioDao = Stlite.getInstance(
                    requireContext()
                ).getUsuarioDao()

                val diasaho = usuarioDao.checkDiasaho()
                val balance = usuarioDao.checkBalance()

                val durl = "http://savetrack.com.mx/dlrval.php"
                val queue: RequestQueue = Volley.newRequestQueue(requireContext())
                val checkDollar = StringRequest(
                    Request.Method.GET, durl,
                    { response ->
                        dolarValue = response.toString()
                        Log.d("DÓLAR HOY", dolarValue)
                        binding.PACurrencyButton.text = buildString {
                            append("Dolar HOY: ")
                            append("$$dolarValue")
                        }
                    },
                    { error ->
                        Toast.makeText(
                            requireContext(), "No se ha podido conectar al valor del dólar hoy", Toast.LENGTH_SHORT
                        ).show()
                        binding.PACurrencyButton.text = buildString {
                            append("Sin conexión")
                        }
                        Log.d("error => $error", "SIE API ERROR")
                    }
                )
                queue.add(checkDollar)

                Log.v("Diasaho", diasaho.toString())
                Log.v("Balance", balance.toString())

                binding.PADiasAhorrandoButton.text = buildString {
                    append("¡")
                    append(diasaho.toString())
                    append(" días ahorrando!")
                }
                binding.PASaldoActual.text = buildString {
                    append("Balance: ")
                    append(balance.toString())
                }
            }
        }
        lifecycleScope.launch {
            mostrarDatos()
        }
    }

    private inner class MontoAdapter(private val montos: List<Monto>) :
        RecyclerView.Adapter<MontoAdapter.MontoViewHolder>() {
        inner class MontoViewHolder(
            itemView: View,
            val conceptoTextView: TextView,
            val valorTextView: TextView,
            val fechaTextView: TextView,
            val updateM: Button,
            val deleteM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MontoViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_pda, parent, false)
            val conceptoTextView = itemView.findViewById<TextView>(R.id.pdaNombre)
            val valorTextView = itemView.findViewById<TextView>(R.id.pdaValor)
            val fechaTextView = itemView.findViewById<TextView>(R.id.pdaFecha)
            val updateM = itemView.findViewById<Button>(R.id.editPda)
            val deleteM = itemView.findViewById<Button>(R.id.deletePda)
            return MontoViewHolder(
                itemView,
                conceptoTextView,
                valorTextView,
                fechaTextView,
                updateM,
                deleteM
            )
        }


        override fun onBindViewHolder(holder: MontoViewHolder, position: Int) {
            val monto = montos[position]
            holder.conceptoTextView.text = monto.concepto
            holder.valorTextView.text = monto.valor.toString()
            holder.fechaTextView.text = monto.fecha
            val upup = indexmontoupdate.sendMonto(
                monto.idmonto,
                monto.concepto,
                monto.valor,
                monto.fecha,
                monto.frecuencia,
                monto.etiqueta,
                monto.interes,
                monto.veces,
                monto.adddate
            )
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
                            .replace(R.id.pda_container, indexmain()).addToBackStack(null).commit()
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