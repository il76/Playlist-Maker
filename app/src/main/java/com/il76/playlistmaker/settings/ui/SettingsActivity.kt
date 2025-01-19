package com.il76.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentSettingsBinding
import com.il76.playlistmaker.sharing.data.EmailData
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentSettings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeShowToast().observe(this) { toast ->
            showToast(toast)
        }


//        binding.activitySettingsToolbar.setOnClickListener {
//            this.finish()
//        }

        binding.SettingsShareApp.setOnClickListener {
            viewModel.shareApp(applicationContext.getString(R.string.share_app_text))
        }

        binding.SettingsWriteSupport.setOnClickListener {
            viewModel.writeSupport(
                EmailData(
                    title = applicationContext.getString(R.string.settings_email_subject),
                    subject = applicationContext.getString(R.string.settings_email_subject),
                    sender = applicationContext.getString(R.string.settings_email_from),
                    recipient = applicationContext.getString(R.string.settings_email_recipient),
                )
            )
        }

        binding.SettingsUserAgreement.setOnClickListener {
            viewModel.openTOS(applicationContext.getString(R.string.settings_ua_link))
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