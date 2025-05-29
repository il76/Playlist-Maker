package com.il76.playlistmaker.media.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.bundle.Bundle
import androidx.core.bundle.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import coil.compose.rememberAsyncImagePainter
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.MediaPlaylistsBinding
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.ui.shared.ErrorImageText
import com.il76.playlistmaker.ui.shared.UIConstants.CLICK_DEBOUNCE_DELAY
import com.il76.playlistmaker.utils.debounce
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {

//    private lateinit var binding: MediaPlaylistsBinding
//
//    private val mediaViewModel by activityViewModel<MediaViewModel>()
//
//    private val playlistsViewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()
//
//    private lateinit var onPlaylistClickDebounce: (PlaylistTrack) -> Unit
//
//    private lateinit var playlistsAdapter: PlaylistAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: android.os.Bundle?
//    ): View? {
//        binding = MediaPlaylistsBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        playlistsViewModel.observePlaylistsList().observe(viewLifecycleOwner) { playlists ->
//            if (playlists != null) {
//                renderPlaylists(playlists)
//            } else {
//                renderPlaylists(arrayListOf())
//            }
//        }
//
//        onPlaylistClickDebounce = debounce<PlaylistTrack>(
//            CLICK_DEBOUNCE_DELAY,
//            viewLifecycleOwner.lifecycleScope,
//            false
//        ) { playlistTrack ->
//            findNavController().navigate(R.id.action_media_fragment_to_fragment_playlist, createArgs(playlistTrack.playlist.id))
//            //playlistsViewModel.addToPlaylist(playlistTrack) //TODO in 23
//        }
//        playlistsViewModel.loadPlaylists()
//
//        binding.playlistsList.layoutManager = GridLayoutManager(requireActivity(), 2)
//        binding.newPlaylist.setOnClickListener {
//            findNavController().navigate(R.id.action_media_fragment_to_fragment_playlistadd)
//        }
//    }
//
//    private fun renderPlaylists(playlists: List<Playlist>) {
//        playlistsAdapter = PlaylistAdapter(playlists, Track(), onPlaylistClickDebounce)
//        binding.playlistsList.adapter = playlistsAdapter
//        playlistsAdapter.notifyDataSetChanged()
//
//        binding.playlistsList.isVisible = playlists.isNotEmpty()
//        binding.emptyPlaylistsList.isVisible = playlists.isEmpty()
//    }
//
//    companion object {
//        private const val NUMBER = "tracks"
//        fun newInstance(number: Int) = PlaylistsFragment().apply {
//            arguments = Bundle().apply {
//                putInt(NUMBER, number)
//
//            }
//        }
//        private const val ARGS_PLAYLISTID = "playlistid"
//
//        fun createArgs(playlistId: Int): android.os.Bundle =
//            bundleOf(ARGS_PLAYLISTID to playlistId)
//    }

}

@Composable
fun PlaylistsScreen(navController: NavController) {
    val viewModel: PlaylistsViewModel = koinViewModel()
    val playlists by viewModel.playlists.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPlaylists()
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                navController.navigate("playlistadd")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background,
            ),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.new_playlist))
        }
        when (val data = playlists) {
            null -> {
                ErrorImageText(R.drawable.search_nothing_found, R.string.media_empty_playlists)
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Фиксируем 2 колонки
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(data.size) { index ->
                        val playlist = data[index]
                        PlaylistItem(playlist = playlist, navController)
                    }
                }
            }
        }




    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = null,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = {
                navController.navigate("playlist/${playlist.id}")
            }),

    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (playlist.cover.isNotEmpty() && playlist.cover != "null") { //почему-то иногда прилетали
                    val imageUrl = playlist.cover
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.search_cover_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                }

            }

            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Text(
                text = "${playlist.cnt} ${stringResource(R.string.playlist_tracks_cnt_description)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}