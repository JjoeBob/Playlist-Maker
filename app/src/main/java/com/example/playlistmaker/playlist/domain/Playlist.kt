package com.example.playlistmaker.playlist.domain

import android.net.Uri

data class Playlist(
    var imageUri: Uri?,
    var name: String?,
    var description: String?
)
