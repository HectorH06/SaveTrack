package com.example.st5.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.st5.*

private val TAB_TITLES = arrayOf(
    R.drawable.ic_person,
    R.drawable.ic_history,
    R.drawable.ic_index,
    R.drawable.ic_savingplans,
    R.drawable.ic_finance
)

private val TAB_TITLES_LIGHT = arrayOf(
    R.drawable.ic_personlight,
    R.drawable.ic_historylight,
    R.drawable.ic_indexlight,
    R.drawable.ic_savingplanslight,
    R.drawable.ic_financelight
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager, mode: Boolean, fragToGo: Int) :
    FragmentPagerAdapter(fm) {

    private val modo = mode
    private val fragToGo = fragToGo
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> containerPerfil.newInstance(position, fragToGo)
            1 -> containerHistorial.newInstance(position, fragToGo)
            2 -> containerIndex.newInstance(position, fragToGo)
            3 -> containerPlanesDeAhorro.newInstance(position, fragToGo)
            4 -> containerFinanzas.newInstance(position, fragToGo)

            else -> containerIndex.newInstance(position, 0)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getPageTitle(position: Int): CharSequence {
        var drawable: Drawable? = context.getDrawable(TAB_TITLES[position])
        if (!modo){
            drawable = context.getDrawable(TAB_TITLES_LIGHT[position])
        }


        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val spannable = SpannableStringBuilder(" ")
        spannable.setSpan(
            drawable?.let { ImageSpan(it) },
            0,
            1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    override fun getCount(): Int {
        return 5
    }
}