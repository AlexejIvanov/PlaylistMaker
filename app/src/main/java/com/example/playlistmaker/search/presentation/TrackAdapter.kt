package com.example.playlistmaker.search.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.core.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    var tracks: List<Track> = emptyList(),
    private val clickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    var onLongClickListener: ((Track) -> Boolean)? = null

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImage: ImageView = itemView.findViewById(R.id.CoverTack)
        val trackName: TextView = itemView.findViewById(R.id.TrackName)
        val artistName: TextView = itemView.findViewById(R.id.ArtistName)
        val trackTime: TextView = itemView.findViewById(R.id.TrackTime)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.ic_placeholder_image)
                .centerCrop()
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_cover_corner_radius)))
                .into(coverImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            clickListener.invoke(track)
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener?.invoke(track) ?: false
        }
    }

    override fun getItemCount(): Int = tracks.size
}