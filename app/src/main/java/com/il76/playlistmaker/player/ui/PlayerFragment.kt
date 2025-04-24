package com.il76.playlistmaker.player.ui

import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.bundle.bundleOf
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.il76.playlistmaker.R
import com.il76.playlistmaker.data.db.InsertStatus
import com.il76.playlistmaker.databinding.FragmentPlayerBinding
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.services.PlayerService
import com.il76.playlistmaker.utils.InternetBroadcastReceiver
import com.il76.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale


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
    var trackData = ""

    /**
     * Добавлено ли в плейлист
     */
    private var isPlaylisted = false

    private lateinit var playlistsAdapter: PlaylistPlayerAdapter

    private lateinit var onPlaylistClickDebounce: (PlaylistTrack) -> Unit

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private val internetBroadcastReceiver = InternetBroadcastReceiver()

    private var playerService: PlayerService? = null
    private var isServiceBound = false

    // Объявляем launcher как переменную класса фрагмента
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.PlayerServiceBinder
            playerService = binder.getService()

            lifecycleScope.launch {
                playerService?.playerStatus?.collect {
                    Log.i("pls", "collect")
                    Log.i("pls", it.toString())
                    viewModel.playerStatus = it
                    renderPlayer(it)
//                    if (it is PlayerStatus.Playing) {
//                        renderCurrentTime(it.progress)
//                    } else if (it is PlayerStatus.Loading) {
//                        render(it)
//                    }
                }
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
        }
    }

    private fun bindPlayerService() {
        val intent = Intent(requireContext(), PlayerService::class.java).apply {
            putExtra("track_data", viewModel.trackData)
        }
        requireActivity().bindService(
            intent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
        isServiceBound = true
    }

    private fun askForPermission() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                bindPlayerService()
            } else {
                // Пользователь отказал в разрешении
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showPermissionRationaleDialog()
                } else {
                    showToast(getString(R.string.cant_bind_service))
                }
            }
        }
        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Для версий ниже Android 13 разрешение не требуется
            bindPlayerService()
        }
    }

    // Показать диалог с объяснением
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Требуется доступ к уведомлениям")
            .setMessage("Приложению требуется доступ к уведомленям для воспроизведения аудио в фоновом режиме")
            .setPositiveButton("Предоставить") { _, _ ->
                requestNotificationPermission()
            }
            .setNegativeButton("Отменить", null)
            .show()
    }

    private fun unbindPlayerService() {
        if (isServiceBound) {
            requireActivity().unbindService(serviceConnection)
            isServiceBound = false
        }
    }

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

        onPlaylistClickDebounce = debounce<PlaylistTrack>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlistTrack ->
            viewModel.addToPlaylist(playlistTrack)
        }
        initBottomSheet()
        initObservers()
        initOnclicks()
        viewModel.loadPlaylists()

        binding.playlistsList.layoutManager = LinearLayoutManager(requireContext())

        askForPermission()
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playerBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
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
    }

    private fun initOnclicks() {
        binding.activityPlayerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }

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
    }

    private fun initObservers() {
//        viewModel.observeState().observe(viewLifecycleOwner) {
//            render(it)
//        }
//        viewModel.observePlayerStatus().observe(viewLifecycleOwner) {
//            Log.i("pls", it.toString())
//            if (it is PlayerStatus.Loading) {
//                render(it)
//            }
//        }
//        viewModel.observeCurrentTime().observe(viewLifecycleOwner) {
//            renderCurrentTime(it)
//        }
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

        viewModel.observeaddTrackResult().observe(viewLifecycleOwner) { status ->
            if (status == InsertStatus.SUCCESS) {
                findNavController().navigateUp()
            } else {
            }
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }
    }

    private fun renderPlaylists(playlists: List<Playlist>) {
        playlistsAdapter = PlaylistPlayerAdapter(playlists, track, onPlaylistClickDebounce)
        binding.playlistsList.adapter = playlistsAdapter
        playlistsAdapter.notifyDataSetChanged()
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
     * Старт-стоп
     */
    private fun playbackControl() {
        Log.i("pls", viewModel.playerStatus.toString())
        when(viewModel.playerStatus) {
            PlayerStatus.Default -> {}
            PlayerStatus.Prepared, PlayerStatus.Paused -> playerService?.startPlayer()
            is PlayerStatus.Playing -> playerService?.pausePlayer()
            is PlayerStatus.Loading -> render(viewModel.playerStatus)
        }
    }

    /**
     * Обновляем текущее время
     */
    private fun renderCurrentTime(time: Int) {
        binding.trackCurrentTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

    override fun onResume() {
        super.onResume()
        @Suppress("DEPRECATION")
        ContextCompat.registerReceiver(requireContext(), internetBroadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED)
        playerService?.hideNotification()
    }

    /**
     * Свернули приложение
     */
    override fun onPause() {
        super.onPause()
        // теперь не нужно
        // playerService?.pausePlayer()
        try {
            requireContext().unregisterReceiver(internetBroadcastReceiver)
        } catch (e: IllegalArgumentException) {
            // Ресивер не был зарегистрирован
        }
        playerService?.showNotification()
    }

    private fun render(status: PlayerStatus) {
        if (status is PlayerStatus.Loading) {
           track = status.track
            fillTrackInfo()
        }
    }

    private fun renderPlayer(status: PlayerStatus) {
        binding.buttonPlay.setStatus(viewModel.playerStatus)
        when (status) {
            PlayerStatus.Default -> {}
            PlayerStatus.Prepared -> {
                binding.buttonPlay.isEnabled = true
                binding.trackCurrentTime.text = getString(R.string.track_time_placeholder)
            }
            is PlayerStatus.Playing -> {
                renderCurrentTime(status.progress)
            }
            PlayerStatus.Paused -> {
            }

            is PlayerStatus.Loading -> render(status)
        }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindPlayerService()
    }

    companion object {

        private const val ARGS_TRACKDATA = "track"

        private const val CLICK_DEBOUNCE_DELAY = 300L

        fun createArgs(trackData: String): Bundle =
            bundleOf(ARGS_TRACKDATA to trackData)
    }

}