package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    override fun prepare(url: String, onPrepared: () -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { onPrepared() }
    }

    override fun start() = mediaPlayer.start()
    override fun pause() = mediaPlayer.pause()
    override fun release() = mediaPlayer.release()
    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition
    override fun isPlaying(): Boolean = mediaPlayer.isPlaying
    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener { listener() }
    }
}
