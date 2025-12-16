package com.example.playlistmaker

data class ITunesResponse(
    val resultCount: Int,
    val results: List<Track>
)