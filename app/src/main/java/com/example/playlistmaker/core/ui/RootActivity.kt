package com.example.playlistmaker.core.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Включаем отображение контента "под" системными панелями (статус-бар и навигация)
        enableEdgeToEdge()

        setContentView(R.layout.activity_root)

        // 2. Инициализация Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // 3. Обработка системных отступов для BottomNavigationView
        // Это нужно, чтобы меню не "наползало" на системную полоску жестов или кнопки
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        // 4. Управление видимостью BottomNavigationView (ТЗ спринта №19)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Скрываем меню на экране плеера
                R.id.playerFragment -> {
                    bottomNavigationView.visibility = View.GONE
                }
                // На всех остальных экранах (Поиск, Медиатека, Настройки) — показываем
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }
}