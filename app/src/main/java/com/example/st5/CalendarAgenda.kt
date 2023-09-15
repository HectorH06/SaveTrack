package com.example.st5

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CalendarAgenda @JvmOverloads constructor(
    context: Context,
    sectionNumber: Int,
    mode: Boolean,
    today: Int,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {
    init {
        rowCount = 7
        columnCount = 7

        // TODO: declarar colores con la variable modo

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, today)
        calendar.set(Calendar.MONTH, sectionNumber)
        val firstDayOfWeek = 0

        setPadding(16, 16, 16, 16)
        val columnWidth = 0
        for (i in 0 until columnCount) {
            for (j in 0 until rowCount) {
                if (j == 0) {
                    val tv = TextView(context)
                    tv.setBackgroundResource(when (i) {
                        0 -> R.drawable.p1leftcell
                        1, 2, 3, 4, 5 -> R.drawable.p1midcell
                        6 -> R.drawable.p1rightcell
                        else -> R.drawable.p1midcell
                    })
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
                    params.width = LayoutParams.WRAP_CONTENT
                    params.height = 12
                    params.columnSpec = spec(i, 1f)
                    params.rowSpec = spec(j, 1f)
                    tv.layoutParams = params
                    tv.gravity = Gravity.CENTER
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                    tv.setTypeface(null, Typeface.BOLD)
                    addView(tv)
                } else {
                    val cellLayout = ConstraintLayout(context)

                    val recycler = RecyclerView(context)
                    recycler.id = View.generateViewId()
                    val tv = TextView(context)

                    val dayOfMonth = (j - 1) * 7 + i - firstDayOfWeek + 2
                    if (dayOfMonth in 1..31) {
                        tv.text = dayOfMonth.toString()
                    } else {
                        tv.text = ""
                    }

                    if (dayOfMonth == today) {
                        cellLayout.setBackgroundResource(R.drawable.p1table)
                    }

                    tv.setPadding(8, 0, 0, 0)

                    cellLayout.addView(tv)
                    cellLayout.addView(recycler)

                    val params = LayoutParams()
                    params.width = LayoutParams.WRAP_CONTENT
                    params.height = LayoutParams.WRAP_CONTENT
                    params.columnSpec = spec(i, 1f)
                    params.rowSpec = spec(j, 1f)
                    cellLayout.layoutParams = params

                    addView(cellLayout)
                }

            }
        }


    }

    companion object {
        @JvmStatic
        fun newInstance(sectionNumber: Int, context: Context, komodo: Boolean, today: Int): CalendarAgenda {
            return CalendarAgenda(context, sectionNumber, komodo, today)
        }
    }
}
