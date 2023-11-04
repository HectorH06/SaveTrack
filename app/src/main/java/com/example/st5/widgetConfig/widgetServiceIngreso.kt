package com.example.st5.widgetConfig

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.example.st5.Decoder
import com.example.st5.database.Stlite
import com.example.st5.models.Monto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep

class widgetServiceIngreso : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_INCREMENT) {
            val montoId = getMontoId(intent)
            Log.v("widget IDM", "$montoId")
            val appWidgetManager = AppWidgetManager.getInstance(this)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(this, widgetProviderIngreso::class.java)
            )
            for (appWidgetId in appWidgetIds) {
                incrementCount(this, appWidgetId, montoId)
                widgetProviderIngreso.updateAppWidget(this, appWidgetManager, appWidgetId, montoId)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private const val ACTION_INCREMENT = "com.example.st5.INCREMENT"

        fun newIncrementIntent(context: Context, montoId: Long): PendingIntent {
            val intent = Intent(context, widgetServiceIngreso::class.java)
            intent.action = ACTION_INCREMENT
            intent.putExtra("MONTO_ID", montoId)
            Log.v("widget IDM", "$montoId")
            return PendingIntent.getService(
                context,
                1000,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        fun getVeces(context: Context, appWidgetId: Int, montoId: Long): Int {
            var veces = 0L
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val montoDao = Stlite.getInstance(context).getMontoDao()
                    veces = montoDao.getVeces(montoId.toInt()) + 1
                }
            }
            sleep(300)
            return veces.toInt()
        }
        fun getConcepto(context: Context, appWidgetId: Int, montoId: Long): String {
            var concepto = "Concepto"
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val montoDao = Stlite.getInstance(context).getMontoDao()
                    concepto = montoDao.getConcepto(montoId.toInt())
                }
            }
            sleep(300)
            return concepto
        }
        fun getValor(context: Context, appWidgetId: Int, montoId: Long): String {
            var valor = 0.0
            val decoder = Decoder(context)
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val montoDao = Stlite.getInstance(context).getMontoDao()
                    valor = montoDao.getValor(montoId.toInt())
                }
            }
            sleep(300)
            return "$" + decoder.format(valor)
        }

        fun incrementCount(context: Context, appWidgetId: Int, montoId: Long) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    if (montoId != null && montoId != -1L) {
                        val montoDao = Stlite.getInstance(context).getMontoDao()
                        val usuarioDao = Stlite.getInstance(context).getUsuarioDao()
                        val ingresoGastoDao = Stlite.getInstance(context).getIngresosGastosDao()

                        val veces = montoDao.getVeces(montoId.toInt())
                        val concepto = montoDao.getConcepto(montoId.toInt())
                        val valor = montoDao.getValor(montoId.toInt())
                        val fecha = montoDao.getFecha(montoId.toInt())
                        val frecuencia = montoDao.getFecha(montoId.toInt())
                        val etiqueta = montoDao.getEtiqueta(montoId.toInt())
                        val interes = montoDao.getInteres(montoId.toInt())
                        val adddate = montoDao.getAdded(montoId.toInt())
                        var nv: Long? = 1
                        if (veces != null)
                            nv = veces + 1

                        val sequence = montoDao.getSequence(montoId.toInt())
                        val values =
                            sequence.trim('.').split('.').map { it.toInt() }.toMutableList()
                        if (values.isNotEmpty()) {
                            val lastIndex = values.size - 1
                            values[lastIndex] += 1
                        }
                        val updatedString = values.joinToString(".")
                        val result = "$updatedString."

                        val enddate = montoDao.getEnded(montoId.toInt())
                        val valorfinal = montoDao.getValorFinal(montoId.toInt())
                        val tipointeres = montoDao.getTipoInteres(montoId.toInt())
                        val delay = montoDao.getDelay(montoId.toInt())
                        val cooldown = montoDao.getCooldown(montoId.toInt())
                        val iduser = usuarioDao.checkId().toLong()
                        val montoPresionado = Monto(
                            idmonto = montoId,
                            iduser = iduser,
                            concepto = concepto,
                            valor = valor,
                            valorfinal = valorfinal,
                            fecha = fecha,
                            frecuencia = frecuencia,
                            etiqueta = etiqueta,
                            interes = interes,
                            tipointeres = tipointeres,
                            veces = nv,
                            adddate = adddate,
                            enddate = enddate,
                            cooldown = cooldown,
                            delay = delay,
                            sequence = result
                        )

                        val totalIngresos = ingresoGastoDao.checkSummaryI()

                        ingresoGastoDao.updateSummaryI(
                            montoPresionado.iduser.toInt(),
                            totalIngresos + montoPresionado.valor
                        )
                        montoDao.updateMonto(montoPresionado)
                        val montos = montoDao.getMonto()
                        Log.i("ALL MONTOS", montos.toString())
                    }
                }
            }
        }

        private fun getMontoId(intent: Intent): Long {
            Log.v("widget IDM", "${intent.getLongExtra("MONTO_ID", -1)}")
            return intent.getLongExtra("MONTO_ID", -1)
        }
    }
}
