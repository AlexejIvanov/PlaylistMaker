package com.example.playlistmaker.data.dto

data class ITunesResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()
