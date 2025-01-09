package com.il76.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.bundle.Bundle
import androidx.fragment.app.Fragment
import com.il76.playlistmaker.databinding.MediaTracksBinding

class TracksFragment: Fragment() {

    private lateinit var binding: MediaTracksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): View? {
        binding = MediaTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //тут будет заполнение содержимого
        //binding.number.text = requireArguments().getInt(NUMBER).toString()
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