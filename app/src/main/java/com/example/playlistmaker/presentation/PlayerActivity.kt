package com.example.playlistmaker.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_KEY = "TRACK_KEY"
        private const val UPDATE_DELAY = 300L
    }

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

    // Группы
    private lateinit var collectionGroup: Group

    // Логика
    private lateinit var playerInteractor: PlayerInteractor
    private var mainThreadHandler: Handler? = null
    private var timerRunnable: Runnable? = null

    // Состояние
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        initViews()
        setupWindowInsets()
        playerInteractor = Creator.providePlayerInteractor()
        mainThreadHandler = Handler(Looper.getMainLooper())


        val json = intent.getStringExtra(TRACK_KEY)
        val track = Gson().fromJson(json, Track::class.java)

        if (track != null) {
            bindTrackData(track)
            preparePlayer(track.previewUrl)
        } else {
            finish()
        }

        backButton.setOnClickListener { finish() }
        playButton.setOnClickListener { startPlayer() }
        pauseButton.setOnClickListener { pausePlayer() }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releasePlayer()
        timerRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
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
        currentTime.text = "00:00"
    }

    private fun bindTrackData(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

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

    private fun preparePlayer(url: String) {
        playButton.isEnabled = false

        playerInteractor.preparePlayer(url, object : PlayerInteractor.PlayerPreparedListener {
            override fun onPrepared() {
                playButton.isEnabled = true
                playButton.isVisible = true
                pauseButton.isVisible = false
            }
        })

        playerInteractor.setOnCompletionListener {
            isPlaying = false
            playButton.isVisible = true
            pauseButton.isVisible = false
            currentTime.text = "00:00"
            timerRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        isPlaying = true
        playButton.isVisible = false
        pauseButton.isVisible = true
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        isPlaying = false
        playButton.isVisible = true
        pauseButton.isVisible = false
        timerRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
    }

    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                if (isPlaying) {
                    val currentPosition = playerInteractor.getCurrentPosition()
                    currentTime.text =
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                    mainThreadHandler?.postDelayed(this, UPDATE_DELAY)
                }
            }
        }
        mainThreadHandler?.post(timerRunnable!!)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<android.view.View>(R.id.player)) { view, insets ->
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
