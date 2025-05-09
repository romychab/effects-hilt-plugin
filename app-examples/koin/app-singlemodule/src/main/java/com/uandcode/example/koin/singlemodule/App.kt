package com.uandcode.example.koin.singlemodule

import android.app.Application
import com.uandcode.effects.koin.installAnnotatedKoinEffects
import com.uandcode.example.koin.singlemodule.di.singletonsModule
import com.uandcode.example.koin.singlemodule.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {

            androidContext(this@App)
            androidLogger()
            modules(singletonsModule, viewModelsModule)

            installAnnotatedKoinEffects()

        }

    }

}
