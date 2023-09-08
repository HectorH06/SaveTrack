package com.example.st5

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentConfiguracionBinding
import com.polyak.iconswitch.IconSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Configuracion : Fragment() {
    private lateinit var binding: FragmentConfiguracionBinding

    private var isDarkMode = false
    private var notifActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            isDarkMode = isDarkModeEnabled(requireContext())
            notifActive = areNotifEnabled(requireContext())

            if (isDarkMode) {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
                binding.claroscuro.checked = IconSwitch.Checked.RIGHT
            } else {
                binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
                binding.claroscuro.checked = IconSwitch.Checked.LEFT
                isDarkMode = true
            }
            if (notifActive) {
                binding.notificame.checked = IconSwitch.Checked.LEFT
            } else {
                binding.notificame.checked = IconSwitch.Checked.RIGHT
            }

            Log.i("MODO", isDarkMode.toString())
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(activity, Index::class.java)
                    intent.putExtra("isDarkMode", !isDarkMode)
                    startActivity(intent)
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

    private suspend fun areNotifEnabled(context: Context): Boolean {
        var modo: Boolean
        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(context).getAssetsDao()
            val mode = assetsDao.getNotif()
            modo = mode != 0
        }
        return modo
    }

    private suspend fun updateTheme(context: Context, komodo: Long){
        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(context).getAssetsDao()

            assetsDao.updateTheme(komodo)
        }
    }

    private suspend fun updateNotif(context: Context, modo: Long){
        withContext(Dispatchers.IO){
            val assetsDao = Stlite.getInstance(context).getAssetsDao()

            assetsDao.updateNotif(modo)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfiguracionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goback.setOnClickListener {
            val intent = Intent(activity, Index::class.java)
            intent.putExtra("isDarkMode", !isDarkMode)
            startActivity(intent)
        }

        binding.claroscuro.setCheckedChangeListener {
            when (binding.claroscuro.checked) {
                IconSwitch.Checked.LEFT -> {
                    binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas)
                    lifecycleScope.launch{
                        updateTheme(requireContext(), 0)
                    }
                    isDarkMode = true
                }
                IconSwitch.Checked.RIGHT -> {
                    binding.background.setBackgroundResource(R.drawable.gradient_background_finanzas2)
                    lifecycleScope.launch{
                        updateTheme(requireContext(), 1)
                    }
                    isDarkMode = false
                }
                else -> {}
            }
        }

        binding.notificame.setCheckedChangeListener {
            when (binding.notificame.checked) {
                IconSwitch.Checked.LEFT -> {
                    lifecycleScope.launch{
                        updateNotif(requireContext(), 1)
                    }
                    notifActive = true
                }
                IconSwitch.Checked.RIGHT -> {
                    lifecycleScope.launch{
                        updateNotif(requireContext(), 0)
                    }
                    notifActive = false
                }
                else -> {}
            }
        }

        binding.manu.setOnClickListener {
            manu()
        }
    }

    private fun manu() {
        val pdfUrl = "http://savetrack.com.mx/Manu.pdf"
        val request = DownloadManager.Request(Uri.parse(pdfUrl))
        request.setTitle("Descarga de Manual para el Usuario")
        request.setDescription("Descargando archivo PDF")
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ManualDeUsuarioSavetrack07092023.pdf")
        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(pdfUrl)
        startActivity(intent)
    }
}