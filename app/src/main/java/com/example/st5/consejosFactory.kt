package com.example.st5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.st5.database.Stlite
import com.example.st5.models.ConySug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate

class consejosFactory : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        runBlocking {
            if (context != null) {
                creaConsejos(context)
            }
        }
    }
    private fun similarityCalculator(search: String, result: String): Double {
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
    private suspend fun creaConsejos(context: Context) {
        withContext(Dispatchers.IO) {
            val localDate = LocalDate.now()
            val day = localDate.dayOfMonth
            val fDay = String.format("%02d", day)
            val month = localDate.monthValue - 1
            val fMonth = String.format("%02d", month)
            val year = localDate.year
            val datedate = "$year$fMonth$fDay"
            val today: Int = datedate.replace("-", "").toInt()

            val assetsDao = Stlite.getInstance(context).getAssetsDao()
            val conySugDao = Stlite.getInstance(context).getConySugDao()
            val eventosDao = Stlite.getInstance(context).getEventosDao()
            val gruposDao = Stlite.getInstance(context).getGruposDao()
            val ingresosGastosDao = Stlite.getInstance(context).getIngresosGastosDao()
            val labelsDao = Stlite.getInstance(context).getLabelsDao()
            val montoDao = Stlite.getInstance(context).getMontoDao()
            val montoGrupoDao = Stlite.getInstance(context).getMontoGrupoDao()
            val usuarioDao = Stlite.getInstance(context).getUsuarioDao()

            val cs = hashMapOf(
                "C" to arrayOf(ConySug(), ConySug()),
                "E" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                //"G" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "I" to arrayOf(ConySug(), ConySug()),
                "L" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "M" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug(), ConySug()),
                "D" to arrayOf(ConySug(), ConySug(), ConySug()),
                "U" to arrayOf(ConySug(), ConySug(), ConySug(), ConySug(), ConySug())
            )

            // FLAGS -> 0: Único, 1: Repetible, 2: Múltiple
            // TYPES -> 1: Start, 2: Stop, 3: More, 4: Less, 5: Create, 6: Destroy, 7: Modify
            // STYLES -> 0: Green, 1: Yellow, 2: Orange, 3: Red
            val assetsNotifEnabled = assetsDao.getNotif() != 0

            val consejosAll = conySugDao.getAllCon()
            val consejosAllActive = conySugDao.getAllActiveCon()
            val consejosAllRejected = conySugDao.getAllRejectedCon()

            if (consejosAllActive == consejosAll) {cs["C"]?.set(0, ConySug(idcon = 100, nombre = "Se están ignorando los consejos", contenido = "Dales una oportunidad", estado = 1, flag = 1, type = 3, style = 0))}
            if (consejosAllRejected.size >= consejosAll.size/2) {cs["C"]?.set(1, ConySug(idcon = 101, nombre = "Se están rechazando demasiados consejos", contenido = "Dales una oportunidad", estado = 1, flag = 1, type = 1, style = 0))}

            val eventosAll = eventosDao.getAllEventos()
            val eventosAllUnabled = eventosDao.getAllUnabledEventos()
            val eventosMaxAddDate =  eventosDao.getMaxAddDate()

            if (eventosAll.size <= 3) {cs["E"]?.set(0, ConySug(idcon = 200, nombre = "Hay pocos eventos", contenido = "Dales una oportunidad", estado = 1, flag = 1, type = 3, style = 0))}
            if (eventosAll.size >= 30) {cs["E"]?.set(1, ConySug(idcon = 201, nombre = "Hay demasiados eventos", contenido = "Podrías tener problemas al visualizarlos", estado = 1, flag = 1, type = 4, style = 1))}
            if (eventosAllUnabled.size >= eventosAll.size/2) {cs["E"]?.set(2, ConySug(idcon = 202, nombre = "Pocos eventos tienen notificaciones", contenido = "Prueba activarlos para evitar olvidarlos", estado = 1, flag = 1, type = 7, style = 1))}
            if (!assetsNotifEnabled) {cs["E"]?.set(3, ConySug(idcon = 203, nombre = "No se te notificará de eventos", contenido = "Tienes las notificaciones desactivadas", estado = 1, flag = 0, type = 7, style = 1))}
            if (today - eventosMaxAddDate >= 1200) {cs["E"]?.set(4, ConySug(idcon = 204, nombre = "No se ha creado un evento en mucho tiempo", contenido = "Prueba a agregar alguno", estado = 1, flag = 2, type = 5, style = 0))}
            for (i in 0 until eventosAll.size - 1) {
                for (j in i + 1 until eventosAll.size) {
                    if (similarityCalculator(eventosAll[i].nombre, eventosAll[j].nombre) >= 85) {
                        cs["E"]?.set(6, ConySug(idcon = 206 + i.toLong(), nombre = "Dos eventos se llaman similar", contenido = "${eventosAll[i].nombre} y ${eventosAll[j].nombre}", estado = 1, flag = 2, type = 7, style = 1))
                        cs["E"]?.get(6)?.let { conySugDao.insertCon(it) }
                    }
                }
            }

            val ingresosTotales = ingresosGastosDao.checkSummaryI()
            val egresosTotales = ingresosGastosDao.checkSummaryG()

            if (ingresosTotales >= egresosTotales + (egresosTotales * .05)) {cs["I"]?.set(0, ConySug(idcon = 400, nombre = "Los egresos son similares a los ingresos", contenido = "Hay un margen de menos del 5%", estado = 1, flag = 1, type = 7, style = 2))}
            if (ingresosTotales < egresosTotales) {cs["I"]?.set(1, ConySug(idcon = 401, nombre = "Los egresos son superiores a los ingresos", contenido = "Es importante tomar medidas", estado = 1, flag = 1, type = 7, style = 3))}

            val labelsAll = labelsDao.getAllLabels()

            if (labelsAll.isEmpty()) {cs["L"]?.set(0, ConySug(idcon = 500, nombre = "No hay etiquetas", contenido = "Es necesario crear al menos una para insertar montos", estado = 1, flag = 1, type = 5, style = 2))}
            if (labelsAll.size <= 3) {cs["L"]?.set(1, ConySug(idcon = 501, nombre = "Hay pocas etiquetas", contenido = "Considera crear más", estado = 1, flag = 0, type = 5, style = 1))}
            if (labelsAll.size >= 25) {cs["L"]?.set(2, ConySug(idcon = 502, nombre = "Hay demasiadas etiquetas", contenido = "Considera eliminar algunas", estado = 1, flag = 0, type = 6, style = 1))}
            //if (CURRENT LABEL IS NOT BEING USED) {cs["L"]?.set(3, ConySug(idcon = 503, nombre = "Una etiqueta no se está usando", contenido = labelsAll[i].plabel, estado = 1, flag = 2, type = 6, style = 1))}
            for (i in 0 until labelsAll.size - 1) {
                for (j in i + 1 until labelsAll.size) {
                    if (similarityCalculator(labelsAll[i].plabel, labelsAll[j].plabel) >= 80) {
                        cs["L"]?.set(4, ConySug(idcon = 504 + i.toLong(), nombre = "Dos etiquetas se llaman similar", contenido = "${labelsAll[i].plabel} y ${labelsAll[j].plabel}", estado = 1, flag = 2, type = 7, style = 1))
                        cs["L"]?.get(4)?.let { conySugDao.insertCon(it) }
                    }
                }
            }
            for (i in 0 until labelsAll.size - 1) {
                for (j in i + 1 until labelsAll.size) {
                    if (similarityCalculator(labelsAll[i].color.toString(), labelsAll[j].color.toString()) >= 80) {
                        cs["L"]?.set(5, ConySug(idcon = 555 + i.toLong(), nombre = "Dos etiquetas tienen colores similares", contenido = "${labelsAll[i].plabel} y ${labelsAll[j].plabel}", estado = 1, flag = 2, type = 7, style = 1))
                        cs["L"]?.get(5)?.let { conySugDao.insertCon(it) }
                    }
                }
            }

            val montosAll = montoDao.getAllMontos()
            val montosDelayed = montoDao.getDelayed(today)

            if (montosAll.size <= 5) {cs["M"]?.set(3, ConySug(idcon = 603, nombre = "Hay pocos montos", contenido = "Considera registrar más", estado = 1, flag = 0, type = 3, style = 1))}
            if (montosAll.size >= 500) {cs["M"]?.set(4, ConySug(idcon = 604, nombre = "Hay demasiados montos", contenido = "Considera reducirlos", estado = 1, flag = 0, type = 2, style = 1))}
            for (i in 0 until montosAll.size - 1) {
                for (j in i + 1 until montosAll.size) {
                    if (similarityCalculator(montosAll[i].concepto, montosAll[j].concepto) >= 80) {
                        cs["M"]?.set(5, ConySug(idcon = 605 + i.toLong(), nombre = "Dos montos se llaman similar", contenido = "${montosAll[i].concepto} y ${montosAll[j].concepto}", estado = 1, flag = 2, type = 7, style = 1))
                        cs["M"]?.get(5)?.let { conySugDao.insertCon(it) }
                    }
                }
            }
            for (i in montosDelayed.indices) {
                cs["M"]?.set(6, ConySug(idcon = 656 + i.toLong(), nombre = "Un monto se está posponiendo demasiado", contenido = montosAll[i].concepto, estado = 1, flag = 2, type = 6, style = 2))
                cs["M"]?.get(6)?.let { conySugDao.insertCon(it) }
            }

            val deudasAll = montoDao.getDeudasList()

            if (deudasAll.size >= montosAll.size * .8) {cs["D"]?.set(0, ConySug(idcon = 700, nombre = "Demasiados montos son deudas", contenido = "Prueba a reducirlas", estado = 1, flag = 1, type = 4, style = 3))}
            for (i in 0 until minOf(maxOf(deudasAll.size - 1, 0), 40)) {
                if (deudasAll[i].delay >= 3) {
                    cs["D"]?.set(1, ConySug(idcon = 701 + i.toLong(), nombre = "Se está posponiendo demasiado una deuda", contenido = deudasAll[i].concepto, estado = 1, flag = 1, type = 6, style = 3))
                    cs["D"]?.get(1)?.let { conySugDao.insertCon(it) }
                }
            }
            for (i in 0 until minOf(maxOf(deudasAll.size - 1, 0), 40)) {
                if ((deudasAll[i].valorfinal ?: return@withContext) >= ingresosTotales/2) {
                    cs["D"]?.set(2, ConySug(idcon = 752 + i.toLong(), nombre = "La deuda ${deudasAll[i].concepto} es demasiado costosa", contenido = "Medidas deben ser tomadas", estado = 1, flag = 2, type = 2, style = 3))
                    cs["D"]?.get(2)?.let { conySugDao.insertCon(it) }
                }
            }

            val usuarioMeta = usuarioDao.checkMeta()

            if (usuarioMeta >= ingresosTotales - egresosTotales) {cs["U"]?.set(0, ConySug(idcon = 800, nombre = "La meta de ahorro supera los ingresos totales", contenido = "Prueba reducirla", estado = 1, flag = 1, type = 4, style = 1))}
            if (usuarioMeta <= (ingresosTotales - egresosTotales) * .1) {cs["U"]?.set(1, ConySug(idcon = 801, nombre = "La meta de ahorro es menor al 10% de los ingresos totales", contenido = "Prueba ahorrar más incrementándola", estado = 1, flag = 0, type = 3, style = 0))}

            for ((_, value) in cs) {
                for ((_, consejo) in value.withIndex()) {
                    val idcon = consejo.idcon
                    val tipo = consejo.type
                    val estadoActual = conySugDao.getEstado(idcon.toInt())

                    if ((tipo == 0 && estadoActual != 0) || (tipo == 1 && (estadoActual == 0 || estadoActual == 3))) {
                        conySugDao.insertCon(consejo)
                    }
                }
            }
        }
    }
}