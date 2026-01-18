// --- Я веду комментарии к коду для запоминания того, что было сделано ранее. ---
// --- В релизной версии комменты будут убраны. ---

package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.App.Companion.PLAYLIST_MAKER_PREFERENCES
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var saveTextInput: String = TEXT_DEF // Сохраняемый текст в поле поиска

    // --- Объявление UI элементов ---
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backArrowImageView: ImageView

    // Элементы для плейсхолдеров
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNetworkError: LinearLayout
    private lateinit var refreshButton: Button

    // --- Изменяемый список треков ---
    private val trackList = mutableListOf<Track>()

    // История поиска
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyAdapter: TrackAdapter
    private var historyList = mutableListOf<Track>()

    // Элементы интерфейса истории
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button

    // Метод сохранения состояния
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_TEXT, saveTextInput)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        // Инициализация UI
        initViews()

        // Настройка отступов
        setupWindowInsets()

        searchEditText.post {
            searchEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        // 1. Инициализация SearchHistory
        val sharePrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharePrefs)


        // 2. Инициализация адаптера поиска
        trackAdapter = TrackAdapter(trackList) { track ->
            searchHistory.add(track)
            val intent = android.content.Intent(this, PlayerActivity::class.java)
            intent.putExtra(PlayerActivity.TRACK_KEY, com.google.gson.Gson().toJson(track))
            startActivity(intent)
        }
        recyclerView.adapter = trackAdapter

        // 3. Инициализация адаптера истории
        historyAdapter = TrackAdapter(historyList) { track ->
            searchHistory.add(track)
            historyList.clear()
            historyList.addAll(searchHistory.read())
            historyAdapter.notifyDataSetChanged()
            val intent = android.content.Intent(this, PlayerActivity::class.java)
            intent.putExtra(PlayerActivity.TRACK_KEY, com.google.gson.Gson().toJson(track))
            startActivity(intent)
        }
        historyRecyclerView.adapter = historyAdapter

        // Восстановление текста
        if (savedInstanceState != null) {
            saveTextInput = savedInstanceState.getString(SAVE_TEXT, TEXT_DEF)
            searchEditText.setText(saveTextInput)
        }

        // --- Слушатели ---

        // 1. Кнопка "Назад"
        backArrowImageView.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 2. Кнопка "Очистить"
        clearButton.setOnClickListener {
            searchEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            hideKeyboard()
            showMessage(SearchResult.NO_RESULTS_OR_CLEAR)
        }

        // 3. Изменение текста и логика показа истории
        searchEditText.doOnTextChanged { s, _, _, _ ->
            clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            if (searchEditText.hasFocus() && s?.isEmpty() == true && searchHistory.read()
                    .isNotEmpty()
            ) {
                recyclerView.visibility = View.GONE
                placeholderNothingFound.visibility = View.GONE
                placeholderNetworkError.visibility = View.GONE
                historyList.clear()
                historyList.addAll(searchHistory.read())
                historyAdapter.notifyDataSetChanged()
                historyLayout.visibility = View.VISIBLE

            } else {
                historyLayout.visibility = View.GONE
            }
        }


        // 4. Нажатие "Done" на клавиатуре
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchEditText.text.isNotEmpty()) {
                    performSearch()
                } else {
                    hideKeyboard()
                    showMessage(SearchResult.NO_RESULTS_OR_CLEAR)
                }
                true
            } else {
                false
            }
        }

        // 5. Кнопка "Обновить"
        refreshButton.setOnClickListener {
            performSearch()
        }

        // 6. Обработка логики фокуса
        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty() && searchHistory.read().isNotEmpty()) {

                historyList.clear()
                historyList.addAll(searchHistory.read())
                historyAdapter.notifyDataSetChanged()
                historyLayout.visibility = View.VISIBLE
            } else {
                historyLayout.visibility = View.GONE
            }

        }
        // 7. Очищаем поиск
        clearHistoryButton.setOnClickListener {
            searchHistory.clear()
            historyList.clear()
            historyAdapter.notifyDataSetChanged()
            historyLayout.visibility = View.GONE
        }
    }


    // Инициализация View-элементов
    private fun initViews() {
        backArrowImageView = findViewById(R.id.back_button)
        clearButton = findViewById(R.id.clear_button)
        searchEditText = findViewById(R.id.search_edit_text)
        recyclerView = findViewById(R.id.recycler_view_track)
        placeholderNothingFound = findViewById(R.id.placeholder_nothing_found)
        placeholderNetworkError = findViewById(R.id.placeholder_network_error)
        refreshButton = findViewById(R.id.refresh_button)
        historyLayout = findViewById(R.id.history_layout)
        historyRecyclerView = findViewById(R.id.recycler_view_history)
        clearHistoryButton = findViewById(R.id.clear_button_history)
    }

    // Настройка Edge-to-Edge (работы с системными отступами)
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

    // --- ОСНОВНАЯ ЛОГИКА ПОИСКА ---
    // Этот метод выполняет запрос к API iTunes
    private fun performSearch() {
        // 1. Скрываем клавиатуру, чтобы пользователю было удобно смотреть результаты
        hideKeyboard()

        val searchText = searchEditText.text.toString()

        // 2. Проверка на пустоту: если пользователь ничего не ввел,
        // то нет смысла делать запрос к серверу. Просто очищаем экран.
        if (searchText.isEmpty()) {
            showMessage(SearchResult.NO_RESULTS_OR_CLEAR)
            return
        }

        // 3. Вызов Retrofit

        ItunesClient.itunesApiService.search(searchText).enqueue(object : Callback<ITunesResponse> {

            // onResponse вызывается, когда сервер прислал какой-то ответ
            override fun onResponse(
                call: Call<ITunesResponse>, response: Response<ITunesResponse>
            ) {
                // Проверяем код ответа. 200 означает "Всё хорошо"
                if (response.code() == 200) {
                    trackList.clear() // Очищаем старые результаты

                    // Проверяем, что список результатов (results) не пустой
                    if (response.body()?.results?.isNotEmpty() == true) {
                        // УСПЕХ: Добавляем треки в список и обновляем адаптер
                        trackList.addAll(response.body()!!.results)
                        trackAdapter.notifyDataSetChanged()
                        showMessage(SearchResult.SUCCESS)
                    } else {
                        // ПУСТО: Сервер ответил 200, но ничего не нашел по запросу
                        showMessage(SearchResult.EMPTY)
                    }
                } else {
                    // ОШИБКА СЕРВЕРА: Код ответа не 200 (например 404, 500)
                    showMessage(SearchResult.ERROR)
                }
            }

            // onFailure вызывается, если запрос вообще не ушел или оборвался
            // (например, нет интернета на телефоне)
            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                showMessage(SearchResult.ERROR)
            }
        })
    }

    // Перечисление (Enum) возможных состояний экрана.
    enum class SearchResult {
        // Данные успешно загружены
        SUCCESS,

        // Поиск прошел, но ничего не найдено
        EMPTY,

        // Ошибка сети или сервера
        ERROR,

        // Поле поиска пустое или очищено
        NO_RESULTS_OR_CLEAR
    }

    // Метод управления видимостью (State Management)
    // Он переключает слои (View): либо список треков, либо одну из заглушек.
    private fun showMessage(result: SearchResult) {
        // Скрываем основные элементы результатов и плейсхолдеры
        recyclerView.visibility = View.GONE
        placeholderNothingFound.visibility = View.GONE
        placeholderNetworkError.visibility = View.GONE


        historyLayout.visibility = View.GONE

        when (result) {
            SearchResult.SUCCESS -> {
                recyclerView.visibility = View.VISIBLE
            }

            SearchResult.EMPTY -> {
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                placeholderNothingFound.visibility = View.VISIBLE
            }

            SearchResult.ERROR -> {
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                placeholderNetworkError.visibility = View.VISIBLE
            }

            SearchResult.NO_RESULTS_OR_CLEAR -> {
                // 1. Очищаем результаты поиска
                trackList.clear()
                trackAdapter.notifyDataSetChanged()

                // 2. ТЕПЕРЬ ПРОВЕРЯЕМ ИСТОРИЮ ЗДЕСЬ
                // Если история не пустая — показываем её
                val history = searchHistory.read()
                if (history.isNotEmpty()) {
                    historyList.clear()
                    historyList.addAll(history)
                    historyAdapter.notifyDataSetChanged()
                    historyLayout.visibility = View.VISIBLE
                } else {
                    historyLayout.visibility = View.GONE
                }
            }
        }
    }

    // Метод для скрытия клавиатуры.
    // Получает системный сервис ввода и принудительно прячет клавиатуру для текущего окна.
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    // Companion Object для хранения констант.
    // Используется для сохранения текста поискового запроса при пересоздании Activity
    // (например, при повороте экрана).
    companion object {
        const val SAVE_TEXT = "SAVE_TEXT"
        const val TEXT_DEF = ""
    }
}