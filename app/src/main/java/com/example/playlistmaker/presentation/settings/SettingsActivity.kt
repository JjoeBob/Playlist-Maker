package com.example.playlistmaker.presentation.settings

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App
import com.example.playlistmaker.domain.models.ThemeSettings
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private val settingsInteractor by lazy { Creator.provideSettingsInteractor(this) }
    private val sharingInteractor by lazy { Creator.provideSharingInteractor(this) }

    lateinit var toolbar: Toolbar
    lateinit var shareButton: TextView
    lateinit var supportButton: TextView
    lateinit var agreementButton: TextView
    lateinit var themeSwitcher: SwitchMaterial

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
        themeSwitcher = findViewById(R.id.themeSwitcher)

        setupToolBar()
        setupShareButton()
        setupSupportButton()
        setupAgreementButton()
        setupThemeSwitcher()
    }

    private fun setupToolBar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupShareButton() {
        shareButton.setOnClickListener {
            sharingInteractor.share()
        }
    }

    private fun setupSupportButton() {
        supportButton.setOnClickListener {
            sharingInteractor.openSupport()
        }
    }

    private fun setupAgreementButton() {
        agreementButton.setOnClickListener {
            sharingInteractor.openAgreement()
        }
    }

    private fun setupThemeSwitcher() {
        themeSwitcher.isChecked = settingsInteractor.getThemeSettings().isDarkTheme
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)

            settingsInteractor.updateThemeSettings(ThemeSettings(checked))
        }
    }
}