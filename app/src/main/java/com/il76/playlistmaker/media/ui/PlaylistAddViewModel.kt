package com.il76.playlistmaker.media.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.il76.playlistmaker.media.domain.api.PlaylistInteractor
import com.il76.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class PlaylistAddViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val playlistId: Int
) : ViewModel() {

    lateinit var playlist: Playlist
    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    fun setImageUri(uri: Uri?, context: Context) {
        viewModelScope.launch {
            _imageUri.value = uri
            _coverPath.value = uri?.let { saveFileToPrivateStorage(it, context) } ?: ""
        }
    }

    private val _coverPath = MutableStateFlow("")
    val coverPath: StateFlow<String> = _coverPath



    init {
        if (playlistId > 0) {
            viewModelScope.launch {
                playlistInteractor.getSinglePlaylist(playlistId)?.collect { playlistData ->
                    playlist = playlistData
                    playlistLiveData.postValue(playlist)
                }
            }
        }
    }

    private val successLiveData = MutableLiveData<Boolean>()
    fun observeSuccess(): LiveData<Boolean> = successLiveData

    fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            if (playlist.id > 0) {
                playlistInteractor.updatePlaylist(playlist)
            } else {
                playlistInteractor.createPlaylist(playlist)
            }
            successLiveData.postValue(true)
        }
    }

    fun saveFileToPrivateStorage(uri: Uri, context: Context): String {
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
}