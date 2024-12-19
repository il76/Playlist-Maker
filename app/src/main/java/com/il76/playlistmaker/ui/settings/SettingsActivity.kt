package com.il76.playlistmaker.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.il76.playlistmaker.Creator
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.ActivitySettingsBinding
import com.il76.playlistmaker.domain.models.ThemeSettings

class SettingsActivity : AppCompatActivity() {

    private var _binding: ActivitySettingsBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")


    private val settingsInteractor = Creator.provideSettingsInteractor()

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

        binding.activitySettingsToolbar.setOnClickListener {
            this.finish()
        }


        binding.SettingsShareApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_text))
            intent.setType("text/plain")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.action_not_supported), Toast.LENGTH_SHORT).show()
            }
        }

        binding.SettingsWriteSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                // type = "*/*"
                setData (Uri.parse("mailto:")) //нам нужны только почтовые приложения
                putExtra(Intent.EXTRA_EMAIL, arrayOf("il@9111.ru"))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_email_text))
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, getString(R.string.action_not_supported), Toast.LENGTH_SHORT).show()
            }
        }

        binding.SettingsUserAgreement.setOnClickListener {
            val webpage: Uri = Uri.parse(getString(R.string.settings_ua_link))
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            // intent.resolveActivity(packageManager) != null // не работает?
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, getString(R.string.action_not_supported), Toast.LENGTH_SHORT).show()
            }
        }

        //обрабатываем переключатель темы
        binding.SettingsThemeSwitcher.isChecked = settingsInteractor.getThemeSettings().isDark
        binding.SettingsThemeSwitcher.setOnCheckedChangeListener {_, isChecked ->
            settingsInteractor.switchTheme(ThemeSettings(isChecked))
            settingsInteractor.saveThemeSettings(ThemeSettings(isChecked))
        }
    }

}