package com.example.st5.calendar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.st5.CalendarAgenda

class MonthsPagerAdapter(private val context: Context, private val mode: Boolean, private val today: Int) : PagerAdapter() {

    override fun getCount(): Int {
        return 12
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val calendarAgenda = when (position) {
            0 -> CalendarAgenda.newInstance(position, context, mode, today)
            1 -> CalendarAgenda.newInstance(position, context, mode, today)
            2 -> CalendarAgenda.newInstance(position, context, mode, today)
            3 -> CalendarAgenda.newInstance(position, context, mode, today)
            4 -> CalendarAgenda.newInstance(position, context, mode, today)
            5 -> CalendarAgenda.newInstance(position, context, mode, today)
            6 -> CalendarAgenda.newInstance(position, context, mode, today)
            7 -> CalendarAgenda.newInstance(position, context, mode, today)
            8 -> CalendarAgenda.newInstance(position, context, mode, today)
            9 -> CalendarAgenda.newInstance(position, context, mode, today)
            10 -> CalendarAgenda.newInstance(position, context, mode, today)
            11 -> CalendarAgenda.newInstance(position, context, mode, today)
            12 -> CalendarAgenda.newInstance(position, context, mode, today)
            else -> CalendarAgenda.newInstance(0, context, mode, today)
        }

        container.addView(calendarAgenda)
        return calendarAgenda
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}