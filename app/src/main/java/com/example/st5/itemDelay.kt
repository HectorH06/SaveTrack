package com.example.st5

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.st5.database.Stlite
import com.example.st5.models.Eventos
import com.example.st5.models.Monto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class itemDelay : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val typee = intent?.getIntExtra("type", 0)
        val idd = intent?.getLongExtra("idd", 0)
        val notificationId = intent?.getIntExtra("notif", 0)
        Log.v("BROADCAST", "$typee, $idd, $notificationId")
        Toast.makeText(context, "Se pospuso", Toast.LENGTH_SHORT).show()
        val scope = CoroutineScope(Dispatchers.Main)

        if (notificationId != null) {
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }

        if (context != null) {
            scope.launch {
                when (typee) {
                    1 -> { // Es monto
                        if (idd != null) {
                            delayM(idd.toLong(), context)
                        }
                    }
                    2 -> { // Es evento
                        if (idd != null) {
                            delayE(idd.toLong(), context)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private suspend fun delayM(
        id: Long,
        context: Context
    ) {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(context).getMontoDao()
            val usuarioDao = Stlite.getInstance(context).getUsuarioDao()

            val estado = montoDao.getEstado(id.toInt())
            val frecuencia = montoDao.getFrecuencia(id.toInt())
            val concepto = montoDao.getConcepto(id.toInt())
            val valor = montoDao.getValor(id.toInt())
            val fecha = montoDao.getFecha(id.toInt())
            val etiqueta = montoDao.getEtiqueta(id.toInt())
            val veces = montoDao.getVeces(id.toInt())
            val interes = montoDao.getInteres(id.toInt())
            val adddate = montoDao.getAdded(id.toInt())

            var status = estado
            when (estado) {
                0 -> status = 1
                3 -> status = 4
                5 -> status = 6
                8 -> status = 9
            }
            var cooldown = 0
            when (frecuencia) {
                14 -> cooldown = 1
                61 -> cooldown = 1
                91 -> cooldown = 2
                122 -> cooldown = 3
                183 -> cooldown = 5
                365 -> cooldown = 11
            }
            val delay = montoDao.getDelay(id.toInt()) + 1
            val sequence = montoDao.getSequence(id.toInt())
            val tipointeres = montoDao.getTipoInteres(id.toInt())
            val valorfinal = montoDao.getValorFinal(id.toInt())
            val enddate = montoDao.getEnded(id.toInt())
            val iduser = usuarioDao.checkId().toLong()
            val montoPresionado = Monto(
                idmonto = id,
                iduser = iduser,
                concepto = concepto,
                valor = valor,
                valorfinal = valorfinal,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                interes = interes,
                tipointeres = tipointeres,
                veces = veces,
                estado = status,
                adddate = adddate,
                enddate = enddate,
                delay = delay,
                sequence = sequence,
                cooldown = cooldown
            )

            montoDao.updateMonto(montoPresionado)
            val montos = montoDao.getMonto()
            Log.i("ALL MONTOS", montos.toString())
        }
    }
    private suspend fun delayE(
        id: Long,
        context: Context
    ) {
        withContext(Dispatchers.IO) {
            val eventosDao = Stlite.getInstance(context).getEventosDao()

            val nombre = eventosDao.getNombre(id.toInt())
            val fecha = eventosDao.getFecha(id.toInt())
            val frecuencia = eventosDao.getFecha(id.toInt())
            val etiqueta = eventosDao.getEtiqueta(id.toInt())
            val adddate = eventosDao.getAddDate(id.toInt())
            val updateEvento = Eventos(
                idevento = id,
                nombre = nombre,
                fecha = fecha,
                frecuencia = frecuencia,
                etiqueta = etiqueta,
                estado = 1,
                adddate = adddate
            )

            eventosDao.updateEvento(updateEvento)
            val eventos = eventosDao.getAllEventos()
            Log.i("ALL EVENTOS", "$eventos")
        }
    }
}
