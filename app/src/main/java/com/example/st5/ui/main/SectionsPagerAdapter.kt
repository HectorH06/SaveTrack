package com.example.st5.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.st5.*

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager, mode: Boolean,
                           private val containerToGo: Int, private var fragToGo: Int) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        if (containerToGo != position) {fragToGo = 0}
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
    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }

    override fun getCount(): Int {
        return 5
    }
}