package com.il76.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.il76.playlistmaker.databinding.ActivityNavBinding
import com.il76.playlistmaker.settings.ui.SettingsActivity
import com.il76.playlistmaker.media.ui.MediaActivity
import com.il76.playlistmaker.search.ui.SearchActivity

class NavActivity : AppCompatActivity() {

    private var _binding: ActivityNavBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityNavBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // анонимный класс
        val btnClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@NavActivity, SearchActivity::class.java)
                startActivity(intent)
            }
        }
        binding.buttonSearch.setOnClickListener(btnClickListener)

        // лямбда
        binding.buttonMedia.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }
        // открываем настройки
        binding.buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}