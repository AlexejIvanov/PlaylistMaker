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
import com.google.android.material.textview.MaterialTextView

class SearchActivity : AppCompatActivity() {
    private var saveTextInput: String = TEXT_DEF

    companion object {
        const val SAVE_TEXT = "SAVE_TEXT"
        const val TEXT_DEF = ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_TEXT, saveTextInput)
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        super.onRestoreInstanceState(savedInstanceState, persistentState)
//        saveTextInput = savedInstanceState.getString(SAVE_TEXT, TEXT_DEF)
//    }


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
        val sittingButton = findViewById<MaterialTextView>(R.id.settings_button)
        val mediaButton = findViewById<MaterialTextView>(R.id.media_button)

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
}
