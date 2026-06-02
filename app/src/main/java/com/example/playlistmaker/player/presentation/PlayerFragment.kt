package com.example.playlistmaker.player.presentation

import android.os.Build
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Фрагмент экрана аудиоплеера.
 * Управляет отображением данных трека, состоянием воспроизведения и добавлением в плейлисты.
 */
class PlayerFragment : Fragment() {

    private var currentTrack: Track? = null

    // Инициализация ViewModel с передачей URL трека
    private val viewModel: PlayerViewModel by viewModel()

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("track")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWindowInsets()


        // Проверяем, удалось ли получить трек
        val track = currentTrack
        if (track == null) {
            findNavController().popBackStack()
            return
        }

        bindTrackData(track)

        // Инициализация плеера только при первом создании (защита от пересоздания фрагмента)
        if (viewModel.state.value is PlayerState.Default) {
            viewModel.preparePlayer(track)
        }

        // Настройка Bottom Sheet и Overlay
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> _binding?.overlay?.visibility = View.GONE
                    else -> _binding?.overlay?.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.overlay?.alpha = (slideOffset + 1) / 2f
            }
        })

        // Настройка адаптера списка плейлистов
        val playlistAdapter = BottomSheetPlaylistAdapter { playlist ->
            android.util.Log.d("MY_DEBUG", "Fragment: клик по ${playlist.name}")
            currentTrack?.let { track ->
                viewModel.addTrackToPlaylist(track, playlist)
            }
        }
        binding.rvBottomSheetPlaylists.adapter = playlistAdapter

        // Подписки на LiveData плеера
        viewModel.state.observe(viewLifecycleOwner) { state -> renderState(state) }
        viewModel.timer.observe(viewLifecycleOwner) { time -> binding.playbackProgress.text = time }
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite -> renderFavorite(isFavorite) }

        // Подписки на LiveData плейлистов
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.playlists = playlists
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                if (message.startsWith("Добавлено")) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

        // Обработчики кликов
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.playButton.setOnClickListener { viewModel.play() }
        binding.pauseButton.setOnClickListener { viewModel.pause() }
        binding.toFavoriteButton.setOnClickListener { viewModel.onFavoriteClicked() }

        // Клик по кнопке "Добавить в плейлист"
        binding.addToPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // Клик по кнопке "Новый плейлист" в Bottom Sheet
        binding.btnNewPlaylistBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            // Упаковываем текущий трек
            val bundle = Bundle().apply {
                putParcelable("track", currentTrack)
            }
            // Передаем bundle при навигации
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment, bundle)
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.playerScrollView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom,
                left = systemBars.left,
                right = systemBars.right,
            )
            insets
        }
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            PlayerState.Default -> {
                binding.playButton.isEnabled = false
                binding.playButton.isVisible = true
                binding.pauseButton.isVisible = false
            }
            PlayerState.Prepared, PlayerState.Paused -> {
                binding.playButton.isEnabled = true
                binding.playButton.isVisible = true
                binding.pauseButton.isVisible = false
            }
            PlayerState.Playing -> {
                binding.playButton.isVisible = false
                binding.pauseButton.isVisible = true
            }
        }
    }

    private fun renderFavorite(isFavorite: Boolean) {
        if (isFavorite) {
            binding.toFavoriteButton.setImageResource(R.drawable.ic_is_favorite_51x51)
        } else {
            binding.toFavoriteButton.setImageResource(R.drawable.ic_add_to_favorites_51x51)
        }
    }

    private fun bindTrackData(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackLengthData.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        if (track.collectionName.isNullOrEmpty()) {
            binding.collectionGroup.isVisible = false
        } else {
            binding.collectionGroup.isVisible = true
            binding.collectionNameData.text = track.collectionName
        }

        binding.releaseData.text = track.releaseDate?.take(4) ?: ""
        binding.primaryGenreNameData.text = track.primaryGenreName
        binding.countryData.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_image)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)))
            .into(binding.bigCoverTrack)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
