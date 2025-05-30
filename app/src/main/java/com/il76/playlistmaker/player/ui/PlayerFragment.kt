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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.bundle.bundleOf
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.il76.playlistmaker.R
import com.il76.playlistmaker.data.db.InsertStatus
import com.il76.playlistmaker.databinding.FragmentPlayerBinding
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.services.PlayerService
import com.il76.playlistmaker.ui.shared.UIConstants.CLICK_DEBOUNCE_DELAY
import com.il76.playlistmaker.utils.InternetBroadcastReceiver
import com.il76.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
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
            viewModel.setPlayerControl(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removePlayerControl()
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
            .setTitle(getString(R.string.notifications_access_required_title))
            .setMessage(getString(R.string.notifications_access_required_description))
            .setPositiveButton(getString(R.string.notifications_access_required_ok)) { _, _ ->
                requestNotificationPermission()
            }
            .setNegativeButton(getString(R.string.notifications_access_required_cancel), null)
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
            viewModel.playbackControl()
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
        viewModel.observeFavourite().observe(viewLifecycleOwner) {
            renderFavourite(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }
        viewModel.observePlayerStatus().observe(viewLifecycleOwner) {
            renderPlayer(it)
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
        viewModel.hideNotification()
    }

    /**
     * Свернули приложение
     */
    override fun onPause() {
        super.onPause()
        try {
            requireContext().unregisterReceiver(internetBroadcastReceiver)
        } catch (e: IllegalArgumentException) {
            // Ресивер не был зарегистрирован
        }
        viewModel.showNotification()
    }

    private fun render(status: PlayerStatus) {
        if (status is PlayerStatus.Loading) {
           track = status.track
            fillTrackInfo()
        }
    }

    private fun renderPlayer(status: PlayerStatus) {
        binding.buttonPlay.setStatus(status)
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

    override fun onDestroy() {
        super.onDestroy()
        unbindPlayerService()
    }

    companion object {

        private const val ARGS_TRACKDATA = "track"

        fun createArgs(trackData: String): Bundle =
            bundleOf(ARGS_TRACKDATA to trackData)
    }

}

@Composable
fun PlayerScreen(navController: NavController, trackData: String) {
    //Log.d("pls", "trackData: " + track.toString())
    val viewModel: PlayerViewModel = koinViewModel() {
        parametersOf(trackData)
    }
    //TODO
    val track = remember(trackData) {
        Gson().fromJson(trackData, Track::class.java)
    }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        // Обложка трека
        val trackPainter =
            if (track.poster.isBlank()) {
                painterResource(R.drawable.icon_share)
            } else {
                rememberAsyncImagePainter(track.poster)
            }
        Image(
            painter = trackPainter,
            contentDescription = "Обложка трека",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(top = 26.dp)
        )

        // Название трека
        Text(
            text = track.trackName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
        )

        // Имя исполнителя
        Text(
            text = track.artistName,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Кнопки управления
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                TODO()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_playlist_add),
                    contentDescription = "Добавить в плейлист",
                    tint = MaterialTheme.colorScheme.inverseSurface
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.size(100.dp)) {
                Button(
                    onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.playbackControl()
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_play),
                        contentDescription = "Воспроизвести",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = {
                viewModel.viewModelScope.launch {
                    viewModel.toggleFavouriteStatus()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_like),
                    contentDescription = "Лайк",
                    tint = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }

        // Текущее время воспроизведения
        Text(
            text = "00:00",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .wrapContentWidth(align = Alignment.CenterHorizontally)
        )

        // Информация о треке
        TrackInfoRow(label = "Время", value = track.trackTime)
        TrackInfoRow(label = stringResource(R.string.track_collection_name), value = track.collectionName)
        TrackInfoRow(label = stringResource(R.string.track_year), value = track.releaseYear)
        TrackInfoRow(label = stringResource(R.string.track_genre), value = track.primaryGenreName)
        TrackInfoRow(label = stringResource(R.string.track_country), value = track.country)
    }
}

@Composable
fun TrackInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}