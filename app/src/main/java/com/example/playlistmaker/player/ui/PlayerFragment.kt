package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.library.ui.PlaylistsState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.dpToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlayerViewModel> {
        val track = BundleCompat.getSerializable(requireArguments(), ARGS_TRACK, Track::class.java)
        parametersOf(track)
    }

    private lateinit var playlistsAdapter: BottomSheetPlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeTrackUI().observe(viewLifecycleOwner) {
            bindTrackData(it)
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observePlaybackTime().observe(viewLifecycleOwner) {
            binding.playbackTime.text = it
        }

        val track = requireNotNull(
            BundleCompat.getSerializable(
                requireArguments(),
                ARGS_TRACK,
                Track::class.java
            )
        ) { "Track argument is required" }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        setupNewPlaylistButton()
        setupAdapter()
        setupPlaylistsRecycler()
        setupBottomSheet(bottomSheetBehavior)
        setupAddToPlaylistButton(bottomSheetBehavior)
        setupPlayButton()
        setupLikeButton(track)
        setupToolBar()

        viewModel.initialize()

        viewModel.observeTrackState().observe(viewLifecycleOwner) { trackState ->
            renderLikeButton(trackState)
        }

        viewModel.observePlaylistsState().observe(viewLifecycleOwner) { state ->
            renderPlaylists(state)
        }
    }

    private fun setupNewPlaylistButton() {
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_playlistFragment)
        }
    }

    private fun renderPlaylists(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> {
                playlistsAdapter.updatePlaylists(state.playlists)
            }

            PlaylistsState.Empty -> {
                playlistsAdapter.updatePlaylists(emptyList())
            }
        }
    }

    private fun setupAdapter() {
        playlistsAdapter = BottomSheetPlaylistsAdapter(emptyList()) { }
    }

    private fun setupPlaylistsRecycler() {
        binding.playlistsRecyclerView.adapter = playlistsAdapter
    }

    private fun setupBottomSheet(bottomSheetBehavior: BottomSheetBehavior<LinearLayout>) {
        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.root.doOnLayout {
            bottomSheetBehavior.peekHeight = (it.height * 0.63f).toInt()
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(p0: View, p1: Float) {}

        })
    }

    private fun setupAddToPlaylistButton(bottomSheetBehavior: BottomSheetBehavior<LinearLayout>) {
        binding.addToPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.loadPlaylists()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun renderLikeButton(trackState: TrackState) {
        binding.apply {
            when (trackState) {
                TrackState.Default -> {
                    likeButton.setIconResource(R.drawable.ic_player_like)
                    likeButton.setIconTintResource(R.color.yp_white)
                }

                TrackState.Favorite -> {
                    likeButton.setIconResource(R.drawable.ic_player_liked)
                    likeButton.setIconTintResource(R.color.yp_red)
                }
            }
        }
    }

    private fun setupLikeButton(track: Track) {
        binding.likeButton.setOnClickListener {
            viewModel.onLikeButtonClicked(track)
        }
    }

    private fun setupPlayButton() {
        binding.playButton.isEnabled = false
        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    private fun setupToolBar() {
        binding.playerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bindTrackData(track: TrackPlayerUI) {
        binding.apply {
            trackTitle.text = track.trackName
            artistName.text = track.artistName
            durationValue.text = track.duration

            val hasAlbum = !track.albumName.isNullOrEmpty()
            albumNameLabel.isVisible = hasAlbum
            albumNameValue.isVisible = hasAlbum
            albumNameValue.text = track.albumName

            val hasReleaseYear = !track.releaseYear.isNullOrEmpty()
            releaseDateLabel.isVisible = hasReleaseYear
            releaseDateValue.isVisible = hasReleaseYear
            releaseDateValue.text = track.releaseYear

            genreValue.text = track.genre
            countryValue.text = track.country

            Glide.with(requireActivity())
                .load(track.coverArtworkUrl)
                .placeholder(R.drawable.ic_placeholder_album_312)
                .transform(
                    CenterCrop(),
                    RoundedCorners(8f.dpToPx(requireContext()))
                )
                .into(albumImage)
        }
    }

    private fun render(state: PlayerScreenState) {
        binding.apply {
            when (state) {
                PlayerScreenState.Default -> {
                    playButton.isEnabled = false
                    playButton.setImageResource(R.drawable.ic_player_play)
                }

                PlayerScreenState.Prepared,
                PlayerScreenState.Completed,
                PlayerScreenState.Paused -> {
                    playButton.setImageResource(R.drawable.ic_player_play)
                    playButton.isEnabled = true
                }

                PlayerScreenState.Playing -> {
                    playButton.setImageResource(R.drawable.ic_player_pause)
                    playButton.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARGS_TRACK = "track"
        fun createArgs(track: Track) = bundleOf(ARGS_TRACK to track)
    }
}