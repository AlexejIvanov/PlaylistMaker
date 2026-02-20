package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

// Адаптер для связывания данных (списка треков) с RecyclerView
class TrackAdapter(
    private val tracks: List<Track>, private val clickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    // ViewHolder хранит ссылки на View-элементы одного элемента списка
    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImage: ImageView = itemView.findViewById(R.id.CoverTack)
        val trackName: TextView = itemView.findViewById(R.id.TrackName)
        val artistName: TextView = itemView.findViewById(R.id.ArtistName)
        val trackTime: TextView = itemView.findViewById(R.id.TrackTime)

        // Метод для заполнения View данными конкретного трека
        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            // Загрузка изображения через Glide с обработкой (CenterCrop и закругление углов)
            Glide.with(itemView.context).load(track.artworkUrl100)
                .placeholder(R.drawable.ic_placeholder_image).centerCrop().transform(
                    RoundedCorners(
                        itemView.resources.getDimensionPixelSize(
                            R.dimen.track_cover_corner_radius
                        )
                    )
                ).into(coverImage)
        }
    }

    // Создание нового ViewHolder (раздувание макета track_layout)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
        return TrackViewHolder(view)
    }

    // Привязка данных к ViewHolder в зависимости от позиции в списке
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            clickListener.invoke(tracks[position])
        }
    }

    // Возвращает количество элементов в списке
    override fun getItemCount(): Int = tracks.size
}