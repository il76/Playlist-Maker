package com.il76.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.il76.playlistmaker.databinding.FragmentSettingsBinding

class SettingsFragment: Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun render(state: SettingsState) {
        binding.SettingsThemeSwitcher.isChecked = state.isChecked
    }

    private fun showToast(additionalMessage: String) {
        //Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG).show()
    }

}