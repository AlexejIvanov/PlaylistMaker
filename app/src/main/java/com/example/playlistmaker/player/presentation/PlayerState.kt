package com.example.playlistmaker.player.presentation

/**
 * Набор возможных состояний плеера для управления UI.
 */
sealed interface PlayerState {
    object Default : PlayerState   // Начальное состояние (до подготовки)
    object Prepared : PlayerState  // Плеер загрузил трек и готов к игре
    object Playing : PlayerState   // В процессе воспроизведения
    object Paused : PlayerState    // Воспроизведение приостановлено
}
