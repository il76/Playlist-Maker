package com.il76.playlistmaker.media.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentMediaBinding

class MediaActivity : AppCompatActivity() {

    private var _binding: FragmentMediaBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = FragmentMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentMedia) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mediaViewPager.adapter = MediaPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.mediaTabLayout, binding.mediaViewPager) { tab, position ->
            when(position) {
                0 -> tab.text = applicationContext.getString(R.string.media_tab_faforite_tracks)
                1 -> tab.text = applicationContext.getString(R.string.media_tab_playlists)
            }
        }
        tabMediator.attach()

        binding.activityMediaToolbar.setNavigationOnClickListener {
            this.finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}