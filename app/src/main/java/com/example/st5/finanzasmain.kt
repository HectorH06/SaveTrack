package com.example.st5

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.net.Uri
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
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentFinanzasmainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException


class finanzasmain : Fragment() {
    private lateinit var binding: FragmentFinanzasmainBinding
    private val decimalFormat = DecimalFormat("#.##")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkMode = isDarkModeEnabled(requireContext())

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
        binding = FragmentFinanzasmainBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            mostrarProductos()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, Configuracion()).addToBackStack(null).commit()
        }

        binding.FinanzasConsejosySugerencias.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, finanzasConySug()).addToBackStack(null).commit()
        }

        binding.Economia.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, finanzasstatsahorro()).addToBackStack(null).commit()
        }

        binding.FinanzasCalendar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, finanzasEventos()).addToBackStack(null).commit()
        }
    }

    private fun bajarfoto(link: String) {
        binding.FinanzasItemImage.load(link) {
            crossfade(true)
            placeholder(R.drawable.ic_notfound)
            scale(Scale.FILL)
        }
    }
    @SuppressLint("SetTextI18n")
    private suspend fun mostrarProductos() {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val conceptos = montoDao.getTopConceptos()
            val precios = montoDao.getTopValor()
            Log.v("CONCEPTOS", conceptos.toString())
            if (conceptos.size >= 3) {
                for (i in 0 until 3) {

                    val durl = "https://api.mercadolibre.com/sites/MLM/search?q=${conceptos[i]}"
                    val queue: RequestQueue = Volley.newRequestQueue(requireContext())
                    val elproducto = JsonObjectRequest(
                        Request.Method.GET, durl, null,
                        { response ->
                            try {
                                val results = response.getJSONArray("results")
                                if (results.length() > 0) {
                                    val firstResult = results.getJSONObject(0)
                                    val id: String = firstResult.getString("id")
                                    val title: String = firstResult.getString("title")
                                    val price: Double = firstResult.getDouble("price")
                                    val permalink: String = firstResult.getString("permalink")
                                    val thumbnail: String = firstResult.getString("thumbnail")

                                    Log.v(
                                        "PRODUCT DATA",
                                        "$id, $title, $price, $permalink, $thumbnail"
                                    )
                                    Item.ItemsRepository.add(
                                        Item(
                                            id,
                                            title,
                                            price,
                                            permalink,
                                            thumbnail
                                        )
                                    )
                                } else {
                                    binding.FinanzasItemImage.setBackgroundResource(R.drawable.ic_notfound)
                                }
                            } catch (e: JSONException) {
                                binding.FinanzasItemImage.setBackgroundResource(R.drawable.ic_notnet)
                            }
                        },
                        { error ->
                            binding.FinanzasItemImage.setBackgroundResource(R.drawable.ic_notnet)
                            Log.e("error => $error", "SIE API ERROR")
                        }
                    )
                    queue.add(elproducto)
                }
            } else {
                binding.FinanzasItemImage.setBackgroundResource(R.drawable.ic_notfound)
            }

            val query = Item.ItemsRepository.getAll()
            val ids: List<String> = query.map { it.id }
            val titles: List<String> = query.map { it.title }
            val prices: List<Double> = query.map { it.price }
            val links: List<String> = query.map { it.permalink }
            val images: List<String> = query.map { it.thumb }

            var i = 1
            if (i <= images.size) {
                val afinity = afinityCalculator(conceptos[i], titles[i])
                val saving = saveCalculator(precios[i], prices[i])
                val fAfinity = decimalFormat.format(afinity)
                val fSaving = decimalFormat.format(saving)
                val pic = images[i]
                Log.v("PIC", adaptLink(pic))
                lifecycleScope.launch {
                    bajarfoto(adaptLink(pic))
                }
                binding.prodhint.text = titles[i]
                binding.afinity.text = "$fAfinity%"
                binding.saving.text = "$fSaving%"

                Log.v(
                    "PRODUCT DATA",
                    "${ids[i]}, ${titles[i]}, ${prices[i]}, ${links[i]}, ${images[i]}"
                )

                binding.VerMasMERCALIBRE.setOnClickListener {
                    val url = links[i]
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }

                binding.VerMenosMERCALIBRE.setOnClickListener {
                    lifecycleScope.launch {
                        Item.ItemsRepository.remove(ids[i])
                        delay(500)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.finanzas_container, finanzasmain()).addToBackStack(null)
                            .commit()
                    }
                }

                binding.NoVerMERCALIBRE.setOnClickListener {
                    lifecycleScope.launch {
                        Item.ItemsRepository.remove(ids[i])
                        delay(500)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.finanzas_container, finanzasmain()).addToBackStack(null)
                            .commit()
                    }
                }
            }
        }
    }

    private fun adaptLink(link: String): String {
        return if (link.endsWith(".jpg", ignoreCase = true)) {
            link.replace(".jpg", ".webp", ignoreCase = true)
            link.replace("http://", "https://")
        } else {
            link
        }
    }

    private fun afinityCalculator(search: String, result: String): Double {
        val len1 = search.length
        val len2 = result.length
        val matrix = Array(len1 + 1) { IntArray(len2 + 1) }

        for (i in 0..len1) {
            matrix[i][0] = i
        }

        for (j in 0..len2) {
            matrix[0][j] = j
        }

        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (search[i - 1] == result[j - 1]) 0 else 1
                matrix[i][j] = minOf(
                    matrix[i - 1][j] + 1,
                    matrix[i][j - 1] + 1,
                    matrix[i - 1][j - 1] + cost
                )
            }
        }

        val maxLen = maxOf(len1, len2)
        val similarity = 1.0 - matrix[len1][len2].toDouble() / maxLen.toDouble()
        return similarity * 100
    }
    private fun saveCalculator(precio: Double, oferta: Double): Double {
        val ahorro = precio - oferta

        return (ahorro / precio) * 100.0
    }
    data class Item(
        val id: String,
        val title: String,
        val price: Double,
        val permalink: String,
        val thumb: String
    ) {
        object ItemsRepository {
            private val data: LinkedHashMap<String, Item> = linkedMapOf()

            fun add(newItem: Item) {
                data[newItem.id] = newItem
            }

            fun remove(itemId: String) = data.remove(itemId)

            fun getAll() = data.values.toList()
        }
    }
}
