package com.example.playlistmaker.presentation.media

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R

/**
 * Экран "Медиатека".
 */
class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Отрисовка контента за системными панелями (статус-бар и др.)
        setContentView(R.layout.activity_media)

        val density = resources.displayMetrics.density
        val sidePadding = (16 * density).toInt()

        // Настройка отступов для учета системных баров (Status Bar, Navigation Bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.media)) { view, insets ->
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

        // Обработка нажатия кнопки "Назад" через системный диспетчер
        backArrowImageView.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}