package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistHorizontalBinding
import com.example.playlistmaker.playlist.domain.Playlist
import com.example.playlistmaker.utils.dpToPx

class BottomSheetPlaylistsAdapter(
    private var playlists: List<Playlist>,
    private val onClickPlaylist: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistsAdapter.BottomSheetPlaylistsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlaylistsViewHolder {
        return BottomSheetPlaylistsViewHolder.from(parent)
    }

    override fun onBindViewHolder(
        holder: BottomSheetPlaylistsViewHolder,
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

    class BottomSheetPlaylistsViewHolder(private val binding: ItemPlaylistHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: Playlist, onClickPlaylist: (Playlist) -> Unit) {
            with(binding) {
                playlistName.text = playlist.name
                trackCount.text = root.resources.getQuantityString(
                    R.plurals.playlist_track_count,
                    playlist.trackCount,
                    playlist.trackCount
                )
                Glide.with(root)
                    .load(playlist.imageUri)
                    .fitCenter()
                    .transform(RoundedCorners(2f.dpToPx(root.context)))
                    .placeholder(R.drawable.ic_placeholder_album_45)
                    .into(playlistImage)
                root.setOnClickListener { onClickPlaylist(playlist) }
            }
        }

        companion object {
            fun from(parent: ViewGroup): BottomSheetPlaylistsViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPlaylistHorizontalBinding.inflate(inflater, parent, false)
                return BottomSheetPlaylistsViewHolder(binding)
            }
        }
    }
}