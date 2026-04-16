package com.example.playlistmaker.network

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val results: ArrayList<Track>
)

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    @SerializedName("artworkUrl100") val artworkUrl: String?
)