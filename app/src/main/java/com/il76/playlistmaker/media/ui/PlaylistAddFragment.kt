package com.il76.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.bundle.Bundle
import androidx.fragment.app.Fragment
import com.il76.playlistmaker.databinding.MediaPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistAddFragment: Fragment() {

    private lateinit var binding: MediaPlaylistsBinding

    private val playlistAddViewModel: PlaylistAddViewModel by viewModel<PlaylistAddViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): View? {
        binding = MediaPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        private const val NUMBER = "tracks"
        fun newInstance(number: Int) = PlaylistAddFragment().apply {
            arguments = Bundle().apply {
                putInt(NUMBER, number)
            }
        }
    }

}