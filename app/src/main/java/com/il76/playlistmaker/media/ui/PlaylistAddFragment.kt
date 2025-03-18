package com.il76.playlistmaker.media.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.bundle.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentPlaylistaddBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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
                    binding.playlistCreateImage.setImageURI(uri)
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

    }


    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
            .setTitle("Завершить создание плейлиста?") // Заголовок диалога
            .setMessage("Все несохраненные данные будут потеряны") // Описание диалога
            .setNegativeButton("Отмена") { dialog, which -> // Добавляет кнопку «Нет»
                // Действия, выполняемые при нажатии на кнопку «Нет»
            }
            .setPositiveButton("Завершить") { dialog, which -> // Добавляет кнопку «Да»
                findNavController().navigateUp()
            }
            .show()
    }

    private fun saveFileToPrivateStorage(uri: Uri) {
        val context = requireContext()
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "my_image.jpg")

        try {
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("MyFragment", "File saved to: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("MyFragment", "Error saving file", e)
        } finally {
            inputStream?.close()
        }
    }

    private fun savePlaylist() {
        imageUri?.let { saveFileToPrivateStorage(it) }
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