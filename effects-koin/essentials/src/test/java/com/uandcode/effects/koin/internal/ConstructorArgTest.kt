package com.uandcode.effects.koin.internal

import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.Test.None

class ConstructorArgTest {

    private lateinit var constructorArg: ConstructorArg

    @Before
    fun setUp() {
        constructorArg = ConstructorArg()
    }

    @Test
    fun `hasCloseable() without assigned closeable returns false`() {
        assertFalse(constructorArg.hasCloseable())
    }

    @Test
    fun `hasCloseable() with assigned closeable returns true`() {
        constructorArg.assignCloseable(mockk())
        assertTrue(constructorArg.hasCloseable())
    }

    @Test(expected = None::class)
    fun `close() without assigned closeable does not fail`() {
        constructorArg.close()
    }

    @Test
    fun `close() with assigned closeable closes it`() {
        val closeable = mockk<AutoCloseable>(relaxUnitFun = true)
        constructorArg.assignCloseable(closeable)

        constructorArg.close()

        verify(exactly = 1) {
            closeable.close()
        }
    }

    @Test
    fun `close() with assigned closeable closes it once`() {
        val closeable = mockk<AutoCloseable>(relaxUnitFun = true)
        constructorArg.assignCloseable(closeable)

        constructorArg.close()
        constructorArg.close()

        verify(exactly = 1) {
            closeable.close()
        }
    }

}
