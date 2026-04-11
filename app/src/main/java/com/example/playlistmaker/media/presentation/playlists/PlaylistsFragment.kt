package com.example.playlistmaker.media.presentation.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Фрагмент для отображения списка плейлистов во вкладке "Медиатека".
 */
class PlaylistsFragment: Fragment() {

    // Инъекция ViewModel через Koin для управления данными и логикой плейлистов
    private val viewModel: PlaylistsViewModule by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    companion object {
        // Статический метод для создания экземпляра фрагмента (используется в MediaViewPagerAdapter)
        fun newInstance() = PlaylistsFragment()
    }
}
