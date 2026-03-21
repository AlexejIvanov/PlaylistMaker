package com.example.playlistmaker.search.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import org.koin.android.ext.android.inject
import com.google.gson.Gson
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.player.presentation.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Экран поиска треков: отображает результаты поиска, историю запросов и ошибки сети.
 */
class SearchActivity : AppCompatActivity() {

    private val gson: Gson by inject() // Инъекция Gson (через Koin)
    private val viewModel: SearchViewModel by viewModel() // Инъекция ViewModel (через Koin)

    // Элементы управления и ввода
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var refreshButton: Button
    private lateinit var clearHistoryButton: Button

    // Списки (RecyclerView)
    private lateinit var recyclerViewTracks: RecyclerView
    private lateinit var recyclerViewHistory: RecyclerView

    // Плейсхолдеры и состояния
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNetworkError: LinearLayout
    private lateinit var historyLayout: LinearLayout
    private lateinit var progressBar: ProgressBar

    // Списки данных и адаптеры
    private val trackList = mutableListOf<Track>()
    private val historyList = mutableListOf<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        setupAdapters()
        setupListeners()
        setupWindowInsets()

        // Подписка на LiveData состояния экрана из ViewModel
        viewModel.state.observe(this) { state ->
            renderState(state)
        }

        searchEditText.requestFocus() // Автофокус на поле ввода при открытии
    }

    /**
     * Управляет видимостью элементов экрана в зависимости от текущего состояния (Loading, Content, Error и т.д.)
     */
    private fun renderState(state: SearchScreenState) {
        progressBar.isVisible = state is SearchScreenState.Loading
        recyclerViewTracks.isVisible = state is SearchScreenState.Content
        placeholderNothingFound.isVisible = state is SearchScreenState.Empty
        placeholderNetworkError.isVisible = state is SearchScreenState.Error
        historyLayout.isVisible = state is SearchScreenState.History

        when (state) {
            is SearchScreenState.Content -> {
                trackList.clear()
                trackList.addAll(state.tracks)
                trackAdapter.notifyDataSetChanged()
            }
            is SearchScreenState.History -> {
                historyList.clear()
                historyList.addAll(state.tracks)
                historyAdapter.notifyDataSetChanged()
            }
            else -> {}
        }
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            viewModel.showHistory() // При очистке поля показываем историю
        }

        refreshButton.setOnClickListener {
            viewModel.search(searchEditText.text.toString()) // Повторный запрос при ошибке
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        // Показ истории, если поле ввода пустое и получило фокус
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel.showHistory()
            }
        }

        // Слушатель изменения текста с вызовом Debounce-поиска
        searchEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.isVisible = !text.isNullOrEmpty()
            viewModel.searchDebounce(text?.toString() ?: "")
        }
    }

    /**
     * Инициализация адаптеров для основного поиска и для истории.
     */
    private fun setupAdapters() {
        // Адаптер результатов поиска
        trackAdapter = TrackAdapter(trackList) { track ->
            if (viewModel.clickDebounce()) { // Защита от множественных кликов
                viewModel.addTrackToHistory(track)
                openPlayer(track)
            }
        }
        recyclerViewTracks.layoutManager = LinearLayoutManager(this)
        recyclerViewTracks.adapter = trackAdapter

        // Адаптер истории поиска
        historyAdapter = TrackAdapter(historyList) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.addTrackToHistory(track) // Обновляем позицию в истории
                openPlayer(track)
            }
        }
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewHistory.adapter = historyAdapter
    }

    /**
     * Переход на экран плеера с передачей объекта трека.
     */
    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.TRACK_KEY, track)
        }
        startActivity(intent)
    }

    /**
     * Скрытие мягкой клавиатуры.
     */
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_button)
        clearButton = findViewById(R.id.clear_button)
        searchEditText = findViewById(R.id.search_edit_text)

        recyclerViewTracks = findViewById(R.id.recycler_view_track)
        recyclerViewHistory = findViewById(R.id.recycler_view_history)

        placeholderNothingFound = findViewById(R.id.placeholder_nothing_found)
        placeholderNetworkError = findViewById(R.id.placeholder_network_error)
        refreshButton = findViewById(R.id.refresh_button)

        historyLayout = findViewById(R.id.history_layout)
        clearHistoryButton = findViewById(R.id.clear_button_history)
        progressBar = findViewById(R.id.progress_bar)
    }

    /**
     * Настройка отступов для корректного отображения за системными панелями.
     */
    private fun setupWindowInsets() {
        val density = resources.displayMetrics.density
        val sidePadding = (16 * density).toInt()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.search)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                sidePadding + systemBars.left,
                systemBars.top,
                sidePadding + systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }
}