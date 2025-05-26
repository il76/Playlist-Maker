package com.il76.playlistmaker.media.ui

import android.content.res.ColorStateList
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.bundle.bundleOf
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.FragmentPlaylistaddBinding
import com.il76.playlistmaker.media.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID


class PlaylistAddFragment: Fragment() {

    private lateinit var binding: FragmentPlaylistaddBinding

    private val playlistAddViewModel: PlaylistAddViewModel by viewModel{
        parametersOf(playlistId)
    }


    private var imageUri: Uri? = null // Переменная для хранения Uri

    private var playlistId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): View? {
        binding = FragmentPlaylistaddBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun putImage(uri: Uri) {
        binding.playlistCreateImage.isVisible = false
        Glide.with(binding.playlistCover)
            .load(uri)
            .placeholder(R.drawable.search_cover_placeholder)
            .transform(CenterCrop(), RoundedCorners(binding.root.context.resources.getDimensionPixelSize(R.dimen.track_cover_border_radius)))
            .into(binding.playlistCover)
        imageUri = uri
    }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = arguments?.getInt(PLAYLIST_ID) ?: 0
        //регистрируем событие, которое вызывает photo picker
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    putImage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        binding.playlistCreateImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Создаем обработчик нажатия кнопки "Назад"
        if (playlistId == 0) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showConfirmationDialog()
                }
            }
            // Регистрируем обработчик
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
            binding.newPlaylistToolbar.setNavigationOnClickListener {
                showConfirmationDialog()
            }
        } else {
            binding.newPlaylistToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }


        playlistAddViewModel.observeSuccess().observe(viewLifecycleOwner) { result ->
            if (result) {
                findNavController().navigateUp()
            } else {
                //ошибка
            }
        }

        if (playlistId > 0) {
            playlistAddViewModel.observePlaylist().observe(viewLifecycleOwner) { playlistData ->
                renderPlaylist(playlistData)
            }
            binding.createPlaylist.text = "Сохранить"
        }

        binding.textInputEditTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                binding.createPlaylist.setEnabled(s.toString().isNotEmpty())
                val isEmpty = s.isNullOrEmpty()
                val hintColorRes = if (isEmpty) R.color.playlist_create_disabled else R.color.playlist_create_enabled
                binding.textInputLayoutName.boxStrokeColor = requireContext().getColor(hintColorRes)
                binding.textInputLayoutName.hintTextColor =
                    ContextCompat.getColorStateList(requireContext(), hintColorRes)
                binding.textInputLayoutName.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(hintColorRes))
            }
        })
        binding.textInputEditTextDescr.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                binding.createPlaylist.setEnabled(s.toString().isNotEmpty())
                val isEmpty = s.isNullOrEmpty()
                val hintColorRes = if (isEmpty) R.color.playlist_create_disabled else R.color.playlist_create_enabled
                binding.textInputLayoutDescr.boxStrokeColor = requireContext().getColor(hintColorRes)
                binding.textInputLayoutDescr.hintTextColor =
                    ContextCompat.getColorStateList(requireContext(), hintColorRes)

            }
        })
        binding.createPlaylist.setEnabled(!binding.textInputEditTextName.text.isNullOrEmpty())

        binding.createPlaylist.setOnClickListener {
            savePlaylist()
        }

    }


    private fun renderPlaylist(playlist: Playlist) {
        binding.textInputEditTextName.setText(playlist.name)
        binding.textInputEditTextDescr.setText(playlist.description)
        if (playlist.cover.isNotEmpty()) {
            binding.playlistCreateImage.isVisible = false
            Glide.with(binding.playlistCover)
                .load(playlist.cover)
                .placeholder(R.drawable.search_cover_placeholder)
                .transform(CenterCrop(), RoundedCorners(binding.root.context.resources.getDimensionPixelSize(R.dimen.track_cover_border_radius)))
                .into(binding.playlistCover)
            //putImage(Uri.parse(playlist.cover))
        }
        binding.createPlaylist.setEnabled(!binding.textInputEditTextName.text.isNullOrEmpty())
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
        if (playlistId > 0 && imageUri != null) { //обновляем фото
            cover = imageUri.toString()
        } else if (playlistId > 0 && imageUri == null) { //НЕ обновляем фото
            cover = playlistAddViewModel.playlist.cover
        } else { //новый плейлист
            imageUri?.let { cover = saveFileToPrivateStorage(it) }
        }

        playlistAddViewModel.savePlaylist(Playlist(
            id = playlistId,
            name = binding.textInputEditTextName.text.toString(),
            description = binding.textInputEditTextDescr.text.toString(),
            cover = cover,
            cnt = 0
        ))
        findNavController().navigateUp()
    }

    companion object {
        private const val PLAYLIST_ID = "playlist"
        fun createArgs(playlistId: Int): android.os.Bundle =
            bundleOf(PLAYLIST_ID to playlistId)

    }

}

@Composable
fun PlaylistAddScreen(
    navController: NavController,
    playlistId: Int = 0
) {
    val context = LocalContext.current
    val viewModel: PlaylistAddViewModel = koinViewModel {
        parametersOf(playlistId)
    }
    var playlistName by remember { mutableStateOf("") }
    var playlistDescription by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val imageUri by viewModel.imageUri.collectAsState()
    val isImageSelected = imageUri != null

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.setImageUri(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {

//        // Toolbar
//        TopAppBar(
//            title = {
//                Text(
//                    text = stringResource(id = R.string.new_playlist),
//                    style = MaterialTheme.typography.titleLarge
//                )
//            },
//            navigationIcon = {
//                IconButton(onClick = { /* Handle back */ }) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.icon_back),
//                        contentDescription = "Back"
//                    )
//                }
//            }
//        )
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .aspectRatio(1f)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                )
                .clip(MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                        pickMediaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isImageSelected) {
                    // Отображаем выбранное изображение — растянуто по ширине
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp), // минимум как заглушка
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Отображаем заглушку — фиксированный размер 100.dp
                    Image(
                        painter = painterResource(id = R.drawable.icon_playlist_create),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Crop
                    )
                }
//            Image(
//                painter = painterResource(id = R.drawable.icon_playlist_create),
//                contentDescription = null,
//                modifier = Modifier.size(100.dp).clickable {
//                    pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//                },
//                contentScale = ContentScale.Crop,
//            )
            }
        }

        OutlinedTextField(
            value = playlistName,
            onValueChange = { playlistName = it },
            label = { Text(stringResource(R.string.new_playlist_title)) },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = playlistDescription,
            onValueChange = { playlistDescription = it },
            label = { Text(stringResource(R.string.new_playlist_description)) },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        )

        // Create Button
        Button(
            //onClick = { onCreatePlaylist(playlistName, playlistDescription) },
            onClick = {  },
            modifier = Modifier
                .padding(horizontal = 17.dp, vertical = 32.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = stringResource(R.string.new_playlist_create))
        }
    }
}

@Composable
@Preview
fun PlaylistAddScreenPreview() {
    PlaylistAddScreen(rememberNavController())
}
