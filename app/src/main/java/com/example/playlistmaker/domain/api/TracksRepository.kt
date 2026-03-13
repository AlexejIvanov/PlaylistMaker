package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTrack(expression: String, callback: (List<Track>?, String?) -> Unit)
}
