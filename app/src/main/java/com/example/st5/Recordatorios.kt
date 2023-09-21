package com.example.st5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.st5.database.Stlite
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
                recordar(context)
            }
        }
    }
    private suspend fun recordar(context: Context) {
        withContext(Dispatchers.IO) {
            val montoDao = Stlite.getInstance(context).getMontoDao()
            val eventosDao = Stlite.getInstance(context).getEventosDao()

            notificationHelper = notificationManager(context)
            decoder = Decoder(context)

            val hoy = LocalDate.now()
            val calendar = Calendar.getInstance()
            val year = hoy.year
            val month = hoy.monthValue
            val day = hoy.dayOfMonth
            calendar.set(year, month, day)

            val fDay = String.format("%02d", day)
            val fMonth = String.format("%02d", month)
            val datedate = "$year$fMonth$fDay"
            val today: Int = datedate.replace("-", "").toInt()
            val anual: Int = "5$fMonth$fDay".toInt()

            val futuro = hoy.plusDays(7)
            val calendarFuturo = Calendar.getInstance()
            val yearFuturo = futuro.year
            val monthFuturo = futuro.monthValue
            val dayFuturo = futuro.dayOfMonth
            calendarFuturo.set(yearFuturo, monthFuturo, dayFuturo)

            val fDayFuturo = String.format("%02d", dayFuturo)
            val fMonthFuturo = String.format("%02d", monthFuturo)
            val datedateFuturo = "$yearFuturo$fMonthFuturo$fDayFuturo"
            val tomorrow: Int = datedateFuturo.replace("-", "").toInt()
            val anualFuturo: Int = "5$fMonthFuturo$fDayFuturo".toInt()

            val addd: Int = today

            val montos = montoDao.getDelayed(addd)
            Log.v("TODAYS", "$today, $anual, $day, $tomorrow, $anualFuturo, $dayFuturo")
            val eventosList = eventosDao.getEventosX2Fechas(today, anual, day, tomorrow, anualFuturo, dayFuturo)

            for (monto in montos) {
                Log.v("notifmonto", monto.concepto)
                if (monto.delay >= 3) {
                    notificationHelper.sendNotification(
                        "Recordatorios",
                        R.drawable.ic_calendar,
                        "URGENTE",
                        "Has pospuesto demasiado el monto ${monto.concepto}",
                        1,
                        monto.idmonto,
                        monto.idmonto.toInt() + 10
                    )
                } else {
                    notificationHelper.sendNotification(
                        "Recordatorios",
                        R.drawable.ic_calendar,
                        monto.concepto,
                        "El monto ${monto.concepto} se paga hoy",
                        1,
                        monto.idmonto,
                        monto.idmonto.toInt() + 10
                    )
                }
            }
            for (eventos in eventosList) {
                Log.v("notifevento", eventos.nombre)
                if (eventos.estado == 0) {
                    if (eventosList.size == 1) {
                        notificationHelper.sendNotification(
                            "Eventos",
                            R.drawable.ic_calendar,
                            "Hoy hay un evento",
                            "${eventos.nombre} es hoy",
                            2,
                            eventos.idevento,
                            eventos.idevento.toInt() + 1000
                        )
                    } else {
                        notificationHelper.sendNotification(
                            "Eventos",
                            R.drawable.ic_calendar,
                            "Hoy hay ${eventosList.size} eventos",
                            "${eventos.nombre} es hoy",
                            2,
                            eventos.idevento,
                            eventos.idevento.toInt() + 1000
                        )
                    }
                }
            }
        }
    }
}