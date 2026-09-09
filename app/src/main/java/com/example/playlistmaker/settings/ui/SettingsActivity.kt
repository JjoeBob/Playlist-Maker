package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.app.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModel.getFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            windowInsets
        }

        setupToolBar()
        setupShareButton()
        setupSupportButton()
        setupAgreementButton()
        setupThemeSwitcher()
    }

    private fun setupToolBar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupShareButton() {
        binding.shareButton.setOnClickListener {
            viewModel.share()
        }
    }

    private fun setupSupportButton() {
        binding.supportButton.setOnClickListener {
            viewModel.openSupport()
        }
    }

    private fun setupAgreementButton() {
        binding.agreementButton.setOnClickListener {
            viewModel.openAgreement()
        }
    }

    private fun setupThemeSwitcher() {
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSettings(checked)
        }

        viewModel.observeThemeSettingsLiveData().observe(this) { settings ->
            binding.themeSwitcher.isChecked = settings.isDarkTheme
            (applicationContext as App).switchTheme(settings.isDarkTheme)
        }
    }
}