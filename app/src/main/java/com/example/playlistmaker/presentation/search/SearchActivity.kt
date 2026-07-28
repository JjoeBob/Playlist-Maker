package com.example.playlistmaker.presentation.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerActivity

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

    private lateinit var toolbar: Toolbar
    private lateinit var searchField: EditText
    private lateinit var clearButton: ImageView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNoInternet: LinearLayout
    private lateinit var searchHistoryLinear: LinearLayout
    private lateinit var searchUpdateButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            windowInsets
        }

        toolbar = findViewById(R.id.search_toolbar)
        searchField = findViewById(R.id.search_field)
        clearButton = findViewById(R.id.search_field_clear)
        tracksRecyclerView = findViewById(R.id.search_recycler_view)
        searchHistoryRecyclerView = findViewById(R.id.search_history_recycler_view)
        placeholderNothingFound = findViewById(R.id.placeholder_nothing_found)
        placeholderNoInternet = findViewById(R.id.placeholder_no_internet)
        searchHistoryLinear = findViewById(R.id.search_history)
        searchUpdateButton = findViewById(R.id.search_update_button)
        clearHistoryButton = findViewById(R.id.clear_search_history)
        progressBar = findViewById(R.id.progress_bar)

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
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateHistoryVisibility() {
        val hasFocus = searchField.hasFocus()
        val isTextEmpty = searchField.text.isEmpty()
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
                clearButton.isVisible = !s.isNullOrEmpty()
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

        searchField.onFocusChangeListener = searchOnFocusChangeListener
        searchField.addTextChangedListener(searchTextWatcher)

        searchField.setOnEditorActionListener { _, actionId, _ ->
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
        clearButton.setOnClickListener {
            searchField.text.clear()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchField.windowToken, 0)
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
        tracksRecyclerView.adapter = searchAdapter
    }

    private fun setupHistoryRecycler() {
        searchHistoryRecyclerView.adapter = historyAdapter
    }

    private fun setupClearHistoryButton() {
        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            historyAdapter.updateTracks(emptyList<Track>())
            displaySearchState(SearchState.CLEAR)
        }
    }

    private fun setupSearchUpdateButton() {
        searchUpdateButton.setOnClickListener {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")

        searchField.setText(searchText)
    }
}
