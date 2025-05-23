package com.il76.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.bundle.Bundle
import androidx.core.bundle.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.MediaPlaylistsBinding
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.ui.shared.UIConstants.CLICK_DEBOUNCE_DELAY
import com.il76.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {

    private lateinit var binding: MediaPlaylistsBinding

    private val mediaViewModel by activityViewModel<MediaViewModel>()

    private val playlistsViewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()

    private lateinit var onPlaylistClickDebounce: (PlaylistTrack) -> Unit

    private lateinit var playlistsAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): View? {
        binding = MediaPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistsViewModel.observePlaylistsList().observe(viewLifecycleOwner) { playlists ->
            if (playlists != null) {
                renderPlaylists(playlists)
            } else {
                renderPlaylists(arrayListOf())
            }
        }

        onPlaylistClickDebounce = debounce<PlaylistTrack>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlistTrack ->
            findNavController().navigate(R.id.action_media_fragment_to_fragment_playlist, createArgs(playlistTrack.playlist.id))
            //playlistsViewModel.addToPlaylist(playlistTrack) //TODO in 23
        }
        playlistsViewModel.loadPlaylists()

        binding.playlistsList.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.newPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_media_fragment_to_fragment_playlistadd)
        }
    }

    private fun renderPlaylists(playlists: List<Playlist>) {
        playlistsAdapter = PlaylistAdapter(playlists, Track(), onPlaylistClickDebounce)
        binding.playlistsList.adapter = playlistsAdapter
        playlistsAdapter.notifyDataSetChanged()

        binding.playlistsList.isVisible = playlists.isNotEmpty()
        binding.emptyPlaylistsList.isVisible = playlists.isEmpty()
    }

    companion object {
        private const val NUMBER = "tracks"
        fun newInstance(number: Int) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putInt(NUMBER, number)

            }
        }
        private const val ARGS_PLAYLISTID = "playlistid"

        fun createArgs(playlistId: Int): android.os.Bundle =
            bundleOf(ARGS_PLAYLISTID to playlistId)
    }

}