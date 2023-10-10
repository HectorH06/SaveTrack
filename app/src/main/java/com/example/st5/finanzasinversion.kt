package com.example.st5

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.st5.databinding.FragmentFinanzasinversionBinding

class finanzasinversion : Fragment() {

    private lateinit var binding: FragmentFinanzasinversionBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val back = finanzasmain()

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.finanzas_container, back).addToBackStack(null).commit()
        }
    }
}