package com.example.playlistmaker.search.ui

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val tracksInteractor: TracksInteractor,
    private val handler: Handler
) : ViewModel() {
    private var isClickAllowed = true

    private val searchRunnable = Runnable {
        val text = latestSearchText
        if (!text.isNullOrEmpty()) {
            search(text)
        }
    }
    private var latestSearchText: String? = null
    private var searchDebounceJob: Job? = null

    private val searchStateLiveData = MutableLiveData<SearchScreenState>()
    fun observeSearchState(): LiveData<SearchScreenState> = searchStateLiveData

    fun search(searchText: String) {
        if (searchText.isBlank()) return

        handler.removeCallbacks(searchRunnable)

        searchStateLiveData.value = SearchScreenState.InProgress

        viewModelScope.launch {
            tracksInteractor.searchTracks(searchText)
                .collect { foundTracks ->
                    processResult(foundTracks)
                }
        }
    }

    private fun processResult(foundTracks: List<Track>?) {
        when {
            foundTracks == null -> searchStateLiveData.value = SearchScreenState.Error
            foundTracks.isNotEmpty() -> searchStateLiveData.value =
                SearchScreenState.Success(foundTracks)

            else -> searchStateLiveData.value = SearchScreenState.Empty
        }
    }

    fun updateHistoryVisibility(hasFocus: Boolean, isTextEmpty: Boolean) {
        val searchHistory = searchHistoryInteractor.getHistory()

        if (hasFocus && isTextEmpty && searchHistory.isNotEmpty()) {
            searchStateLiveData.value = SearchScreenState.History(searchHistory)
        } else if (isTextEmpty) {
            searchStateLiveData.value = SearchScreenState.Clear
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY.milliseconds)
                isClickAllowed = true
            }
        }
        return current
    }

    fun searchDebounce(changedText: String) {
        searchDebounceJob?.cancel()
        if (changedText.isNotEmpty()) {
            searchDebounceJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY.milliseconds)
                if (changedText != latestSearchText) {
                    latestSearchText = changedText
                    val text = latestSearchText
                    if (!text.isNullOrEmpty()) {
                        search(text)
                    }
                }
            }
        } else {
            val history = searchHistoryInteractor.getHistory()
            if (history.isNotEmpty()) {
                searchStateLiveData.value = SearchScreenState.History(history)
                latestSearchText = null
            } else {
                searchStateLiveData.value = SearchScreenState.Clear
                latestSearchText = null
            }
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        searchStateLiveData.value = SearchScreenState.Clear
    }

    fun onTrackClicked(track: Track) {
        searchHistoryInteractor.addTrack(track)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
        tracksInteractor.cancelSearch()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}