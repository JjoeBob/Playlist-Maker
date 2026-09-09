package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.app.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(private val searchHistoryInteractor: SearchHistoryInteractor) : ViewModel() {
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val handler = Handler(Looper.getMainLooper())

    private var latestSearchText: String? = null
    private var isClickAllowed = true

    private val clickRunnable = Runnable { isClickAllowed = true }
    private val searchRunnable = Runnable {
        val text = latestSearchText
        if (!text.isNullOrEmpty()) {
            search(text)
        }
    }

    private val searchStateLiveData = MutableLiveData<SearchScreenState>()
    fun observeSearchState(): LiveData<SearchScreenState> = searchStateLiveData

    fun search(searchText: String) {
        if (searchText.isBlank()) return

        handler.removeCallbacks(searchRunnable)

        searchStateLiveData.value = SearchScreenState.InProgress

        tracksInteractor.searchTracks(searchText) { foundTracks ->
            handler.post {
                when {
                    foundTracks == null -> searchStateLiveData.value = SearchScreenState.Error
                    foundTracks.isNotEmpty() -> searchStateLiveData.value =
                        SearchScreenState.Success(foundTracks)

                    else -> searchStateLiveData.value = SearchScreenState.Empty
                }
            }
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
            handler.postDelayed(clickRunnable, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun searchDebounce(changedText: String) {
        latestSearchText = changedText
        handler.removeCallbacks(searchRunnable)

        if (changedText.isNotEmpty()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        } else {
            val history = searchHistoryInteractor.getHistory()
            if (history.isNotEmpty()) {
                searchStateLiveData.value = SearchScreenState.History(history)
            } else {
                searchStateLiveData.value = SearchScreenState.Clear
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
        handler.removeCallbacks(clickRunnable)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App
                val historyInteractor = Creator.provideSearchHistoryInteractor(app)
                SearchViewModel(historyInteractor)
            }
        }
    }
}