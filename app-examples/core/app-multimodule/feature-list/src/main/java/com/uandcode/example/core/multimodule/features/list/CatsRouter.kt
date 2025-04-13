package com.uandcode.example.core.multimodule.features.list

import com.uandcode.example.core.multimodule.features.list.domain.Cat

interface CatsRouter {
    fun launchDetails(cat: Cat)
}
