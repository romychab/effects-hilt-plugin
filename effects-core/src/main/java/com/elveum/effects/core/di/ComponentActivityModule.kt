package com.elveum.effects.core.di

import android.app.Activity
import androidx.activity.ComponentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
internal object ComponentActivityModule {

    @Provides
    fun provideComponentActivity(
        activity: Activity
    ): ComponentActivity {
        return activity as? ComponentActivity
            ?: throw IllegalStateException("Cant' cast an activity to ComponentActivity")
    }

}