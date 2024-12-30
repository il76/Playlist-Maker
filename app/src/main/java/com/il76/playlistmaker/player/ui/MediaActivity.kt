package com.il76.playlistmaker.player.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.il76.playlistmaker.databinding.ActivityMediaBinding

class MediaActivity : AppCompatActivity() {

    private var _binding: ActivityMediaBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.activityMedia) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.activityMediaToolbar.setNavigationOnClickListener {
            this.finish()
        }

    }
}