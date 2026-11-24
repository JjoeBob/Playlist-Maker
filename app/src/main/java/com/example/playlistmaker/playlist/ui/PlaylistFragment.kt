package com.example.playlistmaker.playlist.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAddPhoto()
        setupNameField()
        setupDescriptionField()
        setupHintFonts()
        setupToolBar()
    }

    private fun setupAddPhoto() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    showPlaylistImage(uri)
                    viewModel.updateImage(uri)
                }
            }

        binding.addPhotoFrame.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun showPlaylistImage(uri: Uri) {
        binding.addPhotoFrame.visibility = View.GONE
        binding.playlistImage.visibility = View.VISIBLE

        Glide.with(this)
            .load(uri)
            .transform(
                CenterCrop(),
                RoundedCorners(8f.dpToPx(requireContext()))
            )
            .into(binding.playlistImage)
    }

    private fun setupNameField() {
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            val name = text?.toString().orEmpty()
            viewModel.updateName(name)
            binding.createButton.isEnabled = name.isNotBlank()
        }
    }

    private fun setupDescriptionField() {
        binding.descriptionEditText.doOnTextChanged { text, _, _, _ ->
            val description = text?.toString().orEmpty()
            viewModel.updateDescription(description)
        }
    }

    private fun setupHintFonts() {
        val hintTypeface = requireContext().resources.getFont(R.font.ys_display_regular)
        binding.tilName.typeface = hintTypeface
        binding.tilDescription.typeface = hintTypeface
    }

    private fun setupToolBar() {
        binding.playlistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}