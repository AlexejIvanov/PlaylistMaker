package com.example.playlistmaker.player.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.playlist.domain.models.Playlist
import java.io.File

class BottomSheetPlaylistAdapter(
    private val onPlaylistClicked: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistAdapter.PlaylistViewHolder>() {

    var playlists = listOf<Playlist>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        // Передаем функцию клика прямо в bind
        holder.bind(playlists[position], onPlaylistClicked)
    }

    override fun getItemCount() = playlists.size

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cover = itemView.findViewById<ImageView>(R.id.bs_playlist_cover)
        private val name = itemView.findViewById<TextView>(R.id.bs_playlist_name)
        private val count = itemView.findViewById<TextView>(R.id.bs_track_count)

        fun bind(playlist: Playlist, onPlaylistClicked: (Playlist) -> Unit) {
            name.text = playlist.name

            // Получаем правильное склонение слова "трек"
            count.text = itemView.resources.getQuantityString(
                R.plurals.track_count, playlist.trackCount, playlist.trackCount
            )

            val file = playlist.coverFilePath?.let { File(it) }
            Glide.with(itemView).load(file)
                .placeholder(R.drawable.ic_placeholder_image)
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.padding_small)))
                .into(cover)

            itemView.setOnClickListener {
                onPlaylistClicked(playlist)
            }
        }
    }
}