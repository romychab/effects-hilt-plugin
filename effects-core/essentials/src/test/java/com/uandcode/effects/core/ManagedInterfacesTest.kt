package com.uandcode.effects.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ManagedInterfacesTest {

    @Test
    fun `contains() for Everything returns true`() {
        assertTrue(ManagedInterfaces.Everything.contains(String::class))
    }

    @Test
    fun `contains() for ListOf returns true for classes in the list`() {
        val listOf = ManagedInterfaces.ListOf(Int::class, String::class)

        assertTrue(listOf.contains(Int::class))
        assertTrue(listOf.contains(String::class))
        assertFalse(listOf.contains(Double::class))
    }

}