package com.uandcode.effects.koin.internal

import com.uandcode.effects.koin.exceptions.DuplicateEffectScopeException
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.Test.None
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class EffectScopeClassValidatorTest {

    private lateinit var validator: EffectScopeValidator

    @Before
    fun setUp() {
        validator = EffectScopeValidator()
    }

    @Test(expected = None::class)
    fun `add() with unique qualifiers does not throw exception`() {
        validator.add(StringQualifier("1"))
        validator.add(StringQualifier("2"))
    }

    @Test(expected = DuplicateEffectScopeException::class)
    fun `add() with duplicated qualifiers throws exception`() {
        validator.add(StringQualifier("1"))
        validator.add(StringQualifier("2"))
        validator.add(StringQualifier("1"))
    }

    @Test
    fun `registerEffectScopeValidator() provides Validator instance as singleton`() {
        val koinApplication = koinApplication {
            registerEffectScopeValidator()
        }

        val validator1 = koinApplication.koin.get<EffectScopeValidator>()
        val validator2 = koinApplication.koin.get<EffectScopeValidator>()

        assertSame(validator1, validator2)
    }

    @Test(expected = None::class)
    fun `validateEffectScopeQualifier() validates qualifier during initialization`() {
        val module = module {
            validateEffectScopeQualifier(StringQualifier("1"), 0)
            validateEffectScopeQualifier(StringQualifier("2"), 1)
        }
        koinApplication {
            registerEffectScopeValidator()
            modules(module)
        }
    }

    @Test
    fun `validateEffectScopeQualifier() with duplicated qualifiers fails during initialization`() {
        val module = module {
            validateEffectScopeQualifier(StringQualifier("1"), 0)
            validateEffectScopeQualifier(StringQualifier("2"), 1)
            validateEffectScopeQualifier(StringQualifier("1"), 1)
        }
        val exception = runCatching {
            koinApplication {
                registerEffectScopeValidator()
                modules(module)
            }
        }.exceptionOrNull()

        assertTrue(exception?.cause is DuplicateEffectScopeException)
    }

    @Test
    fun `validateEffectScopeQualifier() registers qualified validator`() {
        val qualifier = StringQualifier("test")
        val id = 123
        val module = module {
            validateEffectScopeQualifier(qualifier, 123)
        }
        val koinApplication = koinApplication {
            registerEffectScopeValidator()
            modules(module)
        }

        val validator1 = koinApplication.koin.get<EffectScopeValidator>(named("effectScope-$qualifier-$id"))
        val validator2 = koinApplication.koin.get<EffectScopeValidator>(named("effectScope-$qualifier-$id"))

        assertSame(validator1, validator2)
    }

}