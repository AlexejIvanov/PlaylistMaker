package com.example.playlistmaker

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Track(
    @SerializedName("trackName") val trackName: String, // Название композиции
    @SerializedName("artistName") val artistName: String, //Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long, // Время трека в миллисекундах
    @SerializedName("artworkUrl100") val artworkUrl100: String, //Ссылка на изображение обложки
    @SerializedName("trackId") val trackId: Long, // Уникальный идентификатор трека
    @SerializedName("collectionName") val collectionName: String?, //Название альбома
    @SerializedName("releaseDate") val releaseDate: String?, //Год релиза трека
    @SerializedName("primaryGenreName") val primaryGenreName: String, //Жанр трека
    @SerializedName("country") val country: String, //Страна исполнителя
    @SerializedName("previewUrl") val previewUrl: String
) : Serializable
{
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
