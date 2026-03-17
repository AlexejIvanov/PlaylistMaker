package com.example.playlistmaker.domain.models

import java.io.Serializable

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, //Имя исполнителя
    val trackTimeMillis: Long, // Время трека в миллисекундах
    val artworkUrl100: String, //Ссылка на изображение обложки
    val trackId: Long, // Уникальный идентификатор трека
    val collectionName: String?, //Название альбома
    val releaseDate: String?, //Год релиза трека
    val primaryGenreName: String, //Жанр трека
    val country: String, //Страна исполнителя
    val previewUrl: String
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
