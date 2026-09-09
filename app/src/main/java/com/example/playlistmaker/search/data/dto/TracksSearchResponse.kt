package com.example.playlistmaker.search.data.dto

data class TracksSearchResponse(
    val results: List<TrackDto>
) : Response()