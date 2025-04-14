package com.uandcode.example.koin.multimodule.features.list

import com.uandcode.example.koin.multimodule.features.list.domain.Cat

interface CatsRouter {
    fun launchDetails(cat: Cat)
}
