package com.il76.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class PlaylistsFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistsScreen(findNavController())
            }
        }
    }
}
