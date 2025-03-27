package com.uandcode.effects.stub

import com.uandcode.effects.core.CommandExecutor
import kotlin.reflect.KClass

/**
 * For internal usage. This is a stub object containing only one
 * method [createMediator] which can create an auto-generated mediator
 * instance of effect interface. By default, it thrown [IllegalStateException],
 * but KSP can create an alternate object with the same name and within
 * the same package when all mediators becomes known.
 */
public object __InternalEffectMediatorFactory {

    /**
     * Create a mediator class which implements interface [T].
     * @throws IllegalStateException if annotations are not used correctly or dependencies are not added to the build.gradle
     * @throws IllegalArgumentException if there is no effect of such interface [T]
     */
    public fun <T : Any> createMediator(
        clazz: KClass<T>,
        commandExecutor: CommandExecutor<T>,
    ): T {
        throw IllegalStateException("@EffectApplication annotation or KSP annotation processor not found.\n" +
                "1. Make sure you've added @EffectApplication annotation to your main application module\n" +
                "2. Review your dependencies in build.gradle file\n" +
                "3. Do not forget to replace EffectFacadeFactory in unit tests by using EffectFacadeFactory.setDefaultFactory() method")
    }

}
