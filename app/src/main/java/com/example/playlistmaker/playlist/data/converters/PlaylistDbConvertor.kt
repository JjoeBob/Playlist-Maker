package com.example.playlistmaker.playlist.data.converters

import androidx.core.net.toUri
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.Playlist

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity = PlaylistEntity(
        name = playlist.name,
        description = playlist.description,
        imageUri = playlist.imageUri?.toString()
    )

    fun map(playlistEntity: PlaylistEntity): Playlist = Playlist(
        name = playlistEntity.name,
        description = playlistEntity.description,
        imageUri = playlistEntity.imageUri?.toUri()
    )
}