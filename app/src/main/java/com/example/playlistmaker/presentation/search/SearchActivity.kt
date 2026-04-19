package com.example.playlistmaker.presentation.search

import ItunesNetworkClient
import android.os.Bundle
import android.text.Editable
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
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNoInternet: LinearLayout
    private lateinit var searchUpdateButton: Button
    private val adapter = TrackAdapter(emptyList())

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
        placeholderNothingFound = findViewById(R.id.placeholder_nothing_found)
        placeholderNoInternet = findViewById(R.id.placeholder_no_internet)
        searchUpdateButton = findViewById(R.id.search_update_button)

        setupToolBar()
        setupSearch()
        setupClear()
        setupTracksRecycler()
        setupSearchUpdateButton()
    }

    private fun setupToolBar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupSearch() {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()

                if (s.isNullOrEmpty()) {
                    adapter.updateTracks(emptyList())
                    displaySearchResult(SearchState.CLEAR)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

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
            searchField.clearFocus()
            adapter.updateTracks(emptyList())
            displaySearchResult(SearchState.CLEAR)
        }
    }

    private fun setupTracksRecycler() {
        tracksRecyclerView.adapter = adapter
    }

    private fun setupSearchUpdateButton() {
        searchUpdateButton.setOnClickListener {
            search()
        }
    }

    private fun search() {
        if (searchText.isNotEmpty()) {
            adapter.updateTracks(emptyList())
            displaySearchResult(SearchState.CLEAR)
            ItunesNetworkClient.itunesApi.search(searchText)
                .enqueue(object : Callback<SearchResponse> {
                    override fun onResponse(
                        call: Call<SearchResponse>,
                        response: Response<SearchResponse>
                    ) {
                        if (response.code() == 200) {
                            val results = response.body()?.results
                            if (results?.isNotEmpty() == true) {
                                adapter.updateTracks(results)
                                displaySearchResult(SearchState.SUCCESS)
                            } else {
                                displaySearchResult(SearchState.EMPTY)
                            }
                        } else {
                            displaySearchResult(SearchState.ERROR)
                        }
                    }

                    override fun onFailure(
                        call: Call<SearchResponse>,
                        t: Throwable
                    ) {
                        displaySearchResult(SearchState.ERROR)
                    }

                })
        }
    }

    private enum class SearchState {
        SUCCESS, EMPTY, ERROR, CLEAR
    }

    private fun displaySearchResult(state: SearchState) {
        when (state) {
            SearchState.SUCCESS -> {
                placeholderNothingFound.visibility = View.GONE
                placeholderNoInternet.visibility = View.GONE
                tracksRecyclerView.visibility = View.VISIBLE
            }

            SearchState.EMPTY -> {
                placeholderNothingFound.visibility = View.VISIBLE
                placeholderNoInternet.visibility = View.GONE
                tracksRecyclerView.visibility = View.GONE
            }

            SearchState.ERROR -> {
                placeholderNothingFound.visibility = View.GONE
                placeholderNoInternet.visibility = View.VISIBLE
                tracksRecyclerView.visibility = View.GONE
            }

            SearchState.CLEAR -> {
                placeholderNothingFound.visibility = View.GONE
                placeholderNoInternet.visibility = View.GONE
                tracksRecyclerView.visibility = View.GONE
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

        searchField.setText(searchText)
    }

}