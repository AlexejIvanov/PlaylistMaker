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
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Адаптер для отображения списка треков в RecyclerView.
 * Принимает список данных и лямбда-функцию для обработки клика.
 */
class TrackAdapter(
    private val tracks: List<Track>,
    private val clickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    /**
     * ViewHolder: хранит ссылки на UI-элементы одного элемента списка для переиспользования.
     */
    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImage: ImageView = itemView.findViewById(R.id.CoverTack)
        val trackName: TextView = itemView.findViewById(R.id.TrackName)
        val artistName: TextView = itemView.findViewById(R.id.ArtistName)
        val trackTime: TextView = itemView.findViewById(R.id.TrackTime)

        /**
         * Привязывает данные модели Track к конкретным View.
         */
        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            // Форматирование времени трека из миллисекунд в "00:00"
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            // Загрузка обложки: используем Glide для обрезки (CenterCrop) и скругления углов
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.ic_placeholder_image)
                .centerCrop()
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_cover_corner_radius)))
                .into(coverImage)
        }
    }

    // Создание нового объекта ViewHolder на основе XML-макета элемента
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
        return TrackViewHolder(view)
    }

    // Наполнение View данными из списка по позиции и установка слушателя клика
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            clickListener.invoke(tracks[position])
        }
    }

    // Возвращает общее количество элементов в списке
    override fun getItemCount(): Int = tracks.size
}