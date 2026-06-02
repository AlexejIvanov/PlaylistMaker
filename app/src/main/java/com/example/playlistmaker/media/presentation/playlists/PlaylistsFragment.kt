package com.example.playlistmaker.media.presentation.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager // <-- ИМПОРТ
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private val adapter = PlaylistAdapter()

    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newPlaylistBtn = view.findViewById<Button>(R.id.new_playlist_button)
        recyclerView = view.findViewById(R.id.recyclerView)
        placeholderContainer = view.findViewById(R.id.placeholder_container)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        newPlaylistBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsState.Empty -> {
                    recyclerView.visibility = View.GONE
                    placeholderContainer.visibility = View.VISIBLE
                }
                is PlaylistsState.Content -> {
                    adapter.playlists = state.playlists
                    recyclerView.visibility = View.VISIBLE
                    placeholderContainer.visibility = View.GONE
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom, // Если фрагмент под навигацией, bottom можно не трогать или корректировать
                left = systemBars.left,
                right = systemBars.right
            )
            insets
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}