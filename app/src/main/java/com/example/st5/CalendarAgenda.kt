package com.example.st5

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.models.Eventos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*

@SuppressLint("ViewConstructor")
class CalendarAgenda @JvmOverloads constructor(
    context: Context,
    sectionNumber: Int,
    mode: Boolean,
    today: Int,
    year: Int,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private lateinit var eventos: List<Eventos>
    init {
        rowCount = 7
        columnCount = 7

        val textColor = if (mode) {
            resources.getColor(R.color.N5)
        } else {
            resources.getColor(R.color.N0)
        }

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            eventos = getEventos()
            Log.v("Los Eventos", "$eventos")

            val hoy = LocalDate.now()
            val month = hoy.monthValue - 1
            val calendar = Calendar.getInstance()
            val yearNow = hoy.year
            calendar.set(year, Calendar.JANUARY, 1)
            calendar.add(Calendar.MONTH, sectionNumber)

            val formatoFecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val firstDayOfMonth = formatoFecha.format(calendar.time)
            val truefecha = formatoFecha.parse(firstDayOfMonth)
            calendar.time = truefecha

            setPadding(16, 16, 16, 16)
            val columnWidth = 96
            for (i in 0 until columnCount) {
                for (j in 0 until rowCount) {
                    if (j == 0) {
                        val tv = TextView(context)
                        tv.setBackgroundResource(
                            when (i) {
                                0 -> R.drawable.p1leftcell
                                1, 2, 3, 4, 5 -> R.drawable.p1midcell
                                6 -> R.drawable.p1rightcell
                                else -> R.drawable.p1midcell
                            }
                        )
                        tv.text = when (i) {
                            0 -> "Do"
                            1 -> "Lu"
                            2 -> "Ma"
                            3 -> "Mi"
                            4 -> "Ju"
                            5 -> "Vi"
                            6 -> "Sa"
                            else -> "Z"
                        }

                        val params = LayoutParams()
                        params.width = columnWidth
                        params.height = 32
                        params.columnSpec = spec(i, 1f)
                        params.rowSpec = spec(j, 1f)
                        tv.layoutParams = params
                        tv.gravity = Gravity.CENTER
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                        tv.setTypeface(null, Typeface.BOLD)
                        tv.setTextColor(textColor)
                        addView(tv)
                    } else {
                        val cellLayout = ConstraintLayout(context)

                        val recycler = RecyclerView(context)
                        recycler.layoutManager = LinearLayoutManager(context)

                        recycler.id = View.generateViewId()
                        val tv = TextView(context)

                        val dayOfMonth = (j - 1) * 7 + i - calendar.get(Calendar.DAY_OF_WEEK) + 2
                        if (dayOfMonth in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                            tv.text = dayOfMonth.toString()
                            val fDay = String.format("%02d", dayOfMonth)
                            val cMonth = (sectionNumber + 2) % 12
                            val fMonth = String.format("%02d", cMonth)
                            val current = "$year$fMonth$fDay".toInt()
                            Log.v("CURRENT RECYCLER", "$current")
                            recycler.adapter = EventosAdapter(eventos, current)
                        } else {
                            tv.text = ""
                        }

                        if (dayOfMonth == today && sectionNumber % 12 == month && yearNow == year) {
                            cellLayout.setBackgroundResource(R.drawable.p1table)
                        }

                        val params = LayoutParams()
                        params.width = columnWidth
                        params.height = LayoutParams.WRAP_CONTENT
                        params.columnSpec = spec(i, 1f)
                        params.rowSpec = spec(j, 1f)
                        val paramsRecycler = LayoutParams()
                        paramsRecycler.width = columnWidth
                        paramsRecycler.height = LayoutParams.WRAP_CONTENT
                        paramsRecycler.columnSpec = spec(i, 1f)
                        paramsRecycler.rowSpec = spec(j, 1f)

                        tv.setTextColor(textColor)
                        tv.setPadding(8, 8, 0, 0)
                        recycler.setPadding(0, 32, 0, 0)

                        recycler.layoutParams = paramsRecycler

                        cellLayout.addView(tv)
                        cellLayout.addView(recycler)

                        cellLayout.layoutParams = params

                        addView(cellLayout)
                    }

                }
            }
        }
    }

    private suspend fun getEventos(): List<Eventos> {
        withContext(Dispatchers.IO) {
            val eventosDao = Stlite.getInstance(context).getEventosDao()
            eventos = eventosDao.getAllEventos()
        }
        return eventos
    }

    companion object {
        @JvmStatic
        fun newInstance(sectionNumber: Int, context: Context, komodo: Boolean, today: Int, actualPosition: Int): CalendarAgenda {
            return CalendarAgenda(context, sectionNumber, komodo, today, actualPosition)
        }
    }

    private inner class EventosAdapter(private val eventos: List<Eventos>, private val current: Int) :
        RecyclerView.Adapter<EventosAdapter.EventoViewHolder>() {
        inner class EventoViewHolder(
            itemView: View,
            val eventoTextView: TextView,
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_agenda, parent, false)
            val eventoTextView = itemView.findViewById<TextView>(R.id.nombreEvento)
            return EventoViewHolder(
                itemView,
                eventoTextView
            )
        }

        override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
            val eventosCumplen = eventos.filter { evento ->
                val formatoFecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val datedate = current
                val fs = "$datedate"
                val truefecha = formatoFecha.parse(fs)
                val tempo = Calendar.getInstance()
                tempo.time = truefecha

                val day = tempo.get(Calendar.DAY_OF_MONTH)
                val month = tempo.get(Calendar.MONTH)
                val fDay = String.format("%02d", day)
                val fMonth = String.format("%02d", month)
                val searchMonth = "5$fMonth$fDay".toInt()

                evento.fecha == day || evento.fecha == searchMonth || evento.fecha == current
            }

            if (eventosCumplen.isNotEmpty()) {
                val evento = eventosCumplen.getOrNull(position)
                if (evento != null) {
                    holder.eventoTextView.text = evento.nombre
                }
            }
        }

        override fun getItemCount(): Int {
            var count = 0
            for (evento in eventos) {
                val formatoFecha = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val datedate = current
                val fs = "$datedate"
                val truefecha = formatoFecha.parse(fs)
                val tempo = Calendar.getInstance()
                tempo.time = truefecha

                val day = tempo.get(Calendar.DAY_OF_MONTH)
                val month = tempo.get(Calendar.MONTH)
                val fDay = String.format("%02d", day)
                val fMonth = String.format("%02d", month)
                val searchMonth = "5$fMonth$fDay".toInt()
                Log.v("SEARCHING COUNT", "$day, $searchMonth, $current")

                if (evento.fecha == day || evento.fecha == searchMonth || evento.fecha == current) {
                    count++
                }

                if (count >= 3) {
                    break
                }
            }
            return count
        }
    }
}
