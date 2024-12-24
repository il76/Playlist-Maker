package com.il76.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.il76.playlistmaker.databinding.ActivitySettingsBinding
import com.il76.playlistmaker.settings.ui.SettingsViewModel.Companion.getViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private var _binding: ActivitySettingsBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.activitySettings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this, getViewModelFactory())[SettingsViewModel::class.java]
        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeShowToast().observe(this) { toast ->
            showToast(toast)
        }


        binding.activitySettingsToolbar.setOnClickListener {
            this.finish()
        }

        binding.SettingsShareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.SettingsWriteSupport.setOnClickListener {
            viewModel.writeSupport()
        }

        binding.SettingsUserAgreement.setOnClickListener {
            viewModel.openTOS()
        }

        //обрабатываем переключатель темы
        binding.SettingsThemeSwitcher.setOnCheckedChangeListener {_, isChecked ->
            viewModel.switchTheme(isChecked)
        }
    }

    private fun render(state: SettingsState) {
        binding.SettingsThemeSwitcher.isChecked = state.isChecked
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG).show()
    }

}