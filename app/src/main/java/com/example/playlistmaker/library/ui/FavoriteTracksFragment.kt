package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {
    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteTracksViewModel by viewModel()
    private lateinit var favoriteTracksAdapter: FavoriteTracksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupFavoriteTracksRecycler()

        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun setupAdapter() {
        favoriteTracksAdapter = FavoriteTracksAdapter(emptyList()) { track ->
            if (viewModel.clickDebounce()) {
                findNavController().navigate(
                    R.id.action_favoriteTracksFragment_to_playerFragment,
                    PlayerFragment.createArgs(track)
                )
            }
        }
    }

    private fun setupFavoriteTracksRecycler() {
        binding.favoriteTracks.adapter = favoriteTracksAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showContent(state: FavoriteTracksState.Content) {
        binding.emptyPlaceholder.visibility = View.GONE
        binding.favoriteTracks.visibility = View.VISIBLE
        favoriteTracksAdapter.updateTracks(state.tracks)
    }

    private fun showEmpty() {
        binding.favoriteTracks.visibility = View.GONE
        binding.emptyPlaceholder.visibility = View.VISIBLE
    }


    private fun render(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.Content -> showContent(state)
            is FavoriteTracksState.Empty -> showEmpty()
        }
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}