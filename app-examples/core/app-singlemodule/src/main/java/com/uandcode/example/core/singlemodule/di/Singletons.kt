package com.uandcode.example.core.singlemodule.di

import com.uandcode.example.core.singlemodule.domain.CatsRepository
import com.uandcode.example.core.singlemodule.effects.resources.Resources

interface Singletons {
    val catsRepository: CatsRepository
    val resources: Resources

    companion object : Singletons {
        lateinit var instance: Singletons

        fun init(instance: Singletons) {
            this.instance = instance
        }

        override val catsRepository get() = instance.catsRepository
        override val resources get() = instance.resources
    }
}