package com.example.playlistmaker.media.presentation.playlists

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.playlist.domain.models.Playlist
import java.io.File

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val coverImage: ImageView = itemView.findViewById(R.id.playlist_cover)
    private val nameText: TextView = itemView.findViewById(R.id.playlist_name)
    private val trackCountText: TextView = itemView.findViewById(R.id.track_count)

    fun bind(playlist: Playlist) {
        nameText.text = playlist.name
        trackCountText.text = itemView.resources.getQuantityString(
            R.plurals.tracks_plural,
            playlist.trackCount,
            playlist.trackCount
        )

        val coverFile = playlist.coverFilePath?.let { File(it) }

        Glide.with(itemView)
            .load(coverFile)
            .placeholder(R.drawable.ic_placeholder_image)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.padding_small)))
            .into(coverImage)
    }
}
