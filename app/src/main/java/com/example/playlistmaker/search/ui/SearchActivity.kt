package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var searchText = ""
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val searchHistoryInteractor by lazy { Creator.provideSearchHistoryInteractor(this) }
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var isClickAllowed = true
    private val clickRunnable = Runnable { isClickAllowed = true }

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            windowInsets
        }

        setupAdapters()
        setupToolBar()
        setupSearch()
        setupClear()
        setupTracksRecycler()
        setupHistoryRecycler()
        setupSearchUpdateButton()
        setupClearHistoryButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
        handler.removeCallbacks(clickRunnable)
        tracksInteractor.cancelSearch()
    }

    private fun setupToolBar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateHistoryVisibility() {
        val hasFocus = binding.searchField.hasFocus()
        val isTextEmpty = binding.searchField.text.isEmpty()
        val isHistoryNotEmpty = searchHistoryInteractor.getHistory()
            .isNotEmpty()

        if (hasFocus && isTextEmpty && isHistoryNotEmpty) {
            displaySearchState(SearchState.HISTORY)
        } else if (isTextEmpty) {
            displaySearchState(SearchState.CLEAR)
        }
    }

    private fun setupSearch() {
        val searchOnFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            updateHistoryVisibility()
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()

                if (s.isNullOrEmpty()) {
                    handler.removeCallbacks(searchRunnable)
                    tracksInteractor.cancelSearch()

                    searchAdapter.updateTracks(emptyList<Track>())
                    updateHistoryVisibility()
                } else {
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        binding.searchField.onFocusChangeListener = searchOnFocusChangeListener
        binding.searchField.addTextChangedListener(searchTextWatcher)

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (searchText.isNotEmpty()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    private fun setupClear() {
        binding.clearButton.setOnClickListener {
            binding.searchField.text.clear()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
            searchAdapter.updateTracks(emptyList<Track>())
            updateHistoryVisibility()
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(clickRunnable, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun setupAdapters() {
        searchAdapter = TrackAdapter(emptyList<Track>()) { track ->
            if (clickDebounce()) {
                searchHistoryInteractor.addTrack(track)
                historyAdapter.updateTracks(searchHistoryInteractor.getHistory())
                val playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("track", track)
                startActivity(playerIntent)
            }
        }
        historyAdapter = TrackAdapter(searchHistoryInteractor.getHistory()) { track ->
            if (clickDebounce()) {
                val playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("track", track)
                startActivity(playerIntent)
            }
        }
    }

    private fun setupTracksRecycler() {
        binding.tracksRecyclerView.adapter = searchAdapter
    }

    private fun setupHistoryRecycler() {
        binding.searchHistoryRecyclerView.adapter = historyAdapter
    }

    private fun setupClearHistoryButton() {
        binding.clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            historyAdapter.updateTracks(emptyList<Track>())
            displaySearchState(SearchState.CLEAR)
        }
    }

    private fun setupSearchUpdateButton() {
        binding.searchUpdateButton.setOnClickListener {
            search()
        }
    }

    private fun search() {
        if (searchText.isNotEmpty()) {
            handler.removeCallbacks(searchRunnable)

            searchAdapter.updateTracks(emptyList<Track>())
            displaySearchState(SearchState.IN_PROGRESS)

            tracksInteractor.searchTracks(searchText) { foundTracks ->
                handler.post {
                    if (foundTracks == null) {
                        displaySearchState(SearchState.ERROR)
                    } else if (foundTracks.isNotEmpty()) {
                        searchAdapter.updateTracks((foundTracks))
                        displaySearchState(SearchState.SUCCESS)
                    } else {
                        displaySearchState(SearchState.EMPTY)
                    }
                }
            }
        }
    }

    private enum class SearchState {
        SUCCESS, EMPTY, ERROR, CLEAR, HISTORY, IN_PROGRESS
    }

    private fun displaySearchState(state: SearchState) {
        binding.apply {
            placeholderNothingFound.visibility = View.GONE
            placeholderNoInternet.visibility = View.GONE
            tracksRecyclerView.visibility = View.GONE
            searchHistoryLinear.visibility = View.GONE
            progressBar.visibility = View.GONE

            when (state) {
                SearchState.SUCCESS -> tracksRecyclerView.visibility = View.VISIBLE
                SearchState.EMPTY -> placeholderNothingFound.visibility = View.VISIBLE
                SearchState.ERROR -> placeholderNoInternet.visibility = View.VISIBLE
                SearchState.HISTORY -> {
                    historyAdapter.updateTracks(searchHistoryInteractor.getHistory())
                    searchHistoryLinear.visibility = View.VISIBLE
                }

                SearchState.IN_PROGRESS -> progressBar.visibility = View.VISIBLE
                SearchState.CLEAR -> {}
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")

        binding.searchField.setText(searchText)
    }
}
