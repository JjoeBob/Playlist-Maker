package com.example.playlistmaker.playlist.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.playlist.domain.Playlist
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import java.io.IOException

class PlaylistViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val playlist = Playlist(
        savedStateHandle.get<Uri>(IMAGE_URI),
        "",
        null
    )

    fun updateName(name: String) {
        playlist.name = name
    }

    fun updateDescription(description: String) {
        playlist.description = description
    }

    fun updateImage(imageUri: Uri) {
        savedStateHandle[IMAGE_URI] = imageUri
        playlist.imageUri = imageUri
    }

    fun getImageUri(): Uri? = savedStateHandle.get<Uri>(IMAGE_URI)

    fun hasUnsavedChanges(): Boolean {
        return playlist.name.isNotBlank() ||
                !playlist.description.isNullOrBlank() ||
                getImageUri() != null
    }

    suspend fun savePlaylist(): Boolean {
        return try {
            playlistInteractor.savePlaylist(playlist)
            true
        } catch (_: IOException) {
            false
        } catch (_: SecurityException) {
            false
        }
    }

    companion object {
        private const val IMAGE_URI = "image_uri"
    }
}