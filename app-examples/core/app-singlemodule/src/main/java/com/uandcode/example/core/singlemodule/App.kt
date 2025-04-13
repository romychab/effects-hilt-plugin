package com.uandcode.example.core.singlemodule

import android.app.Application
import com.uandcode.example.core.singlemodule.data.InMemoryCatsRepository
import com.uandcode.example.core.singlemodule.di.Singletons
import com.uandcode.example.core.singlemodule.effects.resources.AndroidResources

class App : Application(), Singletons {

    override fun onCreate() {
        super.onCreate()
        Singletons.init(this)
    }

    override val catsRepository = InMemoryCatsRepository()
    override val resources = AndroidResources(this)

}