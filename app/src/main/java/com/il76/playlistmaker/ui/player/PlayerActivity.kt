package com.il76.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.il76.playlistmaker.Creator
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.ActivityPlayerBinding
import com.il76.playlistmaker.domain.models.Track

class PlayerActivity : AppCompatActivity() {

    private var _binding: ActivityPlayerBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")

    private val playerInteractor = Creator.provideMediaPlayerInteractor(MediaPlayer())

    /**
     * Данные о треке, прилетают с экрана поиска
     */
    private var track = Track()


    /**
     * Поставлен ли лайк
     */
    private var isLiked = false

    /**
     * Добавлено ли в плейлист
     */
    private var isPlaylisted = false

    /**
     * Текущее состояние плеера
     */
    private var playerState = STATE_DEFAULT

    val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.activityPlayer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.activityPlayerToolbar.setNavigationOnClickListener {
            this.finish()
        }

        fillTrackInfo()

        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }
        binding.buttonPlaylistAdd.setOnClickListener {
            if (isPlaylisted) {
                binding.buttonPlaylistAdd.setImageResource(R.drawable.icon_playlist_add)
            } else {
                binding.buttonPlaylistAdd.setImageResource(R.drawable.icon_playlist_add_active)
            }
            isPlaylisted = !isPlaylisted
        }
        binding.buttonLike.setOnClickListener {
            if (isLiked) {
                binding.buttonLike.setImageResource(R.drawable.icon_like)
            } else {
                binding.buttonLike.setImageResource(R.drawable.icon_like_active)
            }
            isLiked = !isLiked
        }

        preparePlayer()
    }

    /**
     * Заполняем вью информацией о выбранном треке
     */
    private fun fillTrackInfo() {
        val json = intent.getStringExtra("track")
        track = Creator.provideGson().fromJson(json, Track::class.java)

        with(binding) {
            Glide.with(trackPoster)
                .load(track.poster)
                .placeholder(R.drawable.search_cover_placeholder)
                .centerInside()
                .transform(RoundedCorners(trackPoster.context.resources.getDimensionPixelSize(R.dimen.track_cover_border_radius_player)))
                .into(trackPoster)
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime
            trackCurrentTime.text = track.trackTime
            if (track.collectionName.isNotEmpty()) {
                trackCollection.text = track.collectionName
                groupCollection.isVisible = true
            } else {
                groupCollection.isVisible = false
            }
            trackYear.text = track.releaseYear
            trackGenre.text = track.primaryGenreName
            trackCountry.text = track.country
            buttonPlay.isEnabled = false
        }
    }

    /**
     * Инициализация плеера
     */
    private fun preparePlayer() {
        playerInteractor.init(
            dataSource = track.previewUrl,
            onPreparedListener = {
               binding.buttonPlay.isEnabled = true
               binding.buttonPlay.setImageResource(R.drawable.icon_play)
               playerState = STATE_PREPARED
            },
            onCompletionListener = {
                binding.buttonPlay.setImageResource(R.drawable.icon_play)
                playerState = STATE_PREPARED
                handler.removeCallbacksAndMessages(null)
                binding.trackCurrentTime.text = getString(R.string.track_time_placeholder)
            }
        )
    }

    /**
     * Запуск
     */
    private fun startPlayer() {
        playerInteractor.start()
        binding.buttonPlay.setImageResource(R.drawable.icon_pause)
        playerState = STATE_PLAYING

        handler.post(prepareCurrentTimeTask())
    }

    private fun prepareCurrentTimeTask(): Runnable {
        return object : Runnable {
            override fun run() {
                // Обновляем список в главном потоке
                displayCurrentPosition()

                // И снова планируем то же действие через TIME_REFRESH_INTERVAL секунд
                handler.postDelayed(
                    this,
                    TIME_REFRESH_INTERVAL,
                )
            }
        }
    }

    /**
     * Пауза
     */
    private fun pausePlayer() {
        playerInteractor.pause()
        binding.buttonPlay.setImageResource(R.drawable.icon_play)
        playerState = STATE_PAUSED
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * Старт-стоп
     */
    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    /**
     * Обновляем текущее время
     */
    private fun displayCurrentPosition() {
        binding.trackCurrentTime.text = playerInteractor.getCurrentTime()
    }

    /**
     * Свернули приложение
     */
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    /**
     * Закрыли активити или всё приложение
     */
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        playerInteractor.release()
    }


    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val TIME_REFRESH_INTERVAL = 500L
    }
}