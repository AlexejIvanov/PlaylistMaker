package com.example.playlistmaker.core.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Доменная модель трека.
 * Реализует Parcelable для возможности передачи объекта между Activity через Intent.
 */
@Parcelize
data class Track(
    val trackName: String,         // Название композиции
    val artistName: String,        // Имя исполнителя
    val trackTimeMillis: Long,     // Продолжительность трека (мс)
    val artworkUrl100: String,     // Ссылка на обложку (100x100)
    val trackId: Long,             // Уникальный ID трека
    val collectionName: String?,   // Название альбома
    val releaseDate: String?,      // Дата выхода
    val primaryGenreName: String,  // Жанр
    val country: String,           // Страна
    val previewUrl: String         // Ссылка на аудио-превью (30 сек)
) : Parcelable {

    // Метод для получения ссылки на обложку в высоком разрешении (512x512)
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}