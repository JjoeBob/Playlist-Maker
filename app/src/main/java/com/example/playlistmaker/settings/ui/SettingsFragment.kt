package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.app.App
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()
        setupShareButton()
        setupSupportButton()
        setupAgreementButton()
        setupThemeSwitcher()
    }

    private fun setupToolBar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
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

        viewModel.observeThemeSettingsLiveData().observe(viewLifecycleOwner) { settings ->
            binding.themeSwitcher.isChecked = settings.isDarkTheme
            (requireActivity().application as App).switchTheme(settings.isDarkTheme)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}