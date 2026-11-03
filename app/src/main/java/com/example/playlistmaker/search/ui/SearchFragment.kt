package com.example.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.ui.SearchScreenState.Clear
import com.example.playlistmaker.search.ui.SearchScreenState.Empty
import com.example.playlistmaker.search.ui.SearchScreenState.History
import com.example.playlistmaker.search.ui.SearchScreenState.InProgress
import com.example.playlistmaker.search.ui.SearchScreenState.Success
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchAdapter()
        setupHistoryAdapter()
        setupTracksRecycler()
        setupHistoryRecycler()
        setupToolBar()
        setupSearch()
        setupClear()
        setupSearchUpdateButton()
        setupClearHistoryButton()

        viewModel.observeSearchState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onResume() {
        super.onResume()
        updateHistoryVisibility()
    }

    private fun setupToolBar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
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
            val inputMethodManager =
                requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        }
    }

    private fun setupSearchAdapter() {
        searchAdapter = TrackAdapter(emptyList()) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.onTrackClicked(track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerFragment,
                    PlayerFragment.createArgs(track)
                )
            }
        }
    }

    private fun setupHistoryAdapter() {
        historyAdapter = TrackAdapter(emptyList()) { track ->
            if (viewModel.clickDebounce()) {
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerFragment,
                    PlayerFragment.createArgs(track)
                )
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

                SearchScreenState.Error -> {
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

    private fun updateHistoryVisibility() {
        val hasFocus = binding.searchField.hasFocus()
        val isTextEmpty = binding.searchField.text.isEmpty()

        viewModel.updateHistoryVisibility(hasFocus, isTextEmpty)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}