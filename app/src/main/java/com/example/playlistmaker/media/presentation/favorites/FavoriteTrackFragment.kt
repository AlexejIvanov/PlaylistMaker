package com.example.playlistmaker.media.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.search.presentation.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Фрагмент для отображения списка избранных треков во вкладке "Медиатека".
 */
class FavoriteTrackFragment : Fragment() {

    private val viewModel: FavoriteTracksViewModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholder: LinearLayout

    private val trackList = mutableListOf<Track>()
    private val adapter = TrackAdapter(trackList) { track ->
        openPlayer(track)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.favorites_recycler_view)
        placeholder = view.findViewById(R.id.placeholder_empty_favorites)

        recyclerView.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Content -> showContent(state.tracks)
            is FavoriteState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        recyclerView.isVisible = false
        placeholder.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        placeholder.isVisible = false
        recyclerView.isVisible = true

        trackList.clear()
        trackList.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_playerFragment,
            bundleOf("track" to track)
        )
    }

    companion object {
        fun newInstance() = FavoriteTrackFragment()
    }
}
