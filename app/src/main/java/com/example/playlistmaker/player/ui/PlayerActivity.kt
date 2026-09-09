package com.example.playlistmaker.player.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.search.domain.models.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            windowInsets
        }

        val track = intent.getSerializableExtra("track") as? Track ?: return

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getFactory(track)
        ).get(PlayerViewModel::class.java)

        viewModel.observeTrackUI().observe(this) {
            bindTrackData(it)
        }

        viewModel.observePlayerState().observe(this) {
            render(it)
        }

        viewModel.observePlaybackTime().observe(this) {
            binding.playbackTime.text = it
        }

        setupPlayButton()
        setupToolBar()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun setupPlayButton() {
        binding.playButton.isEnabled = false
        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    private fun setupToolBar() {
        binding.playerToolbar.setNavigationOnClickListener {
            finish()
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

            Glide.with(this@PlayerActivity)
                .load(track.coverArtworkUrl)
                .placeholder(R.drawable.ic_placeholder_album_312)
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
}