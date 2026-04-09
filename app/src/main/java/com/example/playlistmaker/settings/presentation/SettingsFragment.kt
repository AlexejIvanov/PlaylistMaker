package com.example.playlistmaker.settings.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    // Инъекция ViewModel через Koin
    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var shareButton: TextView
    private lateinit var supportButton: TextView
    private lateinit var agreementButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupWindowInsets(view) // Добавление логики отступов

        // Подписка на состояние темы для синхронизации переключателя
        viewModel.themeState.observe(viewLifecycleOwner) { isDark ->
            themeSwitch.isChecked = isDark
        }

        // Слушатель переключения темы
        themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        // Реализация "Поделиться приложением"
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }

        // Реализация "Написать в поддержку"
        supportButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.address_mail_to_support)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_mail_to_support))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_mail_to_support))
            }
            startActivity(emailIntent)
        }

        // Открытие Пользовательского соглашения
        agreementButton.setOnClickListener {
            val url = getString(R.string.link_to_the_user_agreement)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }

    private fun initViews(view: View) {
        themeSwitch = view.findViewById(R.id.theme_switch)
        shareButton = view.findViewById(R.id.share_button)
        supportButton = view.findViewById(R.id.support_button)
        agreementButton = view.findViewById(R.id.user_agreement_button)
    }

    /**
     * Настройка отступов для Edge-to-Edge режима.
     */
    private fun setupWindowInsets(view: View) {
        val rootSettings = view.findViewById<View>(R.id.settings) // ID из вашего XML fragment_settings
        val density = resources.displayMetrics.density
        val sidePadding = (16 * density).toInt()

        ViewCompat.setOnApplyWindowInsetsListener(rootSettings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = sidePadding + systemBars.left,
                top = systemBars.top,      // Отступ под статус-бар
                right = sidePadding + systemBars.right,
                bottom = systemBars.bottom // Отступ снизу для BottomNav
            )
            insets
        }
    }
}