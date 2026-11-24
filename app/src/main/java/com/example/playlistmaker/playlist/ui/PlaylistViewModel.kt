package com.example.playlistmaker.playlist.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.playlist.domain.Playlist

class PlaylistViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val playlist = Playlist(null, null, null)

    fun updateName(name: String) {
        playlist.name = name
    }

    fun updateDescription(description: String) {
        playlist.description = description
    }

    fun updateImage(imageUri: Uri) {
        savedStateHandle[IMAGE_URI] = imageUri
    }

    fun getImageUri(): Uri? = savedStateHandle[IMAGE_URI]

    fun hasUnsavedChanges(): Boolean {
        return !playlist.name.isNullOrBlank() ||
                !playlist.description.isNullOrBlank() ||
                getImageUri() != null
    }

    companion object {
        private const val IMAGE_URI = "image_uri"
    }
}