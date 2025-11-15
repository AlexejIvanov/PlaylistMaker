package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val density = resources.displayMetrics.density
        val sidePadding = (16 * density).toInt()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById<android.view.View>(R.id.settings)) { view, insets ->
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
        val shareButton = findViewById<TextView>(R.id.share_button)
        val supportButton = findViewById<TextView>(R.id.support_button)
        val userAgreementButton = findViewById<TextView>(R.id.user_agreement_button)

        // Кнопка "Назад" возвращает в предыдущий экран
        backArrowImageView.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //Кнопка "Поделиться приложением" отправляет ссылку на курс Практикума
        shareButton.setOnClickListener {
            val shareText = getString(R.string.share_link)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type ="text/plain"
                putExtra(Intent.EXTRA_TEXT,shareText)
            }
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_link))
            startActivity(chooser)
        }

        // Кнопка "Написать в поддержку" отправляет электронное письмо разработчику
        supportButton.setOnClickListener {
            val recipient = arrayOf(getString(R.string.address_mail_to_support))
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.putExtra(Intent.EXTRA_EMAIL,recipient)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.subject_mail_to_support))
            emailIntent.putExtra(Intent.EXTRA_TEXT, getText(R.string.text_mail_to_support))
            startActivity(emailIntent)
        }

        //Кнопка "Пользовательское соглашение" открывает браузер с сылкой на веб-страницу Соглашения
        userAgreementButton.setOnClickListener {
            val linkAgreement = getString(R.string.link_to_the_user_agreement)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkAgreement))
            startActivity(browserIntent)
        }



    }
}

