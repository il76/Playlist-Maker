package com.il76.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentMediaBinding

class MediaFragment: Fragment() {
    private lateinit var binding: FragmentMediaBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mediaViewPager.adapter = MediaPagerAdapter(childFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.mediaTabLayout, binding.mediaViewPager) { tab, position ->
            when(position) {
                0 -> tab.text = requireContext().getString(R.string.media_tab_faforite_tracks)
                1 -> tab.text = requireContext().getString(R.string.media_tab_playlists)
            }
        }
        tabMediator.attach()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }
}