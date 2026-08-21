package com.example.playlistmaker.player.ui

data class TrackPlayerUI(
    val trackName: String,
    val artistName: String,
    val duration: String,
    val albumName: String?,
    val releaseYear: String?,
    val genre: String,
    val country: String,
    val coverArtworkUrl: String?
)
