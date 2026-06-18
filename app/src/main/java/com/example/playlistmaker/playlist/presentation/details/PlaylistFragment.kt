package com.example.playlistmaker.playlist.presentation.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.search.presentation.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private var trackAdapter: TrackAdapter? = null

    private val playlistId: Int by lazy {
        arguments?.getInt(ARGS_PLAYLIST_ID) ?: 0
    }

    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWindowInsets()

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // --- НАСТРОЙКА БОТТОМ-ШИТОВ И ЗАТЕМНЕНИЯ ---
        val bottomSheetMenuBehavior = BottomSheetBehavior.from(binding.bottomSheetMenu)
        bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetMenuBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> _binding?.overlay?.isVisible = false
                    else -> _binding?.overlay?.isVisible = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.overlay?.alpha = slideOffset + 1f
            }
        })

        binding.overlay.setOnClickListener {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.buttonMenu.setOnClickListener {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.buttonShare.setOnClickListener { sharePlaylist() }
        binding.tvShareMenu.setOnClickListener {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.tvDeleteMenu.setOnClickListener {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

        binding.tvEditMenu.setOnClickListener {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val currentPlaylist = viewModel.playlist.value
            val bundle = Bundle().apply {
                putSerializable("playlist", currentPlaylist)
            }
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                bundle
            )
        }

        trackAdapter = TrackAdapter(ArrayList()) { track ->
            val bundle = Bundle().apply {
                putParcelable("track", track)
            }
            findNavController().navigate(R.id.action_playlistFragment_to_playerFragment, bundle)
        }
        binding.rvPlaylistTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaylistTracks.adapter = trackAdapter
        trackAdapter?.onLongClickListener = { track ->
            showDeleteTrackDialog(track.trackId)
            true
        }

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            binding.tvPlaylistName.text = playlist.name
            binding.tvMenuPlaylistName.text = playlist.name
            if (playlist.description.isNullOrEmpty()) {
                binding.tvPlaylistDescription.isVisible = false
            } else {
                binding.tvPlaylistDescription.isVisible = true
                binding.tvPlaylistDescription.text = playlist.description
            }
            Glide.with(this).load(playlist.coverFilePath)
                .placeholder(R.drawable.ic_placeholder_image).centerCrop()
                .into(binding.ivPlaylistCover)
            Glide.with(this).load(playlist.coverFilePath)
                .placeholder(R.drawable.ic_placeholder_image).centerCrop()
                .into(binding.ivMenuPlaylistCover)
        }

        viewModel.duration.observe(viewLifecycleOwner) { duration ->
            val formattedMinutes = requireContext().resources.getQuantityString(
                R.plurals.minutes_plural,
                duration.toInt(),
                duration.toInt()
            )
            binding.tvPlaylistDuration.text = formattedMinutes
        }

        viewModel.trackCount.observe(viewLifecycleOwner) { count ->
            val formattedTracks =
                requireContext().resources.getQuantityString(R.plurals.tracks_plural, count, count)
            binding.tvPlaylistTracksCount.text = formattedTracks
            binding.tvMenuPlaylistTracksCount.text = formattedTracks
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            trackAdapter?.tracks = tracks
            trackAdapter?.notifyDataSetChanged()
        }

        viewModel.closeScreen.observe(viewLifecycleOwner) { shouldClose ->
            if (shouldClose) findNavController().navigateUp()
        }
    }

    private fun sharePlaylist() {
        val tracks = viewModel.tracks.value ?: emptyList()
        if (tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "В этом плейлисте нет списка треков",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val shareString = viewModel.generateShareString(requireContext().resources)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareString)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист?")
            .setPositiveButton("Да") { _, _ -> viewModel.deletePlaylist() }
            .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteTrackDialog(trackId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setPositiveButton("ДА") { _, _ -> viewModel.deleteTrack(trackId) }
            .setNegativeButton("НЕТ") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        trackAdapter = null
        _binding = null
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemBars.top,
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }
    }

    companion object {
        const val ARGS_PLAYLIST_ID = "playlist_id"
        fun createArgs(playlistId: Int): Bundle =
            Bundle().apply { putInt(ARGS_PLAYLIST_ID, playlistId) }
    }
}