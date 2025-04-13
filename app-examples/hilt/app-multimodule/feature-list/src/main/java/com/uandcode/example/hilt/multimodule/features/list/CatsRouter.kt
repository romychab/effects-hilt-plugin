package com.uandcode.example.hilt.multimodule.features.list

import com.uandcode.example.hilt.multimodule.features.list.domain.Cat

interface CatsRouter {
    fun launchDetails(cat: Cat)
}
