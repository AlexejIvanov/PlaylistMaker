package com.example.playlistmaker.media.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTrackFragment : Fragment() {
    private val viewModel: FavoriteTracksViewModule by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites,container, false)
    }
    companion object {
        fun newInstance() = FavoriteTrackFragment()
    }
}
