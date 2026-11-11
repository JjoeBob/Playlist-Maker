package com.example.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class FavoriteTracksAdapter(
    private var favoriteTracksList: List<Track>,
    private val onClickTrack: (Track) -> Unit
) :
    RecyclerView.Adapter<FavoriteTracksAdapter.FavoriteTracksViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteTracksViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return FavoriteTracksViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FavoriteTracksViewHolder,
        position: Int
    ) {
        holder.bind(favoriteTracksList[position], onClickTrack)
    }

    override fun getItemCount(): Int {
        return favoriteTracksList.size
    }

    fun updateTracks(newTracks: List<Track>) {
        this.favoriteTracksList = newTracks
        notifyDataSetChanged()
    }

    class FavoriteTracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackTitle = itemView.findViewById<TextView>(R.id.track_title)
        val trackArtist = itemView.findViewById<TextView>(R.id.track_artist)
        val trackImage = itemView.findViewById<ImageView>(R.id.track_image)
        val trackDuration = itemView.findViewById<TextView>(R.id.track_duration)
        fun bind(track: Track, onClickTrack: (Track) -> Unit) {
            trackTitle.text = track.trackName
            trackArtist.text = track.artistName
            trackDuration.text = timeFormatter.format(track.trackTime)
            Glide.with(itemView)
                .load(track.artworkUrl)
                .fitCenter()
                .transform(RoundedCorners(2f.dpToPx(itemView.context)))
                .placeholder(R.drawable.ic_placeholder_track_45)
                .into(trackImage)

            itemView.setOnClickListener {
                onClickTrack(track)
            }

        }

        companion object {
            private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        }
    }
}

