package com.example.playlistmaker.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SearchResponse(
    val results: List<Track>
)

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    @SerializedName("artworkUrl100") val artworkUrl: String?,
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