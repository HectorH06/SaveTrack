package com.example.st5

import android.content.Context
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import com.example.st5.database.Stlite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*

class Decoder (context: Context) {
    private val cntxt = context

    fun format(value: Double): Double {
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(value).toDouble()
    }

    fun freq (freq: Int): String {
        val freqStr = when (freq) {
            0 -> "Única vez"
            1 -> "Diario"
            7 -> "Semanal"
            14 -> "Quincenal"
            30 -> "Mensual"
            61 -> "Bimestral"
            91 -> "Trimestral"
            122 -> "Cuatrimestral"
            183 -> "Semestral"
            365 -> "Anual"
            else -> "error"
        }
        return freqStr
    }

    suspend fun label (label: Int): String {
        var labelStr: String
        withContext(Dispatchers.IO) {
            val labelsDao = Stlite.getInstance(cntxt).getLabelsDao()
            labelStr = labelsDao.getPlabel(label)
        }
        return labelStr
    }

    fun date (date: Int): String {
        val datedate = when (date) {
            in 1..31 -> {
                val aux = "300012$date"
                val calendar = Calendar.getInstance()
                val formatoFecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val truefecha = formatoFecha.parse(aux)
                calendar.time = truefecha

                val now = LocalDate.now()
                val calendarToday = Calendar.getInstance()
                val today = formatoFecha.parse(now.toString())
                calendarToday.time = today

                var month = calendar.get(Calendar.MONTH)
                if (calendar.get(Calendar.DAY_OF_MONTH) > calendarToday.get(Calendar.DAY_OF_MONTH)) month++
                val mes = when (month) {
                    0 -> "Enero"
                    1 -> "Febrero"
                    2 -> "Marzo"
                    3 -> "Abril"
                    4 -> "Mayo"
                    5 -> "Junio"
                    6 -> "Julio"
                    7 -> "Agosto"
                    8 -> "Septiembre"
                    9 -> "Octubre"
                    10 -> "Noviembre"
                    11 -> "Diciembre"
                    else -> "cualquier mes"
                }

                "$date de $mes"
            }

            41 -> "Lunes"
            42 -> "Martes"
            43 -> "Miércoles"
            44 -> "Jueves"
            45 -> "Viernes"
            46 -> "Sábado"
            47 -> "Domingo"
            100 -> "Diario"

            in 10000000..30000001 -> {
                val formatoFecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val truefecha = formatoFecha.parse(date.toString())
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

                val yyyy = calendar.get(Calendar.YEAR)
                val mesesito = when (calendar.get(Calendar.MONTH)) {
                    0 -> "Enero"
                    1 -> "Febrero"
                    2 -> "Marzo"
                    3 -> "Abril"
                    4 -> "Mayo"
                    5 -> "Junio"
                    6 -> "Julio"
                    7 -> "Agosto"
                    8 -> "Septiembre"
                    9 -> "Octubre"
                    10 -> "Noviembre"
                    11 -> "Diciembre"
                    else -> "cualquier mes"
                }
                val semanita = when (dow) {
                    47 -> "Domingo"
                    41 -> "Lunes"
                    42 -> "Martes"
                    43 -> "Miércoles"
                    44 -> "Jueves"
                    45 -> "Viernes"
                    46 -> "Sábado"
                    else -> ""
                }

                "$semanita $dom de $mesesito del $yyyy"
            }
            else -> LocalDate.now().toString()
        }

        return datedate
    }
}