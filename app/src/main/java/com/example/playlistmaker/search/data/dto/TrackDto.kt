package com.example.playlistmaker.search.data.dto
import com.google.gson.annotations.SerializedName

/**
 * DTO-модель трека для десериализации JSON-ответа от iTunes API.
 * Аннотация @SerializedName связывает имя поля в JSON с переменной в Kotlin.
 */
data class TrackDto(
    @SerializedName("trackId") val trackId: Long, // Уникальный ID трека
    @SerializedName("trackName") val trackName: String, // Название композиции
    @SerializedName("artistName") val artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long, // Длительность в миллисекундах
    @SerializedName("artworkUrl100") val artworkUrl100: String, // Ссылка на обложку (100x100)
    @SerializedName("collectionName") val collectionName: String?, // Название альбома (может отсутствовать)
    @SerializedName("releaseDate") val releaseDate: String?, // Дата выхода (может отсутствовать)
    @SerializedName("primaryGenreName") val primaryGenreName: String, // Основной жанр
    @SerializedName("country") val country: String, // Страна
    @SerializedName("previewUrl") val previewUrl: String? // Ссылка на 30-секундный фрагмент (может отсутствовать)
)
