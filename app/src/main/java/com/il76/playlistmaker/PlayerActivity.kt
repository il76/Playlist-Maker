package com.il76.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {

    private var track = Track()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
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

        val ivPoster = findViewById<ImageView>(R.id.trackPoster)
        Glide.with(ivPoster)
            .load(track.getPoster(false))
            .placeholder(R.drawable.search_cover_placeholder)
            .centerInside()
            .transform(RoundedCorners(ivPoster.context.resources.getDimensionPixelSize(R.dimen.track_cover_border_radius_player)))
            .into(ivPoster)
        val tvName = findViewById<TextView>(R.id.trackName)
        tvName.text = track.trackName
        val tvArtist = findViewById<TextView>(R.id.artistName)
        tvArtist.text = track.artistName
        val tvTime = findViewById<TextView>(R.id.trackTime)
        tvTime.text = track.trackTime
//        val tvJson = findViewById<TextView>(R.id.jsonData)

//        tvJson.text =
//            "Трек: "+track.trackName + "\nИсполнитель: "+track.artistName + "\nВремя: " + track.trackTime +
//                    "\nОбложка: "+track.getPoster(false) + "\nid: "+track.trackId + "\nАльбом: " + track.collectionName +
//                    "\nГод: "+track.releaseDate + "\nЖанр: "+track.primaryGenreName + "\nСтрана: " + track.country


    }
}