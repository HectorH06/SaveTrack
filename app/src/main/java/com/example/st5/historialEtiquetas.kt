package com.example.st5

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentHistorialetiquetasBinding
import com.example.st5.models.Labels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class historialEtiquetas : Fragment() {
    private lateinit var binding: FragmentHistorialetiquetasBinding

    private lateinit var labelsp: List<Labels>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val prev = historialmain()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                        .replace(R.id.historial_container, prev)
                        .addToBackStack(null).commit()
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

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistorialetiquetasBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_historial2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_historial)
            }

            Log.i("MODO", isDarkMode.toString())

            labelsp = labelsget()
            binding.displayLabels.adapter = LabelsAdapter(labelsp)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.historial_container, historialmain()).addToBackStack(null).commit()
        }

        binding.AgregarLabelButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.historial_container, historialAdd()).addToBackStack(null).commit()
        }
    }

    private suspend fun labelsget(): List<Labels> {
        withContext(Dispatchers.IO) {
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            labelsp = labelsDao.getAllLabelsZero()
            Log.i("ALL LABELS", labelsp.toString())
        }
        return labelsp
    }

    private suspend fun labeldelete(
        idlabel: Long,
        plabel: String,
        color: Int
    ) {
        withContext(Dispatchers.IO) {
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()

            val muertaLabel = Labels(
                idlabel = idlabel,
                plabel = plabel,
                color = color,
                estado = 1
            )

            labelsDao.updateLabel(muertaLabel)
            val labelss = labelsDao.getAllLabels()
            Log.i("ALL LABELS", labelss.toString())

            parentFragmentManager.beginTransaction()
                .replace(R.id.historial_container, historialPapelera()).addToBackStack(null)
                .commit()
        }
    }

    private inner class LabelsAdapter(private val labelss: List<Labels>) :
        RecyclerView.Adapter<LabelsAdapter.LabelsViewHolder>() {
        inner class LabelsViewHolder(
            itemView: View,
            val nombreTextView: TextView,
            val colorImageView: ImageView,
            val optionsM: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelsViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_label, parent, false)
            val nombreTextView = itemView.findViewById<TextView>(R.id.LNombre)
            val colorImageView = itemView.findViewById<ImageView>(R.id.LColor)
            val optionsM = itemView.findViewById<Button>(R.id.itemOptions)
            return LabelsViewHolder(
                itemView,
                nombreTextView,
                colorImageView,
                optionsM
            )
        }

        override fun onBindViewHolder(holder: LabelsViewHolder, position: Int) {
            val labels = labelss[position]
            Log.i("INFO", labels.idlabel.toString() + ", " + labels.plabel + ", " + labels.color)
            holder.nombreTextView.text = labels.plabel
            holder.colorImageView.setBackgroundColor(labels.color)
            val upup = historialUpdate.sendLabel(labels.idlabel, labels.plabel, labels.color)

            holder.optionsM.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), holder.optionsM, Gravity.END)
                val inflater = popupMenu.menuInflater
                inflater.inflate(R.menu.options_item_label, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.action_editLabel -> {
                            parentFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                .replace(R.id.historial_container, upup).addToBackStack(null).commit()

                            true
                        }
                        R.id.action_deleteLabel -> {
                            val confirmDialog = AlertDialog.Builder(requireContext())
                                .setTitle("¿Seguro que quieres eliminar la etiqueta ${labels.plabel}? Esta acción no se puede deshacer")
                                .setPositiveButton("Guardar") { dialog, _ ->

                                    Log.v("Id de la etiqueta actualizada", labels.idlabel.toString())
                                    Log.v("Plabel", labels.plabel)
                                    Log.v("Color", labels.color.toString())
                                    lifecycleScope.launch {
                                        labeldelete(labels.idlabel, labels.plabel, labels.color)
                                    }
                                    dialog.dismiss()
                                    parentFragmentManager.beginTransaction()
                                        .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                                        .replace(R.id.historial_container, historialmain()).addToBackStack(null).commit()
                                }
                                .setNegativeButton("Cancelar") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .create()

                            confirmDialog.show()

                            true
                        }
                        else -> false
                    }
                }

                popupMenu.show()
            }
            if (position == labelsp.size - 1){
                holder.itemView.setBackgroundResource(R.drawable.y1bottomcell)
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de labelsssss", labelss.size.toString())
            return labelss.size
        }
    }
}