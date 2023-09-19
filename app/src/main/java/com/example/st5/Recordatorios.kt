package com.example.st5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.example.st5.database.Stlite
import com.example.st5.models.Monto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*

class Recordatorios : BroadcastReceiver() {
    private lateinit var notificationHelper: notificationManager
    private lateinit var decoder: Decoder
    override fun onReceive(context: Context?, intent: Intent?) {
        runBlocking {
            if (context != null) {
                procesarMontos(context)
            }
        }
    }

    private suspend fun procesarMontos(context: Context) {
        withContext(Dispatchers.IO) {
            val usuarioDao = Stlite.getInstance(context).getUsuarioDao()
            val montoDao = Stlite.getInstance(context).getMontoDao()
            val ingresoGastoDao = Stlite.getInstance(context).getIngresosGastosDao()
            val assetsDao = Stlite.getInstance(context).getAssetsDao()

            notificationHelper = notificationManager(context)
            decoder = Decoder(context)
            val fechaActual = LocalDate.now().toString()
            val today: Int = fechaActual.replace("-", "").toInt()
            val prev = assetsDao.getLastProcess()

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val truefecha = formatoFecha.parse(fechaActual)
            val calendar = Calendar.getInstance()
            calendar.time = truefecha

            val dom = calendar.get(Calendar.DAY_OF_MONTH)
            val w = calendar.get(Calendar.DAY_OF_WEEK)
            var dow = 100
            when (w) {
                1 -> dow = 47
                2 -> dow = 41
                3 -> dow = 42
                4 -> dow = 43
                5 -> dow = 44
                6 -> dow = 45
                7 -> dow = 46
            }

            val addd: Int = today

            Log.i("DOM", dom.toString())
            Log.i("DOW", dow.toString())

            Log.i("todayyyy", today.toString())
            Log.i("prevvvvv", prev.toString())

            if (ingresoGastoDao.checkSummaryI() - ingresoGastoDao.checkSummaryG() < usuarioDao.checkMeta()) {
                usuarioDao.updateDiasaho(usuarioDao.checkId(), 0L)
                notificationHelper.sendNotification("General", R.drawable.logo, "No tienes lana we", "Tienes ${decoder.format(usuarioDao.checkBalance())} pesos", 0, 0L)
            } else {
                usuarioDao.updateDiasaho(usuarioDao.checkId(), usuarioDao.checkDiasaho() + 1L)
            }

            val montos = montoDao.getMontoXFecha(today, dom, dow, 100, addd)

            if (prev < today) {
                for (monto in montos) {
                    if (monto.cooldown == 0) {
                        val totalIngresos = ingresoGastoDao.checkSummaryI()

                        Log.i("MONTO PROCESADO", monto.toString())
                        val weekMonto = monto.fecha
                        Log.v("wek", weekMonto.toString())

                        if (monto.etiqueta > 10000) {
                            ingresoGastoDao.updateSummaryI(monto.iduser.toInt(), totalIngresos + monto.valor)
                            monto.veces = monto.veces?.plus(1)
                            monto.sequence = monto.sequence + "1."
                            montoDao.updateMonto(monto)
                        } else {
                            var status = 0
                            var cooldown = 0
                            val sequence = monto.sequence + "0."
                            when (monto.estado) {
                                1 -> status = 0
                                4 -> status = 3
                                6 -> status = 5
                                9 -> status = 8
                            }
                            when (monto.frecuencia) {
                                14 -> cooldown = 1
                                61 -> cooldown = 1
                                91 -> cooldown = 2
                                122 -> cooldown = 3
                                183 -> cooldown = 5
                                365 -> cooldown = 11
                            }
                            val toCheckMonto = Monto(
                                idmonto = monto.idmonto,
                                iduser = monto.iduser,
                                concepto = monto.concepto,
                                valor = monto.valor,
                                valorfinal = monto.valorfinal,
                                fecha = monto.fecha,
                                frecuencia = monto.frecuencia,
                                etiqueta = monto.etiqueta,
                                interes = monto.interes,
                                tipointeres = monto.tipointeres,
                                veces = monto.veces,
                                estado = status,
                                adddate = monto.adddate,
                                enddate = monto.enddate,
                                cooldown = cooldown,
                                delay = monto.delay,
                                sequence = sequence
                            )
                            montoDao.updateMonto(toCheckMonto)
                        }
                    } else {
                        val newcool = monto.cooldown + 1

                        val toMeltMonto = Monto(
                            idmonto = monto.idmonto,
                            iduser = monto.iduser,
                            concepto = monto.concepto,
                            valor = monto.valor,
                            valorfinal = monto.valorfinal,
                            fecha = monto.fecha,
                            frecuencia = monto.frecuencia,
                            etiqueta = monto.etiqueta,
                            interes = monto.interes,
                            tipointeres = monto.tipointeres,
                            veces = monto.veces,
                            estado = monto.estado,
                            adddate = monto.adddate,
                            enddate = monto.enddate,
                            cooldown = newcool,
                            delay = monto.delay,
                            sequence = monto.sequence
                        )
                        montoDao.updateMonto(toMeltMonto)
                    }
                }
            }
            assetsDao.updateLastprocess(today)
        }
    }
}