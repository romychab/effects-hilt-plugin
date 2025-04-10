package com.elveum.effects.example.multimodule.app.data

import com.elveum.effects.example.features.list.domain.Cat as FeatureListCat
import com.elveum.effects.example.features.details.domain.Cat as FeatureDetailsCat

data class Cat(
    override val id: Long,
    override val name: String,
    override val image: String,
    override val details: String,
    override val isLiked: Boolean,
) : FeatureListCat, FeatureDetailsCat

fun FeatureListCat.toDataModel() = this as Cat
fun FeatureDetailsCat.toDataModel() = this as Cat
