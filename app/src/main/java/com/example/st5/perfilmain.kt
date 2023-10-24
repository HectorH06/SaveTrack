package com.example.st5

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentPerfilmainBinding
import com.example.st5.models.Grupos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*

class perfilmain : Fragment() {
    private lateinit var binding: FragmentPerfilmainBinding

    private var isDarkMode = false
    private lateinit var grupos: List<Grupos>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAlarm()

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
        lifecycleScope.launch {
            isDarkMode = isDarkModeEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_perfil2)
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_perfil)
            }

            Log.i("MODO", isDarkMode.toString())
            val decoder = Decoder(requireContext())
            if (decoder.hayNet()) {
                procesarGrupos(requireContext())
            } else {
                Toast.makeText(requireContext(), "No hay acceso a internet", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
    private suspend fun gruposGet(): List<Grupos> {
        withContext(Dispatchers.IO) {
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            grupos = gruposDao.getAllGrupos()
            Log.i("ALL GRUPOS", grupos.toString())
        }
        return grupos
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            grupos = gruposGet()
            if (grupos.isNotEmpty()) {binding.displayGrupos.adapter = GruposDisplayAdapter(grupos)}
        }

        binding.Options.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.barrita)
        }

        binding.barrita.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.perfilEdit -> {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.perfil_container, perfileditar()).addToBackStack(null).commit()

                    true
                }
                R.id.grupos -> {
                    val decoder = Decoder(requireContext())
                    if (decoder.hayNet()) {
                        val intent = Intent(activity, GruposActivity::class.java)
                        intent.putExtra("isDarkMode", isDarkMode)
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "No hay acceso a internet", Toast.LENGTH_SHORT).show()
                    }

                    true
                }
                R.id.configuracion -> {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                        .replace(R.id.perfil_container, Configuracion()).addToBackStack(null).commit()

                    true
                }

                else -> false
            }
        }

        binding.BalanceTV.setOnClickListener {
            val intent = Intent(activity, Index::class.java)
            intent.putExtra("isDarkMode", !isDarkMode)
            intent.putExtra("currentView", 3)
        }

        lifecycleScope.launch {
            mostrarDatos()
        }
    }

    private suspend fun bajarfoto(link: String) {
        withContext(Dispatchers.IO) {
            binding.ProfilePicture.load(link) {
                crossfade(true)
                placeholder(R.drawable.ic_person)
                transformations(CircleCropTransformation())
                scale(Scale.FILL)
            }
        }
    }
    private suspend fun mostrarDatos() {
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
                append(chamba)
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
            if (diasaho > 0) {
                binding.ahorrando.setColorFilter(resources.getColor(R.color.O2), PorterDuff.Mode.SRC_IN)
            }
            if (diasaho > 14) {
                binding.ahorrando.setColorFilter(resources.getColor(R.color.B2), PorterDuff.Mode.SRC_IN)
            }
            if (diasaho > 60) {
                binding.ahorrando.setColorFilter(resources.getColor(R.color.P2), PorterDuff.Mode.SRC_IN)
            }

            val linkfoto = "http://savetrack.com.mx/images/$nombre.jpg"
            lifecycleScope.launch {
                bajarfoto(linkfoto)
            }
        }
    }

    private inner class GruposDisplayAdapter (private val grupos: List<Grupos>) :
        RecyclerView.Adapter<GruposDisplayAdapter.GrupoViewHolder>() {
        inner class GrupoViewHolder(
            itemView: View,
            val nombreTextView: TextView,
            val tipoImage: ImageView
        ) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_displaygrupos, parent, false)
            val nombreTextView = itemView.findViewById<TextView>(R.id.GNombre)
            val tipoImage = itemView.findViewById<ImageView>(R.id.GTipo)
            return GrupoViewHolder(
                itemView,
                nombreTextView,
                tipoImage
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
            val grupo = grupos[position]
            holder.itemView.setOnClickListener {
                val intent = Intent(activity, GruposActivity::class.java)
                intent.putExtra("isDarkMode", isDarkMode)
                startActivity(intent)
            }
            holder.nombreTextView.text = grupo.nameg
            holder.tipoImage.setBackgroundResource(when (grupo.type) {
                0 -> R.drawable.ic_pin
                1 -> R.drawable.ic_temporal
                else -> R.drawable.ic_delete
            })
            holder.itemView.setBackgroundColor(grupo.color)
            when (position){
                minOf(grupos.size, 3) -> {
                    holder.nombreTextView.text = "Todos los grupos"
                    holder.tipoImage.setBackgroundResource(R.drawable.ic_list)
                    holder.itemView.setBackgroundResource(R.drawable.p1bottomcell)
                    holder.tipoImage.setBackgroundResource(R.drawable.ic_grupos)
                    holder.itemView.setOnClickListener {
                        val intent = Intent(activity, GruposActivity::class.java)
                        intent.putExtra("isDarkMode", isDarkMode)
                        startActivity(intent)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return minOf(grupos.size, 4)
        }
    }

    private fun setupAlarm() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 3)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarma = Intent(requireContext(), AlarmaGrupos::class.java)
        val pendingAlarmaG = PendingIntent.getBroadcast(requireContext(), 0, alarma,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingAlarmaG
        )
    }

    private suspend fun procesarGrupos(context: Context) {
        withContext(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                val gruposDao = Stlite.getInstance(context).getGruposDao()
                val grupos = gruposDao.getAllGrupos()

                for ((Id, nameg, description, type, admin, idori, color, enlace) in grupos) {
                    if (Id != null && nameg != null) {
                        val viejoGrupo = Grupos(
                            Id = Id,
                            nameg = nameg,
                            description = description,
                            type = type,
                            admin = admin,
                            idori = idori,
                            color = color,
                            enlace = enlace
                        )

                        val grupoJson = withContext(Dispatchers.IO) { JSONObject(URL("http://savetrack.com.mx/grupoGet.php?localid=${viejoGrupo.idori}&admin=${viejoGrupo.admin}").readText()) }

                        if (grupoJson.getLong("idgrupoglobal") != null && grupoJson.getInt("tipo") != 2) {
                            val idoriA: Long = grupoJson.getLong("idgrupolocal")
                            val adminA: Long = grupoJson.getLong("idadmin")
                            val namegA: String = grupoJson.optString("nombre")
                            val descA: String = grupoJson.optString("descripcion")
                            val tipoA: Int = grupoJson.optInt("tipo")
                            val colorA: Int = grupoJson.optInt("color")

                            val grupoActualizado = Grupos(
                                Id = Id,
                                nameg = namegA,
                                description = descA,
                                type = tipoA,
                                admin = adminA,
                                idori = idoriA,
                                color = colorA,
                                enlace = enlace
                            )

                            gruposDao.updateGrupo(grupoActualizado)
                        } else {
                            Log.v("Current grupo", "VACÍO")
                        }
                        val grup = gruposDao.getAllGrupos()
                        Log.i("ALL GRUPOS ALARMA", grup.toString())
                    }
                }
            }
        }
    }
}