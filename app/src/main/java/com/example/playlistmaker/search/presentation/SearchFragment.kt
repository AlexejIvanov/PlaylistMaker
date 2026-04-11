package com.example.playlistmaker.search.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Фрагмент экрана поиска: управление вводом, отображение результатов и истории запросов.
 */
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var refreshButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var recyclerViewTracks: RecyclerView
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNetworkError: LinearLayout
    private lateinit var historyLayout: LinearLayout
    private lateinit var progressBar: ProgressBar

    private val trackList = mutableListOf<Track>()
    private val historyList = mutableListOf<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupAdapters()
        setupListeners()
        setupWindowInsets(view)

        // Подписка на реактивное изменение состояний экрана (Loading, Content, Error, History)
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun initViews(view: View) {
        searchEditText = view.findViewById(R.id.search_edit_text)
        clearButton = view.findViewById(R.id.clear_button)
        recyclerViewTracks = view.findViewById(R.id.recycler_view_track)
        recyclerViewHistory = view.findViewById(R.id.recycler_view_history)
        placeholderNothingFound = view.findViewById(R.id.placeholder_nothing_found)
        placeholderNetworkError = view.findViewById(R.id.placeholder_network_error)
        refreshButton = view.findViewById(R.id.refresh_button)
        historyLayout = view.findViewById(R.id.history_layout)
        clearHistoryButton = view.findViewById(R.id.clear_button_history)
        progressBar = view.findViewById(R.id.progress_bar)
    }

    private fun setupWindowInsets(view: View) {
        val rootElement = view.findViewById<View>(R.id.search_screen)

        ViewCompat.setOnApplyWindowInsetsListener(rootElement) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }
    }

    // Настройка адаптеров для списков поиска и истории.

    private fun setupAdapters() {
        // Основной список результатов
        trackAdapter = TrackAdapter(trackList) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.addTrackToHistory(track)
                openPlayer(track)
            }
        }
        recyclerViewTracks.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewTracks.adapter = trackAdapter

        // Список последних запросов (история)
        historyAdapter = TrackAdapter(historyList) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.addTrackToHistory(track)
                openPlayer(track)
            }
        }
        recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewHistory.adapter = historyAdapter
    }

    // Привязка действий к элементам управления: очистка, повтор запроса, логика ввода.
    private fun setupListeners() {
        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            viewModel.showHistory()
        }

        refreshButton.setOnClickListener {
            viewModel.search(searchEditText.text.toString())
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        // Показ истории при фокусе на пустом поле ввода
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel.showHistory()
            }
        }

        // Слушатель изменений текста для запуска Debounce-поиска
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounce(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        searchEditText.addTextChangedListener(textWatcher)
    }


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

    // Навигация к плееру с передачей выбранного трека через аргументы Navigation Component.

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
            bundleOf("TRACK_KEY" to track)
        )
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Очистка TextWatcher и ссылок на View для предотвращения утечек памяти
        textWatcher?.let { searchEditText.removeTextChangedListener(it) }
        textWatcher = null
    }
}
