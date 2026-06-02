package com.example.playlistmaker.playlist.domain.models

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val coverFilePath: String?,
    val trackIds: List<Long>,
    val trackCount: Int
)