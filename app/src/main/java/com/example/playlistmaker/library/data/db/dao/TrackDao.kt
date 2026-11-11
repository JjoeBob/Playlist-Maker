package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table ORDER BY addedAt DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE trackId = :trackId)")
    suspend fun checkId(trackId: Long): Boolean
}