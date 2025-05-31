package com.il76.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.bundle.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.MediaTracksBinding
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.search.ui.TrackAdapter
import com.il76.playlistmaker.search.ui.TrackScreen
import com.il76.playlistmaker.ui.shared.ErrorImageText
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracksFragment: Fragment() {

    private lateinit var binding: MediaTracksBinding

    private val mediaViewModel by activityViewModel<MediaViewModel>()

    private val tracksViewModel: TracksViewModel by viewModel<TracksViewModel>()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                FavoriteTracksScreen(findNavController())
            }
        }
    }
}

@Composable
fun FavoriteTracksScreen(navController: NavController) {
    val viewModel: TracksViewModel = koinViewModel()
    val tracks = viewModel.tracks.collectAsState()

    TrackScreen(
        tracks = tracks.value ?: emptyList(),
        onTrackClick = { viewModel.onTrackClicked(it) },
        modifier = Modifier
    )
    Box(modifier = Modifier.fillMaxSize()) {
        ErrorImageText(R.drawable.search_nothing_found, R.string.media_empty_tracks)
    }
    // Обработка событий
    LaunchedEffect(Unit) {
        viewModel.trackEvents.collect { event ->
            when (event) {
                is TrackEvent.TrackClicked -> {
                    val args = bundleOf("trackData" to viewModel.provideTrackData(event.track))
                    navController.navigate(R.id.playerFragment, args)
                }
            }
        }
    }
}