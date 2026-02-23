package com.example.playlistmaker.domain.api

interface PlayerInteractor {
    fun preparePlayer(url: String, listener: PlayerPreparedListener)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun setOnCompletionListener(listener: () -> Unit)

    interface PlayerPreparedListener {
        fun onPrepared()
    }
}
