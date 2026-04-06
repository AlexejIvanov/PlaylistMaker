package com.example.playlistmaker.main.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R
import com.example.playlistmaker.media.presentation.MediaActivity
import com.example.playlistmaker.search.presentation.SearchActivity
import com.example.playlistmaker.settings.presentation.SettingsActivity

/**
 * Главный экран приложения.
 * Обеспечивает навигацию между основными разделами: Поиск, Медиатека и Настройки.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Позволяет интерфейсу отображаться под системными панелями (статус-бар и навигация)
        setContentView(R.layout.activity_main)

        val density = resources.displayMetrics.density
        val sidePadding = (16 * density).toInt()

        // Настройка Insets: добавляет отступы, чтобы контент не перекрывался системными барами
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                sidePadding + systemBars.left,
                systemBars.top,
                sidePadding + systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val searchButton = findViewById<Button>(R.id.search_button)
        val mediaButton = findViewById<Button>(R.id.media_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        // Переход на экран поиска
        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // Переход в раздел "Медиатека"
        mediaButton.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        // Переход в настройки
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
