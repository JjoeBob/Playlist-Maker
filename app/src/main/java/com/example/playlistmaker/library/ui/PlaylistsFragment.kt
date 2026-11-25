package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.playlist.domain.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var playlistsAdapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_playlistFragment)
        }

        setupAdapter()
        setupPlaylistsRecycler()

        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun setupAdapter() {
        playlistsAdapter = PlaylistsAdapter(emptyList()) { }
    }

    private fun setupPlaylistsRecycler() {
        binding.playlists.adapter = playlistsAdapter
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            PlaylistsState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.playlists.visibility = View.GONE
        binding.emptyPlaceholderLinear.visibility = View.VISIBLE
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.emptyPlaceholderLinear.visibility = View.GONE
        binding.playlists.visibility = View.VISIBLE
        playlistsAdapter.updatePlaylists(playlists)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}