package com.elveum.effects.example.presentation.details

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.elveum.container.Container
import com.elveum.effects.example.R
import com.elveum.effects.example.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val viewModel by viewModels<DetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailsBinding.bind(view)

        viewModel.catLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it is Container.Pending
            binding.contentContainer.isVisible = it is Container.Success
            if (it is Container.Success) {
                binding.catNameTextView.text = it.value.name
                binding.catDetailsTextView.text = it.value.details
                binding.catImageView.load(it.value.image) {
                    transformations(CircleCropTransformation())
                }
            }
        }

        binding.goBackButton.setOnClickListener {
            viewModel.goBack()
        }
    }

}