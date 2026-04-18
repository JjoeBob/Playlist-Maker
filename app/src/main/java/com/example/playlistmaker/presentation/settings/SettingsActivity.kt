package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var shareButton: TextView
    lateinit var supportButton: TextView
    lateinit var agreementButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            windowInsets
        }

        toolbar = findViewById(R.id.settings_toolbar)
        shareButton = findViewById(R.id.settings_share_button)
        supportButton = findViewById(R.id.settings_support_button)
        agreementButton = findViewById(R.id.settings_agreement_button)

        setupToolBar()
        setupShareButton()
        setupSupportButton()
        setupAgreementButton()
    }

    private fun setupToolBar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupShareButton() {
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            }
            startActivity(shareIntent)
        }
    }

    private fun setupSupportButton() {
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            }
            startActivity(supportIntent)
        }
    }

    private fun setupAgreementButton() {
        agreementButton.setOnClickListener {
            val url = Uri.parse(getString(R.string.agreement_link))
            val agreementIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(agreementIntent)
        }
    }
}