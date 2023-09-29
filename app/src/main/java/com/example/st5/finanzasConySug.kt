package com.example.st5

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
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
            val allCon = conySugDao.getAllCon()
            Log.v("ALLCON", "$allCon")
            Log.v("ACTIVECON", "$consejos")
        }
        return consejos
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val back = finanzasmain()

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
                1 -> resources.getColor(R.color.Y1)
                2 -> resources.getColor(R.color.O2)
                3 -> resources.getColor(R.color.R2)
                else -> resources.getColor(R.color.G4)
            })
            holder.consejo.text = consejo.nombre
            holder.descripcion.text = consejo.contenido

            holder.accept.setOnClickListener {
                lifecycleScope.launch {
                    //acceptedOrRejected(consejo.idcon, 2)
                    val intent = Intent(activity, Index::class.java)
                    var goTo = when (consejo.idcon) {
                        in 0..99 -> 0
                        in 100..199 -> 4
                        in 200..299 -> 4
                        in 300..399 -> 0
                        in 400..499 -> 2
                        in 500..599 -> 1
                        in 600..699 -> 2
                        in 700..799 -> 3
                        in 800..899 -> 1
                        else -> 2
                    }
                    val fragmento = when (consejo.idcon) {
                        500L, 501L, 700L, in 800L..802L -> 1
                        0L, 1L, 200L, 204L, 205L, 502L -> 2
                        203L -> {
                            goTo = 0
                            2
                        }
                        201L, 202L -> 3
                        400L, in 600L..604L -> 4
                        401L -> 5
                        100L, 101L -> 6
                        else -> 0
                    }
                    Log.v("Intent data", "${consejo.idcon}, $goTo, $fragmento")
                    intent.putExtra("currentView", goTo)
                    intent.putExtra("fragToGo", fragmento)
                    startActivity(intent)
                }
            }
            holder.ignore.setOnClickListener {
                lifecycleScope.launch {
                    //acceptedOrRejected(consejo.idcon, 3)
                    goodbye(holder.itemView)
                }
            }
        }


        override fun getItemCount(): Int {
            Log.v("size de Consejossss", consejos.size.toString())
            return consejos.size
        }

        suspend fun acceptedOrRejected(
            id: Long,
            estado: Int,
        ) {
            withContext(Dispatchers.IO) {
                val conySugDao = Stlite.getInstance(requireContext()).getConySugDao()

                val nombre = conySugDao.getNombre(id.toInt())
                val contenido = conySugDao.getContenido(id.toInt())
                //val estado = conySugDao.getEstado(id.toInt())
                val flag = conySugDao.getFlag(id.toInt())
                val type = conySugDao.getType(id.toInt())
                val style = conySugDao.getStyle(id.toInt())
                val pressedSug = ConySug(
                    idcon = id,
                    nombre = nombre,
                    contenido = contenido,
                    estado = estado,
                    flag = flag,
                    type = type,
                    style = style
                )

                conySugDao.updateCon(pressedSug)
                val montos = conySugDao.getAllAcceptedCon()
                Log.i("ALL MONTOS", montos.toString())
            }
        }

        fun goodbye (viewToAnimate: View) {
            val bounceLeft = ObjectAnimator.ofFloat(viewToAnimate, "translationX", 0f, -100f)
            bounceLeft.duration = 100
            bounceLeft.interpolator = AccelerateDecelerateInterpolator()

            val bounceRight = ObjectAnimator.ofFloat(viewToAnimate, "translationX", -100f, 800f)
            bounceRight.duration = 500

            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(bounceLeft, bounceRight)
            animatorSet.start()

            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {}

                override fun onAnimationEnd(p0: Animator) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.finanzas_container, finanzasConySug()).addToBackStack(null)
                        .commit()
                }

                override fun onAnimationCancel(p0: Animator) {}

                override fun onAnimationRepeat(p0: Animator) {}
            })

            animatorSet.start()
        }
    }
}