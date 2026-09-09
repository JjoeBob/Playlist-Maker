package com.example.playlistmaker.presentation.player

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
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val UPDATE_TIME_DELAY = 100L
    }


    private lateinit var toolbar: Toolbar
    private lateinit var albumImage: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var albumNameLabel: TextView
    private lateinit var albumNameValue: TextView
    private lateinit var releaseDateLabel: TextView
    private lateinit var releaseDateValue: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var playButton: ImageView
    private lateinit var playbackTime: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val audioPlayerInteractor = Creator.provideAudioPlayerInteractor()

    private val timeFormatter = SimpleDateFormat("m:ss", Locale.getDefault())

    private val timerRunnable = object : Runnable {
        override fun run() {
            playbackTime.text = timeFormatter.format(audioPlayerInteractor.getCurrentPosition())
            handler.postDelayed(this, UPDATE_TIME_DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.playerRoot)) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            windowInsets
        }

        toolbar = findViewById(R.id.playerToolbar)
        albumImage = findViewById(R.id.albumImage)
        trackTitle = findViewById(R.id.trackTitle)
        artistName = findViewById(R.id.artistName)
        duration = findViewById(R.id.durationValue)
        albumNameLabel = findViewById(R.id.albumLabel)
        albumNameValue = findViewById(R.id.albumValue)
        releaseDateLabel = findViewById(R.id.yearLabel)
        releaseDateValue = findViewById(R.id.yearValue)
        genre = findViewById(R.id.genreValue)
        country = findViewById(R.id.countryValue)
        playButton = findViewById(R.id.playButton)
        playbackTime = findViewById(R.id.playbackTime)

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
                playButton.isEnabled = true
            },
            onCompletionListener = {
                playButton.setImageResource(R.drawable.ic_player_play)
                handler.removeCallbacks(timerRunnable)
                playbackTime.text = getString(R.string.default_timer_value)
            },
            onErrorListener = {
                playButton.isEnabled = false
            }
        )
    }

    private fun setupPlayButton() {
        playButton.isEnabled = false
        playButton.setOnClickListener {
            audioPlayerInteractor.playbackControl(
                onStartUI = { showPlayingUI() },
                onPauseUI = { showPausedUI() }
            )
        }
    }

    private fun showPlayingUI() {
        playButton.setImageResource(R.drawable.ic_player_pause)
        handler.post(timerRunnable)
    }

    private fun showPausedUI() {
        playButton.setImageResource(R.drawable.ic_player_play)
        handler.removeCallbacks(timerRunnable)
    }

    private fun setupToolBar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindTrackData(track: Track) {
        trackTitle.text = track.trackName
        artistName.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)

        if (track.collectionName.isNullOrEmpty()) {
            albumNameLabel.isVisible = false
            albumNameValue.isVisible = false
        } else {
            albumNameLabel.isVisible = true
            albumNameValue.isVisible = true
            albumNameValue.text = track.collectionName
        }

        if (track.releaseDate.isNullOrEmpty()) {
            releaseDateLabel.isVisible = false
            releaseDateValue.isVisible = false
        } else {
            releaseDateLabel.isVisible = true
            releaseDateValue.isVisible = true
            releaseDateValue.text = track.releaseDate.take(4)
        }

        genre.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_album_312)
            .into(albumImage)
    }
}