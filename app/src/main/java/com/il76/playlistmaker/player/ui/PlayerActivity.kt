package com.il76.playlistmaker.player.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.ActivityPlayerBinding
import com.il76.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private var _binding: ActivityPlayerBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(trackData)
    }

    /**
     * Данные о треке, прилетают с экрана поиска
     */
    private var track = Track()
    /**
     * Данные о треке, прилетают с экрана поиска
     */
    private var trackData = ""


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
    private var playerSatus = PlayerStatus.DEFAULT

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
        trackData = intent.getStringExtra("track").orEmpty()

        viewModel.observeState().observe(this) {
            render(it)
        }
        viewModel.observePlayerStatus().observe(this) {
            playerSatus = it
            renderPlayer(it)
        }
        viewModel.observeCurrentTime().observe(this) {
            renderCurrentTime(it)
        }

        viewModel.observeShowToast().observe(this) { toast ->
            showToast(toast)
        }

        binding.activityPlayerToolbar.setNavigationOnClickListener {
            this.finish()
        }

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
    }

    /**
     * Заполняем вью информацией о выбранном треке
     */
    private fun fillTrackInfo() {
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
     * Запуск
     */
    private fun startPlayer() {
        viewModel.changePlayerStatus(PlayerStatus.PLAYIND)
        playerSatus = PlayerStatus.PLAYIND
    }

    /**
     * Пауза
     */
    private fun pausePlayer() {
        viewModel.changePlayerStatus(PlayerStatus.PAUSED)
        playerSatus = PlayerStatus.PAUSED
    }

    /**
     * Старт-стоп
     */
    private fun playbackControl() {
        when(playerSatus) {
            PlayerStatus.DEFAULT -> {}
            PlayerStatus.PREPARED, PlayerStatus.PAUSED -> startPlayer()
            PlayerStatus.PLAYIND -> pausePlayer()
        }
    }

    /**
     * Обновляем текущее время
     */
    private fun renderCurrentTime(time: String) {
        binding.trackCurrentTime.text = time
    }

    /**
     * Свернули приложение
     */
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private fun render(state: PlayerState) {
        when(state) {
            is PlayerState.Loading -> {
                track = state.track
            }
        }
        fillTrackInfo()
    }

    private fun renderPlayer(status: PlayerStatus) {
        when (status) {
            PlayerStatus.DEFAULT -> fillTrackInfo()
            PlayerStatus.PREPARED -> {
                binding.buttonPlay.isEnabled = true
                binding.buttonPlay.setImageResource(R.drawable.icon_play)
                binding.trackCurrentTime.text = getString(R.string.track_time_placeholder)
            }
            PlayerStatus.PLAYIND -> {
                binding.buttonPlay.setImageResource(R.drawable.icon_pause)
            }
            PlayerStatus.PAUSED -> {
                binding.buttonPlay.setImageResource(R.drawable.icon_play)
            }
        }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG).show()
    }

}