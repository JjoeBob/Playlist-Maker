package com.example.playlistmaker.playlist.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.playlist.domain.Playlist

class PlaylistViewModel : ViewModel() {
    private val playlist = Playlist(null, null, null)

    fun updateName(name: String) {
        playlist.name = name
    }

    fun updateDescription(description: String) {
        playlist.description = description
    }

    fun updateImage(imageUri: Uri) {
        playlist.imageUri = imageUri
    }

    fun hasUnsavedChanges(): Boolean {
        return !playlist.name.isNullOrBlank() ||
                !playlist.description.isNullOrBlank() ||
                playlist.imageUri != null
    }
}