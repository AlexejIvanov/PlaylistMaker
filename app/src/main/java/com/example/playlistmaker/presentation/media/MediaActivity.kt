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

class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_media)
        val density = resources.displayMetrics.density
        val sidePadding = (16 * density).toInt()


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

        // Кнопка "Назад"
        backArrowImageView.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}