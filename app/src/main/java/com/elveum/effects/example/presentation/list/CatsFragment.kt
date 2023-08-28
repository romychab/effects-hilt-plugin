package com.elveum.effects.example.presentation.list

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import coil.load
import coil.transform.CircleCropTransformation
import com.elveum.container.Container
import com.elveum.effects.example.R
import com.elveum.effects.example.databinding.FragmentCatsBinding
import com.elveum.effects.example.databinding.ItemCatBinding
import com.elveum.effects.example.domain.Cat
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatsFragment : Fragment(R.layout.fragment_cats) {

    private val viewModel by viewModels<CatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCatsBinding.bind(view)
        val adapter = catsAdapter()
        binding.catsRecyclerView.adapter = adapter
        (binding.catsRecyclerView.itemAnimator as DefaultItemAnimator)
            .supportsChangeAnimations = false

        viewModel.catsLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it !is Container.Success
            binding.catsRecyclerView.isVisible = it is Container.Success
            if (it is Container.Success) {
                adapter.submitList(it.value)
            }
        }
    }

    private fun catsAdapter() = simpleAdapter<Cat, ItemCatBinding> {
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
        areContentsSame = { oldItem, newItem -> oldItem == newItem }
        bind {  cat ->
            catNameTextView.text = cat.name
            catDescriptionTextView.text = cat.details
            favoriteImageView.setImageResource(
                if (cat.isLiked)
                    R.drawable.ic_favorite
                else
                    R.drawable.ic_favorite_not
            )
            favoriteImageView.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    root.context,
                    if (cat.isLiked)
                        R.color.active
                    else
                        R.color.inactive
                )
            )
            catImageView.load(cat.image) {
                transformations(CircleCropTransformation())
            }
        }
        listeners {
            root.onClick {  cat ->
                viewModel.launchDetails(cat)
            }
            deleteImageView.onClick { cat ->
                viewModel.delete(cat)
            }
            favoriteImageView.onClick { cat ->
                viewModel.toggleLike(cat)
            }
        }
    }

}