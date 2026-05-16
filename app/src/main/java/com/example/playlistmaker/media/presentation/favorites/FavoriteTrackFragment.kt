package com.example.playlistmaker.media.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Фрагмент для отображения списка избранных треков во вкладке "Медиатека".
 */
class FavoriteTrackFragment : Fragment() {

    // Инъекция ViewModel через Koin для управления состоянием экрана избранного
    private val viewModel: FavoriteTracksViewModule by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    companion object {
        // Статический метод для создания экземпляра фрагмента (используется в MediaViewPagerAdapter)
        fun newInstance() = FavoriteTrackFragment()
    }
}