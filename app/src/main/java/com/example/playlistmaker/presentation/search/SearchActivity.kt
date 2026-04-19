package com.example.playlistmaker.presentation.search

import ItunesNetworkClient
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.network.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }

    private var searchText: String = ""

    private lateinit var toolbar: Toolbar
    private lateinit var searchField: EditText
    private lateinit var clearButton: ImageView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNoInternet: LinearLayout
    private lateinit var searchHistoryLinear: LinearLayout
    private lateinit var searchUpdateButton: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var clearHistoryButton: Button
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

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
        searchHistory = SearchHistory(this)

        setupAdapters()
        setupToolBar()
        setupSearch()
        setupClear()
        setupTracksRecycler()
        setupHistoryRecycler()
        setupSearchUpdateButton()
        setupClearHistoryButton()
    }

    private fun setupToolBar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateHistoryVisibility() {
        val hasFocus = searchField.hasFocus()
        val isTextEmpty = searchField.text.isEmpty()
        val isHistoryNotEmpty = searchHistory.getHistory().isNotEmpty()

        if (hasFocus && isTextEmpty && isHistoryNotEmpty) {
            displaySearchState(SearchState.HISTORY)
        } else if (isTextEmpty) {
            displaySearchState(SearchState.CLEAR)
        }
    }

    private fun setupSearch() {
        val searchOnFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            updateHistoryVisibility()
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()

                if (s.isNullOrEmpty()) {
                    searchAdapter.updateTracks(emptyList())
                    updateHistoryVisibility()
                } else {
                    displaySearchState(SearchState.CLEAR)
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

    private fun setupClear() {
        clearButton.setOnClickListener {
            searchField.text.clear()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchField.windowToken, 0)
            searchAdapter.updateTracks(emptyList())
            updateHistoryVisibility()
        }
    }

    private fun setupAdapters() {
        searchAdapter = TrackAdapter(emptyList()) { track ->
            searchHistory.addTrack(track)
            historyAdapter.updateTracks(searchHistory.getHistory())
        }
        historyAdapter = TrackAdapter(searchHistory.getHistory()) { }
    }

    private fun setupTracksRecycler() {
        tracksRecyclerView.adapter = searchAdapter
    }

    private fun setupHistoryRecycler() {
        searchHistoryRecyclerView.adapter = historyAdapter
    }

    private fun setupClearHistoryButton() {
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.updateTracks(emptyList())
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
            searchAdapter.updateTracks(emptyList())
            displaySearchState(SearchState.CLEAR)
            ItunesNetworkClient.itunesApi.search(searchText)
                .enqueue(object : Callback<SearchResponse> {
                    override fun onResponse(
                        call: Call<SearchResponse>,
                        response: Response<SearchResponse>
                    ) {
                        if (response.code() == 200) {
                            val results = response.body()?.results
                            if (results?.isNotEmpty() == true) {
                                searchAdapter.updateTracks(results)
                                displaySearchState(SearchState.SUCCESS)
                            } else {
                                displaySearchState(SearchState.EMPTY)
                            }
                        } else {
                            displaySearchState(SearchState.ERROR)
                        }
                    }

                    override fun onFailure(
                        call: Call<SearchResponse>,
                        t: Throwable
                    ) {
                        displaySearchState(SearchState.ERROR)
                    }

                })
        }
    }

    private enum class SearchState {
        SUCCESS, EMPTY, ERROR, CLEAR, HISTORY
    }

    private fun displaySearchState(state: SearchState) {
        placeholderNothingFound.visibility = View.GONE
        placeholderNoInternet.visibility = View.GONE
        tracksRecyclerView.visibility = View.GONE
        searchHistoryLinear.visibility = View.GONE

        when (state) {
            SearchState.SUCCESS -> tracksRecyclerView.visibility = View.VISIBLE
            SearchState.EMPTY -> placeholderNothingFound.visibility = View.VISIBLE
            SearchState.ERROR -> placeholderNoInternet.visibility = View.VISIBLE
            SearchState.HISTORY -> {
                historyAdapter.updateTracks(searchHistory.getHistory())
                searchHistoryLinear.visibility = View.VISIBLE
            }

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