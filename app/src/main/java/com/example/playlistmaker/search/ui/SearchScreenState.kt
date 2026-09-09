package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchScreenState {
    data class Success(val newTracks: List<Track>): SearchScreenState
    data object Empty: SearchScreenState
    data object Error: SearchScreenState
    data object Clear: SearchScreenState
    data class History(val searchHistory: List<Track>): SearchScreenState
    data object InProgress: SearchScreenState
}