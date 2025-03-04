package com.il76.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.bundle.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.MediaTracksBinding
import com.il76.playlistmaker.media.ui.MediaFragment.Companion.createArgs
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.il76.playlistmaker.search.ui.TrackAdapter
import com.il76.playlistmaker.utils.debounce
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
        binding = MediaTracksBinding.inflate(inflater, container, false)
        tracksViewModel.observeTracksList().observe(viewLifecycleOwner) {
            renderTracks(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //тут будет заполнение содержимого
        //binding.number.text = requireArguments().getInt(NUMBER).toString()
        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_media_fragment_to_playerFragment,
                createArgs(tracksViewModel.provideTrackData(track))
            )
        }
        binding.trackList.layoutManager = LinearLayoutManager(requireContext())

        tracksViewModel.getTracks()
    }

    private fun renderTracks(tracks: List<Track>) {
        trackAdapter = TrackAdapter(tracks, onTrackClickDebounce)
        binding.trackList.adapter = trackAdapter
        trackAdapter.notifyDataSetChanged()
        binding.trackList.isVisible = tracks.isNotEmpty()
        binding.emptyResult.isVisible = tracks.isEmpty()
    }

    companion object {
        private const val NUMBER = "tracks"
        fun newInstance(number: Int) = TracksFragment().apply {
            arguments = Bundle().apply {
                putInt(NUMBER, number)
            }
        }
    }

}