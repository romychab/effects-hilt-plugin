package com.uandcode.example.core.multimodule.features.list

import androidx.compose.runtime.Stable
import com.uandcode.example.core.multimodule.effects.api.Dialogs
import com.uandcode.example.core.multimodule.effects.api.Resources
import com.uandcode.example.core.multimodule.effects.api.Toasts
import com.uandcode.example.core.multimodule.features.list.domain.CatListRepository

@Stable
interface CatsListDependencyContainer {
    val catListRepository: CatListRepository
    val router: CatsRouter
    val dialogs: Dialogs
    val toasts: Toasts
    val resources: Resources
}
