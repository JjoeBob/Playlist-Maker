package com.example.playlistmaker.search.domain.models

import java.io.Serializable

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val artworkUrl: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Serializable {
    fun getCoverArtwork(): String? {
        return artworkUrl?.replaceAfterLast('/', "512x512bb.jpg")
    }
}