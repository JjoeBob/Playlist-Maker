package com.example.playlistmaker.network

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val results: List<Track>
)

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    @SerializedName("artworkUrl100") val artworkUrl: String?
)