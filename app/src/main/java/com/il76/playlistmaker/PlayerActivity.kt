package com.il76.playlistmaker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.il76.playlistmaker.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private var _binding: ActivityPlayerBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")

    private var track = Track()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonBack = findViewById<MaterialToolbar>(R.id.activity_player_toolbar)
        buttonBack.setNavigationOnClickListener {
            this.finish()
        }

        val json = intent.getStringExtra("track")
        track = Gson().fromJson(json, Track::class.java)

        with(binding) {
            Glide.with(trackPoster)
                .load(track.getPoster(false))
                .placeholder(R.drawable.search_cover_placeholder)
                .centerInside()
                .transform(RoundedCorners(trackPoster.context.resources.getDimensionPixelSize(R.dimen.track_cover_border_radius_player)))
                .into(trackPoster)
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.getTime()
            trackCurrentTime.text = track.getTime()
            if (track.collectionName.isNotEmpty()) {
                trackCollection.text = track.collectionName
                groupCollection.isVisible = true
            } else {
                groupCollection.isVisible = false
            }
            trackYear.text = track.getReleaseYear()
            trackGenre.text = track.primaryGenreName
            trackCountry.text = track.country
        }
    }
}