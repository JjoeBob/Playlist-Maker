package com.example.playlistmaker.presentation.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.network.Track
import com.example.playlistmaker.utils.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(private var trackList: List<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun updateTracks(newTracks: List<Track>) {
        this.trackList = newTracks
        notifyDataSetChanged()
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        }

        val trackTitle = itemView.findViewById<TextView>(R.id.track_title)
        val trackArtist = itemView.findViewById<TextView>(R.id.track_artist)
        val trackImage = itemView.findViewById<ImageView>(R.id.track_image)
        val trackDuration = itemView.findViewById<TextView>(R.id.track_duration)
        fun bind(track: Track) {
            trackTitle.text = track.trackName
            trackArtist.text = track.artistName
            trackDuration.text = timeFormatter.format(track.trackTime)
            Glide.with(itemView)
                .load(track.artworkUrl)
                .fitCenter()
                .transform(RoundedCorners(2f.dpToPx(itemView.context)))
                .placeholder(R.drawable.ic_placeholder_track_45)
                .into(trackImage)
        }
    }
}