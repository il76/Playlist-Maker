package com.il76.playlistmaker.playlist.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.bundle.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentPlaylistBinding
import com.il76.playlistmaker.media.domain.models.Playlist
import com.il76.playlistmaker.media.domain.models.PlaylistTrack
import com.il76.playlistmaker.media.ui.PlaylistAddFragment
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.search.ui.TrackAdapter
import com.il76.playlistmaker.ui.shared.UIConstants.CLICK_DEBOUNCE_DELAY
import com.il76.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment: Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private lateinit var onTrackLongClick: (PlaylistTrack) -> Boolean

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var bottomSheetInfoBehavior: BottomSheetBehavior<*>

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
        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }
        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            if (playlist != null) {
                renderPlaylist(playlist)
            } else {
                showToast("Ошибка при загрузке плейлиста")
                findNavController().navigateUp()
            }
        }
        viewModel.observeTrackslist().observe(viewLifecycleOwner) { trackList ->
            renderTracks(trackList)
        }
        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetInfoBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val args = bundleOf("trackData" to viewModel.provideTrackData(track))
            findNavController().navigate(R.id.playerFragment, args)
//            findNavController().navigate(
//                R.id.action_fragment_playlist_to_playerFragment,
//                PlayerFragment.createArgs(viewModel.provideTrackData(track))
//            )
        }
        onTrackLongClick = {playlistTrack ->
            MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
                .setTitle(getString(R.string.delete_track))
                .setMessage("Вы уверены, что хотите удалить трек из плейлиста \""+viewModel.playlist?.name + "\"?")
                .setNegativeButton(getString(R.string.text_no)) { dialog, which ->
                    // ничего не делаем пока
                }
                .setPositiveButton(getString(R.string.text_yes)) { dialog, which ->
                    viewModel.deleteTrackFromPlaylist(playlistTrack).observe(viewLifecycleOwner) { result ->
                        if (result) {
                            viewModel.loadTracks()
                        }
                    }
                }
                .show()
            true
        }
        // Создаем обработчик нажатия кнопки "Назад"
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                //showConfirmationDialog()
//            }
//        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetTracks)
        bottomSheetInfoBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetPlaylistInfo)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetInfoBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.playlistIconSubmenu.setOnClickListener {
            bottomSheetInfoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetInfoBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.isVisible = false
                            bottomSheetBehavior.isHideable = false
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        else -> {
                            binding.overlay.isVisible = true
                            bottomSheetBehavior.isHideable = true
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.overlay.alpha = (slideOffset + 1f) / 2
                }
            })
        }

        binding.playlistIconShare.setOnClickListener {
            viewModel.sharePlaylist()
        }
        binding.bottomSheetShare.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.bottomSheetDeletePlaylist.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
                .setTitle("")
                .setMessage("Хотите удалить плейлист \""+viewModel.playlist?.name + "\"")
                .setNegativeButton(getString(R.string.text_no)) { dialog, which ->
                    // ничего не делаем пока
                }
                .setPositiveButton(getString(R.string.text_yes)) { dialog, which ->
                    viewModel.deletePlaylist().observe(viewLifecycleOwner) { result ->
                        if (result) {
                            findNavController().navigateUp()
                        }
                    }

                }
                .show()
        }

        binding.bottomSheetEdit.setOnClickListener {
            val id = viewModel.playlist?.id ?: 0
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetInfoBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_fragment_playlist_to_fragment_playlistadd,
                PlaylistAddFragment.createArgs(id)
            )
        }


    }

    override fun onResume() {
        super.onResume()
        bottomSheetInfoBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        viewModel.loadPlaylist()
        // без кода ниже едет вёрстка
        // Получаем ширину экрана
        val windowMetrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Для API 30+
            requireActivity().windowManager.currentWindowMetrics.bounds.width()
        } else {
            // Для API 28-29
            resources.displayMetrics.widthPixels
        }
        // Устанавливаем высоту FrameLayout равной ширине экрана
        binding.frameLayout.layoutParams = binding.frameLayout.layoutParams.apply {
            height = windowMetrics
        }
        // Обновляем разметку (если изменения не применяются автоматически)
        binding.frameLayout.requestLayout()
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun renderPlaylist(playlist: Playlist) {
        if (playlist.cover.isNotEmpty()) {
            binding.playlistCover.setPadding(0,0,0,0) //для реальной картинки в макете отступы отсутствуют
            Glide.with(this).load(playlist.cover).into(binding.playlistCover)
            binding.frameLayout.requestLayout()
            Glide.with(this).load(playlist.cover).into(binding.bottomSheetPlaylistinfoBlock.playlistCover)
        }
        binding.playlistName.text = playlist.name
        binding.bottomSheetPlaylistinfoBlock.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        binding.bottomSheetPlaylistinfoBlock.playlistCover
        // длительность и количество треков после загрузки данных по ним

    }

    private fun renderTracks(trackList: List<Track>?) {
        var counterText = "0 треков"
        var durationText = "0 минут"
        if (!trackList.isNullOrEmpty()) {
            val trackAdapter = TrackAdapter(trackList, onTrackClickDebounce, viewModel.playlist, onTrackLongClick)
            binding.tracksList.layoutManager = LinearLayoutManager(requireActivity())
            binding.tracksList.adapter = trackAdapter
            trackAdapter.notifyDataSetChanged()

            counterText = trackList?.count().toString() + " треков"

            var duration = 0
            for (track in trackList) {
                val parts = track.trackTime.split(":")
                if (parts.size != 2) continue // пропускаем некорректные форматы, хотя откуда им там взяться?
                val minutes = parts[0].toIntOrNull() ?: 0
                val seconds = parts[1].toIntOrNull() ?: 0
                duration += minutes * 60 + seconds
            }
            val minutes = duration/60
            durationText = "$minutes минут"
        } else {
            binding.emptyTracksList.isVisible = true
            binding.tracksList.isVisible = false
        }
        binding.playlistCounter.text = counterText
        binding.bottomSheetPlaylistinfoBlock.playlistCounter.text = counterText
        binding.playlistDuration.text = durationText
    }

    private var playlistId: Int = 0

    companion object {

        private const val ARGS_PLAYLISTID = "playlistid"

        fun createArgs(trackData: String): Bundle =
            bundleOf(ARGS_PLAYLISTID to trackData)
    }

}