package com.example.playlistmaker.presentation.player

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_KEY = "TRACK_KEY"
    }

    private val viewModel: PlayerViewModel by viewModel()

    // UI
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
    private lateinit var currentTime: TextView
    private lateinit var collectionGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        initViews()
        setupWindowInsets()

        val json = intent.getStringExtra(TRACK_KEY)
        val track = Gson().fromJson(json, Track::class.java)

        if (track != null) {
            bindTrackData(track)
            if (savedInstanceState == null) {
                viewModel.preparePlayer(track.previewUrl)
            }
        } else {
            finish()
        }

        // Подписываемся на состояния плеера
        viewModel.state.observe(this) { state ->
            when (state) {
                PlayerState.Default -> {
                    playButton.isEnabled = false
                    playButton.isVisible = true
                    pauseButton.isVisible = false
                }
                PlayerState.Prepared -> {
                    playButton.isEnabled = true
                    playButton.isVisible = true
                    pauseButton.isVisible = false
                }
                PlayerState.Playing -> {
                    playButton.isVisible = false
                    pauseButton.isVisible = true
                }
                PlayerState.Paused -> {
                    playButton.isVisible = true
                    pauseButton.isVisible = false
                }
            }
        }

        // Подписываемся на таймер
        viewModel.timer.observe(this) { time ->
            currentTime.text = time
        }

        backButton.setOnClickListener { finish() }
        playButton.setOnClickListener { viewModel.play() }
        pauseButton.setOnClickListener { viewModel.pause() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_button)
        coverImage = findViewById(R.id.big_cover_track)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        trackTime = findViewById(R.id.track_length_data)
        collectionName = findViewById(R.id.collection_name_data)
        releaseDate = findViewById(R.id.release_data)
        primaryGenreName = findViewById(R.id.primary_genre_name_data)
        country = findViewById(R.id.country_data)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)
        currentTime = findViewById(R.id.playback_progress)
        collectionGroup = findViewById(R.id.collection_group)
    }

    private fun bindTrackData(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        if (track.collectionName.isNullOrEmpty()) {
            collectionGroup.isVisible = false
        } else {
            collectionGroup.isVisible = true
            collectionName.text = track.collectionName
        }

        releaseDate.text = track.releaseDate?.take(4) ?: ""
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_image)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)))
            .into(coverImage)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.player)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }
}
