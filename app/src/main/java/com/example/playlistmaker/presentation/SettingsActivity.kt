package com.example.playlistmaker.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.ThemeSettings
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        setupWindowInsets()

        val settingsInteractor = Creator.provideSettingsInteractor(this)

        val backButton = findViewById<ImageView>(R.id.back_button)
        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)
        val shareButton = findViewById<TextView>(R.id.share_button)
        val supportButton = findViewById<TextView>(R.id.support_button)
        val agreementButton = findViewById<TextView>(R.id.user_agreement_button)

        // Установка текущего состояния переключателя
        themeSwitch.isChecked = settingsInteractor.getThemeSettings().isDarkTheme

        // Обработка переключения темы
        themeSwitch.setOnCheckedChangeListener { _, checked ->
            settingsInteractor.updateThemeSettings(ThemeSettings(checked))
        }

        backButton.setOnClickListener {
            finish()
        }

        // Поделиться приложением
        shareButton.setOnClickListener {
            val shareText = getString(R.string.share_link)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }

        // Написать в поддержку
        supportButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.address_mail_to_support)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_mail_to_support))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_mail_to_support))
            }
            startActivity(emailIntent)
        }

        // Пользовательское соглашение
        agreementButton.setOnClickListener {
            val url = getString(R.string.link_to_the_user_agreement)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }
    private fun setupWindowInsets() {
        val density = resources.displayMetrics.density
        val sidePadding = (16 * density).toInt()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.settings)) { view, insets ->
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