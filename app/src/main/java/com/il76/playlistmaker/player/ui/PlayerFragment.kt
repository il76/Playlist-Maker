package com.il76.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.bundle.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentPlayerBinding
import com.il76.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment: Fragment() {
    private lateinit var binding: FragmentPlayerBinding

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
     * Добавлено ли в плейлист
     */
    private var isPlaylisted = false

    /**
     * Текущее состояние плеера
     */
    private var playerSatus = PlayerStatus.DEFAULT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackData = requireArguments().getString(ARGS_TRACKDATA).orEmpty()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.observePlayerStatus().observe(viewLifecycleOwner) {
            playerSatus = it
            renderPlayer(it)
        }
        viewModel.observeCurrentTime().observe(viewLifecycleOwner) {
            renderCurrentTime(it)
        }
        viewModel.observeFavourite().observe(viewLifecycleOwner) {
            renderFavourite(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }

        binding.activityPlayerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
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
            lifecycleScope.launch {
                viewModel.toggleFavouriteStatus()
            }
        }
    }

    private fun renderFavourite(isFauvorite: Boolean) {
        if (isFauvorite) {
            binding.buttonLike.setImageResource(R.drawable.icon_like_active)
        } else {
            binding.buttonLike.setImageResource(R.drawable.icon_like)
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
            Log.i("pls", track.isFavourite.toString())
            renderFavourite(track.isFavourite)
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
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }


    companion object {

        private const val ARGS_TRACKDATA = "track"

        fun createArgs(trackData: String): Bundle =
            bundleOf(ARGS_TRACKDATA to trackData)
    }

}