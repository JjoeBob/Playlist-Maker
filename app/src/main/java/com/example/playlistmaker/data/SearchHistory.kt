package com.example.playlistmaker.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.data.network.Track
import com.google.gson.Gson

class SearchHistory(private val context: Context) {
    companion object {
        const val SEARCH_HISTORY_PREFS = "search_history_preferences"
        const val KEY_HISTORY = "key_history"
        private const val  MAX_HISTORY_SIZE = 10
    }

    private val sharedPrefs = context.getSharedPreferences(SEARCH_HISTORY_PREFS, MODE_PRIVATE)
    private val gson = Gson()

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }

        sharedPrefs.edit().putString(KEY_HISTORY, gson.toJson(history)).apply()
    }

    fun getHistory() : List<Track> {
        val json = sharedPrefs.getString(KEY_HISTORY, null) ?: return emptyList<Track>()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    fun clearHistory() {
        sharedPrefs.edit().putString(KEY_HISTORY, gson.toJson((emptyList<Track>()))).apply()
    }
}