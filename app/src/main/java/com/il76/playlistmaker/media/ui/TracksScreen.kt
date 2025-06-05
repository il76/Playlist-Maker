package com.il76.playlistmaker.media.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.bundle.bundleOf
import androidx.navigation.NavController
import com.il76.playlistmaker.R
import com.il76.playlistmaker.search.ui.TrackScreen
import com.il76.playlistmaker.ui.shared.ErrorImageText
import org.koin.androidx.compose.koinViewModel

@Composable
fun TracksScreen(navController: NavController) {
    val viewModel: TracksViewModel = koinViewModel()
    val tracks = viewModel.tracks.collectAsState()

    when {
        tracks.value.isEmpty() -> {
            ErrorImageText(R.drawable.search_nothing_found, R.string.media_empty_tracks)
        }

        else -> {
            TrackScreen(
                tracks = tracks.value ?: emptyList(),
                onTrackClick = { viewModel.onTrackClicked(it) },
                modifier = Modifier
            )
        }
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