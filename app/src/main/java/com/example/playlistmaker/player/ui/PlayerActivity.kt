package com.example.playlistmaker.player.ui

import android.R.attr.country
import android.R.attr.duration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val UPDATE_TIME_DELAY = 100L
    }

    private lateinit var binding: ActivityPlayerBinding

    private val handler = Handler(Looper.getMainLooper())
    private val audioPlayerInteractor = Creator.provideAudioPlayerInteractor()

    private val timeFormatter = SimpleDateFormat("m:ss", Locale.getDefault())

    private val timerRunnable = object : Runnable {
        override fun run() {
            binding.playbackTime.text = timeFormatter.format(audioPlayerInteractor.getCurrentPosition())
            handler.postDelayed(this, UPDATE_TIME_DELAY)
        }
    }

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

        setupPlayButton()
        preparePlayer(track)
        setupToolBar()
        bindTrackData(track)
    }

    override fun onPause() {
        super.onPause()
        audioPlayerInteractor.pausePlayer()
        showPausedUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerInteractor.releasePlayer()
        handler.removeCallbacks(timerRunnable)
    }

    private fun preparePlayer(track: Track) {
        val url = track.previewUrl

        audioPlayerInteractor.preparePlayer(
            url,
            onPreparedListener = {
                binding.playButton.isEnabled = true
            },
            onCompletionListener = {
                binding.playButton.setImageResource(R.drawable.ic_player_play)
                handler.removeCallbacks(timerRunnable)
                binding.playbackTime.text = getString(R.string.default_timer_value)
            },
            onErrorListener = {
                binding.playButton.isEnabled = false
            }
        )
    }

    private fun setupPlayButton() {
        binding.playButton.isEnabled = false
        binding.playButton.setOnClickListener {
            audioPlayerInteractor.playbackControl(
                onStartUI = { showPlayingUI() },
                onPauseUI = { showPausedUI() }
            )
        }
    }

    private fun showPlayingUI() {
        binding.playButton.setImageResource(R.drawable.ic_player_pause)
        handler.post(timerRunnable)
    }

    private fun showPausedUI() {
        binding.playButton.setImageResource(R.drawable.ic_player_play)
        handler.removeCallbacks(timerRunnable)
    }

    private fun setupToolBar() {
        binding.playerToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindTrackData(track: Track) {
        binding.trackTitle.text = track.trackName
        binding.artistName.text = track.artistName
        binding.durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)

        if (track.collectionName.isNullOrEmpty()) {
            binding.albumNameLabel.isVisible = false
            binding.albumNameValue.isVisible = false
        } else {
            binding.albumNameLabel.isVisible = true
            binding.albumNameValue.isVisible = true
            binding.albumNameValue.text = track.collectionName
        }

        if (track.releaseDate.isNullOrEmpty()) {
            binding.releaseDateLabel.isVisible = false
            binding.releaseDateValue.isVisible = false
        } else {
            binding.releaseDateLabel.isVisible = true
            binding.releaseDateValue.isVisible = true
            binding.releaseDateValue.text = track.releaseDate.take(4)
        }

        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_album_312)
            .into(binding.albumImage)
    }
}