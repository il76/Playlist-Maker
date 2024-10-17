package com.il76.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        val tvJson = findViewById<TextView>(R.id.jsonData)
        //val intent = intent
        val json = intent.getStringExtra("track")
//        Toast.makeText(this, json, Toast.LENGTH_SHORT).show()
//        tvJson.text = "adsgj s;lkejfg lk;szdgjf sdkljgls;kd jg;lkdsjg;lksdj g;lsdjg ;lkdsfg"
        tvJson.text = json
        track = Gson().fromJson(json, Track::class.java)
        tvJson.text =
            "Трек: "+track.trackName + "\nИсполнитель: "+track.artistName + "\nВремя: " + track.trackTime +
                    "\nОбложка: "+track.getPoster(false) + "\nid: "+track.trackId + "\nАльбом: " + track.collectionName +
                    "\nГод: "+track.releaseDate + "\nЖанр: "+track.primaryGenreName + "\nСтрана: " + track.country


    }
}