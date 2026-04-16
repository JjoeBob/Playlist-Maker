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
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.utils.dpToPx

class TrackAdapter(val trackList: ArrayList<Track>) :
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

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackTitle = itemView.findViewById<TextView>(R.id.track_title)
        val trackArtist = itemView.findViewById<TextView>(R.id.track_artist)
        val trackImage = itemView.findViewById<ImageView>(R.id.track_image)
        val trackDuration = itemView.findViewById<TextView>(R.id.track_duration)
        fun bind(track: Track) {
            trackTitle.text = track.trackName
            trackArtist.text = track.artistName
            trackDuration.text = track.trackTime
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .fitCenter()
                .transform(RoundedCorners(2f.dpToPx(itemView.context)))
                .placeholder(R.drawable.ic_placeholder_track_45)
                .into(trackImage)
        }
    }
}