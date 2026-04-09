package com.example.playlistmaker.media.presentation.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {
    private val viewModel: PlaylistsViewModule by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
