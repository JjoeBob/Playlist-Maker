package com.example.playlistmaker.playlist.data

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.playlist.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.Playlist
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID

class PlaylistRepositoryImpl(
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val appDatabase: AppDatabase,
    private val context: Context
) : PlaylistRepository {
    private suspend fun saveImage(uri: Uri): Uri =
        withContext(Dispatchers.IO) {
            val directory = File(context.filesDir, "playlist_covers")
            directory.mkdirs()

            val file = File(directory, UUID.randomUUID().toString())

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Failed to open input stream for URI: $uri")

            inputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Uri.fromFile(file)
        }

    override suspend fun savePlaylist(playlist: Playlist) {
        val savedImageUri = playlist.imageUri?.let {
            saveImage(it)
        }

        val savedPlaylist = playlist.copy(imageUri = savedImageUri)

        val playlistEntity = playlistDbConvertor.map(savedPlaylist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromTrackEntity(playlists))
    }

    private fun convertFromTrackEntity(playlists: List<PlaylistEntity>) =
        playlists.map { playlist -> playlistDbConvertor.map(playlist) }
}
