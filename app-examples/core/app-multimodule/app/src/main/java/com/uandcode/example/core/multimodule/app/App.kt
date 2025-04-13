package com.uandcode.example.core.multimodule.app

import android.app.Application
import com.uandcode.effects.core.RootEffectScopes
import com.uandcode.effects.core.getProxy
import com.uandcode.example.core.multimodule.app.data.InMemoryCatsRepository
import com.uandcode.example.core.multimodule.effects.api.Dialogs
import com.uandcode.example.core.multimodule.effects.api.Resources
import com.uandcode.example.core.multimodule.effects.api.Toasts
import com.uandcode.example.core.multimodule.effects.api.locator.Locator
import com.uandcode.example.core.multimodule.effects.api.locator.factory
import com.uandcode.example.core.multimodule.effects.api.locator.get
import com.uandcode.example.core.multimodule.effects.api.locator.singleton
import com.uandcode.example.core.multimodule.features.details.CatDetailsRouter
import com.uandcode.example.core.multimodule.features.details.domain.CatDetailsRepository
import com.uandcode.example.core.multimodule.features.list.CatsRouter
import com.uandcode.example.core.multimodule.features.list.domain.CatListRepository

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Locator.setup {

            singleton<Resources> { AndroidResources(applicationContext) }

            singleton { InMemoryCatsRepository() }
            factory<CatListRepository> { get<InMemoryCatsRepository>() }
            factory<CatDetailsRepository> { get<InMemoryCatsRepository>() }

            factory<Dialogs> { RootEffectScopes.global.getProxy() }
            factory<Toasts> { RootEffectScopes.global.getProxy() }
            factory<CatDetailsRouter> { RootEffectScopes.global.getProxy() }
            factory<CatsRouter> { RootEffectScopes.global.getProxy() }

        }
    }
}
