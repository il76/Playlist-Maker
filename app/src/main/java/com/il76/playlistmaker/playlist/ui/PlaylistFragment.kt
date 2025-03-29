package com.il76.playlistmaker.playlist.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.bundle.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.il76.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment: Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = requireArguments().getInt(ARGS_PLAYLISTID)
        binding.activityPlaylistToolbar.setOnClickListener {
            findNavController().navigateUp()
        }
        Log.i("pls", viewModel.playlist.toString())
        // Создаем обработчик нажатия кнопки "Назад"
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //showConfirmationDialog()
            }
        }


        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetTracks)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        //bottomSheetBehavior.isHideable = true // Разрешить скрытие
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // newState — новое состояние BottomSheet
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                        // загружаем список плейлистов
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f) / 2
            }
        })


    }

    private var playlistId: Int = 0

    companion object {

        private const val ARGS_PLAYLISTID = "playlistid"

        fun createArgs(trackData: String): Bundle =
            bundleOf(ARGS_PLAYLISTID to trackData)
    }

}