package com.example.playlistmaker.player.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Фрагмент экрана аудиоплеера.
 * Управляет отображением данных трека и состоянием воспроизведения.
 */
class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel()

    private lateinit var backButton: ImageView
    private lateinit var coverImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var favoriteButton: ImageView
    private lateinit var currentTime: TextView
    private lateinit var collectionGroup: Group

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupWindowInsets(view)

        // Извлечение данных трека из аргументов навигации
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("TRACK_KEY", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("TRACK_KEY")
        }

        if (track != null) {
            bindTrackData(track)
            // Инициализация плеера только при первом создании (защита от пересоздания фрагмента)
            if (viewModel.state.value is PlayerState.Default) {
                viewModel.preparePlayer(track)
            }
        } else {
            findNavController().popBackStack()
        }

        // Подписки на LiveData: состояние плеера и текущее время таймера
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.timer.observe(viewLifecycleOwner) { time ->
            currentTime.text = time
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            renderFavorite(isFavorite)
        }

        // Навигация назад и управление воспроизведением
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        playButton.setOnClickListener { viewModel.play() }
        pauseButton.setOnClickListener { viewModel.pause() }
        favoriteButton.setOnClickListener { viewModel.onFavoriteClicked() }
    }

    private fun initViews(view: View) {
        backButton = view.findViewById(R.id.back_button)
        coverImage = view.findViewById(R.id.big_cover_track)
        trackName = view.findViewById(R.id.track_name)
        artistName = view.findViewById(R.id.artist_name)
        trackTime = view.findViewById(R.id.track_length_data)
        collectionName = view.findViewById(R.id.collection_name_data)
        releaseDate = view.findViewById(R.id.release_data)
        primaryGenreName = view.findViewById(R.id.primary_genre_name_data)
        country = view.findViewById(R.id.country_data)
        playButton = view.findViewById(R.id.play_button)
        pauseButton = view.findViewById(R.id.pause_button)
        favoriteButton = view.findViewById(R.id.to_favorite_button)
        currentTime = view.findViewById(R.id.playback_progress)
        collectionGroup = view.findViewById(R.id.collection_group)
    }

    private fun setupWindowInsets(view: View) {
        val playerScrollView = view.findViewById<View>(R.id.player_scroll_view)

        ViewCompat.setOnApplyWindowInsetsListener(playerScrollView) { v, insets ->
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

    // Обновление видимости и доступности кнопок управления в зависимости от состояния плеера.

    private fun renderState(state: PlayerState) {
        when (state) {
            PlayerState.Default -> {
                playButton.isEnabled = false
                playButton.isVisible = true
                pauseButton.isVisible = false
            }
            PlayerState.Prepared, PlayerState.Paused -> {
                playButton.isEnabled = true
                playButton.isVisible = true
                pauseButton.isVisible = false
            }
            PlayerState.Playing -> {
                playButton.isVisible = false
                pauseButton.isVisible = true
            }
        }
    }

    private fun renderFavorite(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_is_favorite_51x51)
        } else {
            favoriteButton.setImageResource(R.drawable.ic_add_to_favorites_51x51)
        }
    }

    // Отображение статических данных трека в элементах интерфейса.
      private fun bindTrackData(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        // Скрытие группы Альбом, если данные отсутствуют
        if (track.collectionName.isNullOrEmpty()) {
            collectionGroup.isVisible = false
        } else {
            collectionGroup.isVisible = true
            collectionName.text = track.collectionName
        }

        releaseDate.text = track.releaseDate?.take(4) ?: ""
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        // Загрузка обложки в высоком разрешении
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_image)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)))
            .into(coverImage)
    }

    override fun onPause() {
        super.onPause()
        // Принудительная пауза при сворачивании приложения или уходе с экрана
        viewModel.pause()
    }
}
