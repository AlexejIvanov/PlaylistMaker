package com.example.playlistmaker



import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

class SearchActivity : AppCompatActivity() {
    private var saveTextInput: String = TEXT_DEF
    private lateinit var recyclerView: RecyclerView
    private lateinit var  trackAdepter: TrackAdepter
    private lateinit var trackList: List<Track>


    companion object {
        const val SAVE_TEXT = "SAVE_TEXT"
        const val TEXT_DEF = ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_TEXT, saveTextInput)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        if(savedInstanceState != null) {
            saveTextInput = savedInstanceState.getString(SAVE_TEXT, TEXT_DEF)
        }

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

        val backArrowImageView = findViewById<ImageView>(R.id.back_button)
        val clearButton = findViewById<ImageView>(R.id.clear_button)
        val searchEditText = findViewById<EditText>(R.id.search_edit_text)

        recyclerView = findViewById(R.id.recycler_view_track)
        trackList = createTrackList()
        trackAdepter = TrackAdepter(trackList)
        recyclerView.adapter =trackAdepter

        searchEditText.setText(saveTextInput)

        // TextWatcher
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                saveTextInput = s?.toString() ?: ""
            }
        })

        // Кнопка "Назад" возвращает в предыдущий экран
        backArrowImageView.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fun hideKeyboard() {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

        // Очистка строки поиска
        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
        }
    }

    private fun createTrackList(): List<Track> {
        return listOf(
            Track(
                getString(R.string.track_1_name),
                getString(R.string.track_1_artist),
                getString(R.string.track_1_time),
                getString(R.string.track_1_url)
            ),
            Track(
                getString(R.string.track_2_name),
                getString(R.string.track_2_artist),
                getString(R.string.track_2_time),
                getString(R.string.track_2_url)
            ),
            Track(
                getString(R.string.track_3_name),
                getString(R.string.track_3_artist),
                getString(R.string.track_3_time),
                getString(R.string.track_3_url)
            ),
            Track(
                getString(R.string.track_4_name),
                getString(R.string.track_4_artist),
                getString(R.string.track_4_time),
                getString(R.string.track_4_url)
            ),
            Track(
                getString(R.string.track_5_name),
                getString(R.string.track_5_artist),
                getString(R.string.track_5_time),
                getString(R.string.track_5_url)
            )
        )
    }
}
