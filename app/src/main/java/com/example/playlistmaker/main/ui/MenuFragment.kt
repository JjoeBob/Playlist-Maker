package com.example.playlistmaker.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchButton()
        setupLibraryButton()
        setupSettingsButton()
    }

    private fun setupSearchButton() {
        binding.searchButton.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_searchFragment)
        }
    }

    private fun setupLibraryButton() {
        binding.libraryButton.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_libraryFragment)

        }
    }

    private fun setupSettingsButton() {
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_settingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}