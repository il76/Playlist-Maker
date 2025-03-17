package com.il76.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.bundle.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.il76.playlistmaker.databinding.FragmentPlaylistaddBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistAddFragment: Fragment() {

    private lateinit var binding: FragmentPlaylistaddBinding

    private val playlistAddViewModel: PlaylistAddViewModel by viewModel<PlaylistAddViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): View? {
        binding = FragmentPlaylistaddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newPlaylistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
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