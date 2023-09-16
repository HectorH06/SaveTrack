package com.example.st5.calendar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.st5.CalendarAgenda

class MonthsPagerAdapter(private val context: Context, private val mode: Boolean, private val today: Int, private val actualPosition: Int) : PagerAdapter() {

    override fun getCount(): Int {
        return 600
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val realPosition = position % 12
        val calendarAgenda = CalendarAgenda.newInstance(realPosition, context, mode, today, actualPosition)
        container.addView(calendarAgenda)
        return calendarAgenda
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}