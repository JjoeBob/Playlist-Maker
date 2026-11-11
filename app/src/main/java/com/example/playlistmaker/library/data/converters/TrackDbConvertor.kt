package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConvertor {
    fun map(trackEntity: TrackEntity) = Track(
        trackId = trackEntity.trackId,
        trackName = trackEntity.trackName,
        artistName = trackEntity.artistName,
        trackTime = trackEntity.trackTime,
        artworkUrl = trackEntity.artworkUrl,
        collectionName = trackEntity.collectionName,
        releaseDate = trackEntity.releaseDate,
        primaryGenreName = trackEntity.primaryGenreName,
        country = trackEntity.country,
        previewUrl = trackEntity.previewUrl
    )

    fun map(track: Track) = TrackEntity(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        trackTime = track.trackTime,
        artworkUrl = track.artworkUrl,
        collectionName = track.collectionName,
        releaseDate = track.releaseDate,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        previewUrl = track.previewUrl
    )
}