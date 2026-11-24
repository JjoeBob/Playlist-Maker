package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.models.AudioPlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class PlayerViewModel(
    private val track: Track,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val timeFormatter: SimpleDateFormat
) : ViewModel() {
    private var isInitialized = false
    private var timerJob: Job? = null

    private val playbackTimeLiveData = MutableLiveData("0:00")
    fun observePlaybackTime(): LiveData<String> = playbackTimeLiveData

    private val playerStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Default)
    fun observePlayerState(): LiveData<PlayerScreenState> = playerStateLiveData

    private val trackStateLiveData = MutableLiveData<TrackState>()
    fun observeTrackState(): LiveData<TrackState> = trackStateLiveData

    private val trackUILiveData = MutableLiveData<TrackPlayerUI>()
    fun observeTrackUI(): LiveData<TrackPlayerUI> = trackUILiveData

    fun initialize() {
        if (isInitialized) return

        checkTrack(track.trackId)
        audioPlayerInteractor.setOnStateChangeListener { playerState ->
            val uiState = when (playerState) {
                AudioPlayerState.PREPARED -> {
                    pauseTimer()
                    playbackTimeLiveData.postValue("0:00")
                    PlayerScreenState.Prepared
                }

                AudioPlayerState.DEFAULT, AudioPlayerState.ERROR -> {
                    pauseTimer()
                    PlayerScreenState.Default
                }

                AudioPlayerState.PLAYING -> {
                    startTimer()
                    PlayerScreenState.Playing
                }

                AudioPlayerState.PAUSED -> {
                    pauseTimer()
                    PlayerScreenState.Paused
                }

                AudioPlayerState.COMPLETED -> {
                    pauseTimer()
                    playbackTimeLiveData.postValue("0:00")
                    PlayerScreenState.Completed
                }
            }
            playerStateLiveData.postValue(uiState)
        }

        preparePlayer(track)
        trackUILiveData.postValue(mapToUI(track))

        isInitialized = true
    }

    private fun checkTrack(trackId: Long) {
        viewModelScope.launch {
            favoriteTracksInteractor.checkId(trackId)
                .collect { isFavorite ->
                    when (isFavorite) {
                        true -> trackStateLiveData.value = TrackState.Favorite
                        false -> trackStateLiveData.value = TrackState.Default
                    }
                }
        }
    }

    private fun mapToUI(track: Track): TrackPlayerUI {
        return TrackPlayerUI(
            trackName = track.trackName,
            artistName = track.artistName,
            duration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime),
            albumName = track.collectionName,
            releaseYear = track.releaseDate?.take(4),
            genre = track.primaryGenreName ?: "",
            country = track.country ?: "",
            coverArtworkUrl = track.getCoverArtwork()
        )
    }

    private fun preparePlayer(track: Track) {
        val url = track.previewUrl
        audioPlayerInteractor.preparePlayer(url)
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            PlayerScreenState.Playing -> pausePlayer()
            PlayerScreenState.Prepared, PlayerScreenState.Paused, PlayerScreenState.Completed -> startPlayer()
            else -> {}
        }
    }

    fun onLikeButtonClicked(track: Track) {
        viewModelScope.launch {
            when (trackStateLiveData.value) {
                TrackState.Default -> {
                    favoriteTracksInteractor.likeTrack(track)
                    trackStateLiveData.postValue(TrackState.Favorite)
                }

                TrackState.Favorite -> {
                    favoriteTracksInteractor.removeFromFavorites(track)
                    trackStateLiveData.postValue(TrackState.Default)
                }

                null -> {}
            }
        }
    }

    private fun startPlayer() {
        audioPlayerInteractor.startPlayer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (audioPlayerInteractor.isPlaying()) {
                delay(UPDATE_TIME_DELAY.milliseconds)
                playbackTimeLiveData.postValue(timeFormatter.format(audioPlayerInteractor.getCurrentPosition()))
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
    }

    private fun releasePlayer() {
        audioPlayerInteractor.releasePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        releasePlayer()
    }

    companion object {
        private const val UPDATE_TIME_DELAY = 300L
    }
}