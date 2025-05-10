package com.uandcode.effects.koin.internal

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.RootEffectScopes
import com.uandcode.effects.core.getProxy
import com.uandcode.effects.core.runtime.RuntimeEffectScopes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class InternalKoinEffectScopesTest {

    @Test
    fun `test internalSetupRootEffects()`() {
        val module = module {
            internalSetupRootEffects()
        }
        val koin = koinApplication {
            modules(module)
        }.koin

        val localQualifier = koin.get<LocalEffectQualifier>()
        assertEquals(KOIN_EFFECT_ROOT_QUALIFIER.value, localQualifier.value)

        val scopeWithQualifier = koin.get<EffectScope>(KOIN_EFFECT_ROOT_QUALIFIER)
        assertSame(RootEffectScopes.global, scopeWithQualifier)

        val scope = koin.get<EffectScope>()
        assertSame(RootEffectScopes.global, scope)
    }

    @Test
    fun `test installRootEffects()`() {
        val koin = koinApplication {
            installRootEffects()
        }.koin

        val localQualifier = koin.get<LocalEffectQualifier>()
        assertEquals(KOIN_EFFECT_ROOT_QUALIFIER.value, localQualifier.value)

        val scopeWithQualifier = koin.get<EffectScope>(KOIN_EFFECT_ROOT_QUALIFIER)
        assertSame(RootEffectScopes.global, scopeWithQualifier)

        val scope = koin.get<EffectScope>()
        assertSame(RootEffectScopes.global, scope)
    }

    @Test
    fun `test internalSetupScopedEffects`() {
        val key = "key"
        val rootEffectScope = RuntimeEffectScopes.createGlobal()
        val module = module {
            scope<ScopeClass> {
                internalSetupScopedEffects(
                    key = key,
                    managedInterfaces = ManagedInterfaces.ListOf(Effect1::class, Effect2::class)
                )
            }
        }
        val koin = koinApplication {
            modules(module)
            installRootEffects(rootEffectScope)
        }.koin

        val scope = koin.createScope<ScopeClass>()

        // assert local qualifier
        val localEffectQualifier1 = scope.get<LocalEffectQualifier>()
        val localEffectQualifier2 = scope.get<LocalEffectQualifier>()
        assertEquals(localEffectQualifier1.value, key)
        assertSame(localEffectQualifier1, localEffectQualifier2)

        // assert named effect scopes
        val namedEffectScope1 = scope.get<EffectScope>(named(key))
        val namedEffectScope2 = scope.get<EffectScope>(named(key))
        assertNotSame(namedEffectScope1, rootEffectScope)
        assertSame(namedEffectScope1, namedEffectScope2)
        val namedResult = runCatching { namedEffectScope1.getProxy<Effect1>() }
        assertTrue(namedResult.isSuccess)

        // assert effect scopes
        val effectScope1 = scope.get<EffectScope>(named(key))
        val effectScope2 = scope.get<EffectScope>(named(key))
        assertNotSame(effectScope1, rootEffectScope)
        assertSame(effectScope1, effectScope2)
        val result = runCatching { effectScope1.getProxy<Effect2>() }
        assertTrue(result.isSuccess)
    }

    private interface Effect1
    private interface Effect2
    private class ScopeClass
}