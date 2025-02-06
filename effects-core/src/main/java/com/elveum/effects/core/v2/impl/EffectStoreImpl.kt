package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.EffectController
import com.elveum.effects.core.v2.EffectRecord
import com.elveum.effects.core.v2.EffectStore
import javax.inject.Inject
import kotlin.reflect.KClass

internal class EffectStoreImpl @Inject constructor(
    private val effectRecords: Set<@JvmSuppressWildcards EffectRecord>,
) : EffectStore {

    override fun <T : Any> getEffectController(clazz: KClass<T>): EffectController<T> {
        val effectRecord = effectRecords.firstOrNull {
            it.effectInterfaceClass == clazz || it.effectImplementationClass == clazz
        } ?: throw IllegalArgumentException("Can't find an effect '${clazz.simpleName}'")
        return effectRecord.effectController as EffectController<T>
    }

}
