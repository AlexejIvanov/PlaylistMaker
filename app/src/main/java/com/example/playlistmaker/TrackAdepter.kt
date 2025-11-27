package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdepter (
    private val tracks: List<Track>
) : RecyclerView.Adapter<TrackAdepter.TrackViewHolder>() {


    class TrackViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImage: ImageView = itemView.findViewById(R.id.CoverTack)
        val trackName: TextView = itemView.findViewById(R.id.TrackName)
        val artistName: TextView = itemView.findViewById(R.id.ArtistName)
        val trackTime : TextView  =itemView.findViewById(R.id.TrackTime)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime

            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder_image)
                .centerCrop()
                .transform(RoundedCorners(
                    itemView.resources.getDimensionPixelSize(
                        R.dimen.track_cover_corner_radius)
                )
                )
                .into(coverImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent,false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size
}