package com.example.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.playlist.domain.Playlist
import com.example.playlistmaker.utils.dpToPx

class PlaylistsAdapter(
    private var playlists: List<Playlist>,
    private val onClickPlaylist: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsViewHolder {
        return PlaylistsViewHolder.from(parent)
    }

    override fun onBindViewHolder(
        holder: PlaylistsViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position], onClickPlaylist)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        this.playlists = newPlaylists
        notifyDataSetChanged()
    }

    class PlaylistsViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: Playlist, onClickPlaylist: (Playlist) -> Unit) {
            with(binding) {
                playlistName.text = playlist.name
                playlistTrackCount.text = root.resources.getQuantityString(
                    R.plurals.playlist_track_count,
                    playlist.trackCount,
                    playlist.trackCount
                )
                Glide.with(root)
                    .load(playlist.imageUri)
                    .transform(CenterCrop(), RoundedCorners(8f.dpToPx(root.context)))
                    .placeholder(R.drawable.ic_placeholder_playlist_160)
                    .into(playlistImage)

                root.setOnClickListener {
                    onClickPlaylist(playlist)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): PlaylistsViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPlaylistBinding.inflate(inflater, parent, false)
                return PlaylistsViewHolder(binding)
            }
        }
    }


}