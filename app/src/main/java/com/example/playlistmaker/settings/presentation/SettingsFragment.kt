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

/**
 * Фрагмент экрана настроек: управление темой и взаимодействие с внешними сервисами.
 */
class SettingsFragment : Fragment() {

    // Инъекция ViewModel для работы с бизнес-логикой настроек темы
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
        setupWindowInsets(view)

        // Синхронизация состояния переключателя с текущими настройками темы
        viewModel.themeState.observe(viewLifecycleOwner) { isDark ->
            themeSwitch.isChecked = isDark
        }

        // Обработка ручного переключения темы пользователем
        themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        // Поделиться ссылкой на приложение через системный диалог выбора
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }

        // Отправка письма в поддержку через почтовые приложения
        supportButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.address_mail_to_support)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_mail_to_support))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_mail_to_support))
            }
            startActivity(emailIntent)
        }

        // Переход на веб-страницу с пользовательским соглашением
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


    private fun setupWindowInsets(view: View) {
        val rootSettings = view.findViewById<View>(R.id.settings)
        val density = resources.displayMetrics.density
        val sidePadding = (16 * density).toInt()

        ViewCompat.setOnApplyWindowInsetsListener(rootSettings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = sidePadding + systemBars.left,
                top = systemBars.top,
                right = sidePadding + systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }
    }
}
