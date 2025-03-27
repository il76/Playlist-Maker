package com.il76.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.bundle.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentPlayerBinding
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import com.il76.playlistmaker.media.ui.PlaylistAdapter
import com.il76.playlistmaker.media.ui.PlaylistPlayerAdapter
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.utils.debounce
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

    private lateinit var playlistsAdapter: PlaylistPlayerAdapter

    private lateinit var onPlaylistClickDebounce: (PlaylistTrack) -> Unit

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

        viewModel.observePlaylistsList().observe(viewLifecycleOwner) { playlists ->
            if (playlists != null) {
                renderPlaylists(playlists)
            } else {
                renderPlaylists(arrayListOf())
            }
        }

        binding.activityPlayerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }

//        onPlaylistClickDebounce = debounce<Playlist, Track>(
//            CLICK_DEBOUNCE_DELAY,
//            viewLifecycleOwner.lifecycleScope,
//            false
//        ) {}

        onPlaylistClickDebounce = debounce<PlaylistTrack>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlistTrack ->
            viewModel.addToPlaylist(playlistTrack)
        }
        viewModel.loadPlaylists()



        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playerBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        //bottomSheetBehavior.isHideable = true // Разрешить скрытие
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // newState — новое состояние BottomSheet
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                        // загружаем список плейлистов
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f) / 2
            }
        })


        binding.buttonPlaylistAdd.setOnClickListener {
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.playerBottomSheet)
            if (isPlaylisted) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.buttonPlaylistAdd.setImageResource(R.drawable.icon_playlist_add)
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.buttonPlaylistAdd.setImageResource(R.drawable.icon_playlist_add_active)
            }
            isPlaylisted = !isPlaylisted
        }
        binding.buttonLike.setOnClickListener {
            lifecycleScope.launch {
                viewModel.toggleFavouriteStatus()
            }
        }

        binding.newPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            findNavController().navigate(R.id.action_playerFragment_to_fragment_playlistadd)
        }

        binding.playlistsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun renderPlaylists(playlists: List<Playlist>) {
        playlistsAdapter = PlaylistPlayerAdapter(playlists, track, onPlaylistClickDebounce)
        binding.playlistsList.adapter = playlistsAdapter
        playlistsAdapter.notifyDataSetChanged()
        //binding.playlistsList.isVisible = tracks.isNotEmpty()
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

        private const val CLICK_DEBOUNCE_DELAY = 300L

        fun createArgs(trackData: String): Bundle =
            bundleOf(ARGS_TRACKDATA to trackData)
    }

}