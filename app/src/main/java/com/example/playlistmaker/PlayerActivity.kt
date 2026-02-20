package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.RoundedCorner
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class   PlayerActivity: AppCompatActivity() {



    //UI элементы
    private lateinit var backButton: ImageView
    private lateinit var coverImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var toPlaylistButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var toFavoriteButton: ImageView
    private lateinit var artistName: TextView
    private lateinit var playbackProgress: TextView


    //Поля данных и группы
    private lateinit var trackLength: TextView
    private lateinit var trackLengthData: TextView
    private lateinit var collectionGroup: Group
    private lateinit var collectionName: TextView
    private lateinit var collectionNameData: TextView
    private lateinit var releaseGroup: Group
    private lateinit var release: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var primaryGenreNameData: TextView
    private lateinit var country: TextView
    private lateinit var countryData: TextView

    // Проигрыватель трека
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private lateinit var handler: Handler
    private lateinit var updateProgressRunnable: Runnable



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        initViews()




        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playbackControl()
        }

        pauseButton.setOnClickListener {
            playbackControl()
        }


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

        val json = intent.getStringExtra(TRACK_KEY)
        val track = Gson().fromJson(json, Track::class.java)

        if (track != null) {
            bindTrack(track)
        }

        preparePlayer(track)

        handler = Handler(Looper.getMainLooper())

        updateProgressRunnable = object  : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING && mediaPlayer != null) {
                    val currentPosition = mediaPlayer!!.currentPosition
                    val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                    playbackProgress.text = formattedTime
                    handler.postDelayed(this, DEALY_MILLIS)
                }
            }

        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun initViews() {

        backButton = findViewById(R.id.back_button)
        coverImage = findViewById(R.id.big_cover_track)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        toPlaylistButton = findViewById(R.id.to_playlist_button)
        playButton = findViewById(R.id.play_button)
        playbackProgress = findViewById(R.id.playback_progress)
        playbackProgress.text = "00:00"
        pauseButton = findViewById(R.id.pause_button)
        toFavoriteButton = findViewById(R.id.to_favorite_button)
        trackLength = findViewById(R.id.track_length)
        trackLengthData = findViewById(R.id.track_length_data)
        collectionGroup = findViewById(R.id.collection_group)
        collectionName = findViewById(R.id.collection_name)
        collectionNameData = findViewById(R.id.collection_name_data)
        releaseGroup = findViewById(R.id.release_group)
        release = findViewById(R.id.release)
        releaseDate = findViewById(R.id.release_data)
        primaryGenreName = findViewById(R.id.primary_genre_name)
        primaryGenreNameData = findViewById(R.id.primary_genre_name_data)
        country = findViewById(R.id.country)
        countryData = findViewById(R.id.country_data)

    }

    private fun bindTrack(track: Track) {

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackLengthData.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        if(track.collectionName.isNullOrEmpty()) {
            collectionGroup.visibility = View.GONE
        } else {
            collectionGroup.visibility = View.VISIBLE
            collectionNameData.text = track.collectionName
        }

        if(track.releaseDate.isNullOrEmpty()) {
            releaseGroup.visibility = View.GONE
        } else {
            releaseGroup.visibility = View.VISIBLE
            releaseDate.text = track.releaseDate.take(4)
        }

        if (track.primaryGenreName.isNullOrEmpty()) {
            findViewById<Group>(R.id.genre_group).visibility = View.GONE
        } else {
            findViewById<Group>(R.id.genre_group).visibility = View.VISIBLE
            primaryGenreNameData.text = track.primaryGenreName
        }

        if (track.country.isNullOrEmpty()) {
            findViewById<Group>(R.id.country_group).visibility = View.GONE
        } else {
            findViewById<Group>(R.id.country_group).visibility = View.VISIBLE
            countryData.text = track.country
        }



        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_image)
            .transform(CenterCrop(), RoundedCorners(8))
            .into(coverImage)

    }

    private fun preparePlayer(track: Track){
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
            playerState = STATE_PREPARED
            playbackProgress.text = "00:00"
        }
        mediaPlayer.setOnCompletionListener {
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
            playerState = STATE_PREPARED
            playbackProgress.text = "00:00"
            stopProgressUpdate()


        }
    }
    companion object {
        const val TRACK_KEY = "TRACK_KEY"
        private const val DEALY_MILLIS = 300L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

    }

    private fun startPlayer() {
        mediaPlayer.start()
        pauseButton.visibility = View.VISIBLE
        playButton.visibility = View.GONE
        playerState = STATE_PLAYING
        startProgressUpdate()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        pauseButton.visibility = View.GONE
        playButton.visibility = View.VISIBLE
        playerState = STATE_PAUSED
        stopProgressUpdate()
    }

    private fun playbackControl() {
        when(playerState){
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startProgressUpdate() {
        handler.post(updateProgressRunnable)
    }

    private fun stopProgressUpdate() {
        handler.removeCallbacks(updateProgressRunnable)
    }
}

