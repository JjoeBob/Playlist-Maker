package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.SearchScreenState.Clear
import com.example.playlistmaker.search.ui.SearchScreenState.Empty
import com.example.playlistmaker.search.ui.SearchScreenState.Error
import com.example.playlistmaker.search.ui.SearchScreenState.History
import com.example.playlistmaker.search.ui.SearchScreenState.InProgress
import com.example.playlistmaker.search.ui.SearchScreenState.Success

class SearchActivity : AppCompatActivity() {

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.getFactory()
    }

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

        setupSearchAdapter()
        setupHistoryAdapter()
        setupTracksRecycler()
        setupHistoryRecycler()
        setupToolBar()
        setupSearch()
        setupClear()
        setupSearchUpdateButton()
        setupClearHistoryButton()

        viewModel.observeSearchState().observe(this) {
            render(it)
        }
    }

    override fun onResume() {
        super.onResume()
        updateHistoryVisibility()
    }

    private fun setupToolBar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateHistoryVisibility() {
        val hasFocus = binding.searchField.hasFocus()
        val isTextEmpty = binding.searchField.text.isEmpty()

        viewModel.updateHistoryVisibility(hasFocus, isTextEmpty)
    }

    private fun setupSearch() {

        binding.searchField.setOnFocusChangeListener { _, _ ->
            updateHistoryVisibility()
        }
        binding.searchField.doOnTextChanged { text, _, _, _ ->
            binding.clearButton.isVisible = !text.isNullOrEmpty()

            viewModel.searchDebounce(text?.toString().orEmpty())
        }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }
    }

    private fun setupClear() {
        binding.clearButton.setOnClickListener {
            binding.searchField.text.clear()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        }
    }

    private fun setupSearchAdapter() {
        searchAdapter = TrackAdapter(emptyList()) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.onTrackClicked(track)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("track", track)
                startActivity(playerIntent)
            }
        }
    }

    private fun setupHistoryAdapter() {
        historyAdapter = TrackAdapter(emptyList()) { track ->
            if (viewModel.clickDebounce()) {
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
            viewModel.clearHistory()
        }
    }

    private fun setupSearchUpdateButton() {
        binding.searchUpdateButton.setOnClickListener {
            search()
        }
    }

    private fun search() {
        viewModel.search(binding.searchField.text.toString())
    }

    private fun render(searchState: SearchScreenState) {
        binding.apply {
            placeholderNothingFound.visibility = View.GONE
            placeholderNoInternet.visibility = View.GONE
            tracksRecyclerView.visibility = View.GONE
            searchHistoryLinear.visibility = View.GONE
            progressBar.visibility = View.GONE

            when (searchState) {
                Empty -> {
                    placeholderNothingFound.visibility = View.VISIBLE
                }

                Error -> {
                    placeholderNoInternet.visibility = View.VISIBLE
                }

                is History -> {
                    historyAdapter.updateTracks(searchState.searchHistory)
                    searchHistoryLinear.visibility = View.VISIBLE
                }

                InProgress -> {
                    progressBar.visibility = View.VISIBLE
                    searchAdapter.updateTracks(emptyList())
                }

                is Success -> {
                    tracksRecyclerView.visibility = View.VISIBLE
                    searchAdapter.updateTracks(searchState.newTracks)
                }

                Clear -> {
                    historyAdapter.updateTracks(emptyList())
                    searchAdapter.updateTracks(emptyList())
                }
            }
        }
    }
}
