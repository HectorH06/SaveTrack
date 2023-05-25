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

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> containerPerfil.newInstance(position)
            1 -> historialmain.newInstance(position)
            2 -> containerIndex.newInstance(position)
            3 -> planesdeahorromain.newInstance(position)
            4 -> finanzasmain.newInstance(position)

            else -> containerIndex.newInstance(position)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getPageTitle(position: Int): CharSequence {
        val drawable: Drawable? = context.getDrawable(TAB_TITLES[position])
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