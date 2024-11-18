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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.il76.playlistmaker.application.App
import com.il76.playlistmaker.application.App.Companion.DARK_THEME_ENABLED
import com.il76.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonBack = findViewById<MaterialToolbar>(R.id.activity_settings_toolbar)
        buttonBack.setOnClickListener {
            this.finish()
        }


        val buttonShare = findViewById<MaterialTextView>(R.id.SettingsShareApp)
        buttonShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_text))
            intent.setType("text/plain")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.action_not_supported), Toast.LENGTH_SHORT).show()
            }
        }

        val buttonWrite = findViewById<MaterialTextView>(R.id.SettingsWriteSupport)
        buttonWrite.setOnClickListener {
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

        val buttonUA = findViewById<MaterialTextView>(R.id.SettingsUserAgreement)
        buttonUA.setOnClickListener {
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
        val switcher = findViewById<SwitchMaterial>(R.id.SettingsThemeSwitcher)
        switcher.isChecked = (applicationContext as App).darkTheme
        switcher.setOnCheckedChangeListener {_, isChecked ->
            with((applicationContext as App)) {
                switchTheme(isChecked)
                sharedPrefs.edit().putBoolean(DARK_THEME_ENABLED, isChecked).apply()
            }
        }
    }

}