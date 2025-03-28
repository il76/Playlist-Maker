package com.il76.playlistmaker.media.ui

import android.content.res.ColorStateList
import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.bundle.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentPlaylistaddBinding
import com.il76.playlistmaker.media.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID


class PlaylistAddFragment: Fragment() {

    private lateinit var binding: FragmentPlaylistaddBinding

    private val playlistAddViewModel: PlaylistAddViewModel by viewModel<PlaylistAddViewModel>()

    private var imageUri: Uri? = null // Переменная для хранения Uri

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
            showConfirmationDialog()

        }
        //регистрируем событие, которое вызывает photo picker
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.playlistCreateImage.isVisible = false
                    //binding.playlistCover.setImageURI(uri)
                    Glide.with(binding.playlistCover)
                        .load(uri)
                        .placeholder(R.drawable.search_cover_placeholder)
                        .transform(CenterCrop(), RoundedCorners(binding.root.context.resources.getDimensionPixelSize(R.dimen.track_cover_border_radius)))
                        .into(binding.playlistCover)
                    imageUri = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        binding.playlistCreateImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Создаем обработчик нажатия кнопки "Назад"
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               showConfirmationDialog()
            }
        }
        // Регистрируем обработчик
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        playlistAddViewModel.observeSuccess().observe(viewLifecycleOwner) { result ->
            if (result) {
                findNavController().navigateUp()
            } else {
                //ошибка
            }
        }

        binding.textInputEditTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                binding.createPlaylist.setEnabled(s.toString().isNotEmpty())
//                val isEmpty = s.isNullOrEmpty()
//                val hintColorRes = if (isEmpty) R.color.playlist_create_disabled else R.color.playlist_create_enabled
//                binding.textInputLayoutName.boxStrokeColor = requireContext().getColor(hintColorRes)
//                binding.textInputLayoutName.hintTextColor =
//                    ContextCompat.getColorStateList(requireContext(), hintColorRes)
            }
        })
//        binding.textInputEditTextDescr.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable) {
//                binding.createPlaylist.setEnabled(s.toString().isNotEmpty())
//                val isEmpty = s.isNullOrEmpty()
//                val hintColorRes = if (isEmpty) R.color.playlist_create_disabled else R.color.playlist_create_enabled
//                binding.textInputLayoutDescr.boxStrokeColor = requireContext().getColor(hintColorRes)
//                binding.textInputLayoutDescr.hintTextColor =
//                    ContextCompat.getColorStateList(requireContext(), hintColorRes)
//
//            }
//        })
        binding.createPlaylist.setEnabled(!binding.textInputEditTextName.text.isNullOrEmpty())

        binding.createPlaylist.setOnClickListener {
            savePlaylist()
        }

    }


    private fun showConfirmationDialog() {
        if (binding.textInputEditTextName.text.isNullOrEmpty() && imageUri == null) {
            findNavController().navigateUp()
            return
        }
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { dialog, which ->
                // ничего не делаем пока
            }
            .setPositiveButton("Завершить") { dialog, which -> //
                findNavController().navigateUp()
            }
            .show()
    }

    private fun saveFileToPrivateStorage(uri: Uri): String {
        val context = requireContext()
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        val file = File(context.filesDir, UUID.randomUUID().toString().take(16)) //генерим случайное имя файла

        try {
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return file.absolutePath
            //Log.d("MyFragment", "File saved to: ${file.absolutePath}")
        } catch (e: Exception) {
            //Log.e("MyFragment", "Error saving file", e)
        } finally {
            inputStream?.close()
        }
        return ""
    }

    private fun savePlaylist() {
        var cover = ""
        imageUri?.let { cover = saveFileToPrivateStorage(it) }
        playlistAddViewModel.savePlaylist(Playlist(
            name = binding.textInputEditTextName.text.toString(),
            description = binding.textInputEditTextDescr.text.toString(),
            cover = cover,
            cnt = 0
        ))
        findNavController().navigateUp()
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