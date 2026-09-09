package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import androidx.core.content.edit

class SearchHistoryRepositoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {
    companion object {
        const val SEARCH_HISTORY_PREFS = "search_history_preferences"
        const val KEY_HISTORY = "key_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }

        sharedPrefs.edit { putString(KEY_HISTORY, gson.toJson(history)) }
    }

    override fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(KEY_HISTORY, null) ?: return emptyList<Track>()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    override fun clearHistory() {
        sharedPrefs.edit { putString(KEY_HISTORY, gson.toJson((emptyList<Track>()))) }
    }
}