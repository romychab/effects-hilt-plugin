package com.elveum.effects.example.features.list

import com.elveum.effects.example.features.list.domain.Cat

interface CatsRouter {
    fun launchDetails(cat: Cat)
}
