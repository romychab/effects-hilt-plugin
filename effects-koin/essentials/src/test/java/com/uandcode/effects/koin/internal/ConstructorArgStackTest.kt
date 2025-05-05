package com.uandcode.effects.koin.internal

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class ConstructorArgStackTest {

    private lateinit var stack: ConstructorArgStack

    @Before
    fun setUp() {
        stack = ConstructorArgStack()
    }

    @Test
    fun `push() adds new element`() {
        val arg = ConstructorArg()

        stack.push(arg)

        assertSame(arg, stack.peek())
    }

    @Test
    fun `peek() returns latest element`() {
        val arg = ConstructorArg()

        stack.push(ConstructorArg())
        stack.push(ConstructorArg())
        stack.push(arg)

        assertSame(arg, stack.peek())
    }

    @Test
    fun `peek() does not remove latest element from stack`() {
        val arg = ConstructorArg()

        stack.push(ConstructorArg())
        stack.push(arg)

        assertSame(arg, stack.peek())
        assertSame(arg, stack.peek())
    }

    @Test
    fun `pop() returns latest element and removes it from stack`() {
        val arg1 = ConstructorArg()
        val arg2 = ConstructorArg()

        stack.push(arg1)
        stack.push(arg2)

        assertSame(arg2, stack.pop())
        assertSame(arg1, stack.pop())
        assertNull(stack.pop())
    }

    @Test
    fun `clear() removes all elements from stack`() {
        stack.push(ConstructorArg())
        stack.push(ConstructorArg())

        stack.clear()

        assertNull(stack.peek())
        assertEquals(0, stack.count())
    }

    @Test
    fun `popTo() removes latest elements up to specified element`() {
        val arg1 = createArg()
        val arg2 = createArg()
        val arg3 = createArg()
        val arg4 = createArg()
        val arg5 = createArg()
        stack.push(arg1)
        stack.push(arg2)
        stack.push(arg3)
        stack.push(arg4)
        stack.push(arg5)

        val args = stack.popTo(arg2)

        assertEquals(
            listOf(arg3, arg4, arg5),
            args
        )
        assertEquals(2, stack.count())
        assertEquals(arg2, stack.peek())
    }

    @Test
    fun `popTo() with specified latest element  returns empty list`() {
        val arg1 = createArg()
        val arg2 = createArg()
        val arg3 = createArg()
        stack.push(arg1)
        stack.push(arg2)
        stack.push(arg3)

        val args = stack.popTo(arg3)

        assertEquals(emptyList<ConstructorArg>(), args)
        assertEquals(3, stack.count())
        assertEquals(arg3, stack.peek())
    }

    @Test
    fun `popTo() with unknown specified element returns empty list`() {
        val arg1 = createArg()
        val arg2 = createArg()
        val arg3 = createArg()
        stack.push(arg1)
        stack.push(arg2)
        stack.push(arg3)

        val args = stack.popTo(ConstructorArg())

        assertEquals(emptyList<ConstructorArg>(), args)
        assertEquals(3, stack.count())
        assertEquals(arg3, stack.peek())
    }

    @Test
    fun `popTo() does not return items without assigned closeables`() {
        val arg1 = ConstructorArg()
        val arg2 = ConstructorArg()
        val arg3 = ConstructorArg().apply { assignCloseable(mockk()) }
        val arg4 = ConstructorArg()
        val arg5 = ConstructorArg().apply { assignCloseable(mockk()) }
        stack.push(arg1)
        stack.push(arg2)
        stack.push(arg3)
        stack.push(arg4)
        stack.push(arg5)

        val args = stack.popTo(arg2)

        assertEquals(listOf(arg3, arg5), args)
        assertEquals(2, stack.count())
        assertEquals(arg2, stack.peek())
    }

    @Test
    fun `popTo() with specified first item returns all items except first one`() {
        val arg1 = createArg()
        val arg2 = createArg()
        val arg3 = createArg()
        stack.push(arg1)
        stack.push(arg2)
        stack.push(arg3)

        val args = stack.popTo(arg1)

        assertEquals(listOf(arg2, arg3), args)
        assertEquals(1, stack.count())
        assertEquals(arg1, stack.peek())
    }

    @Test
    fun `count() returns current count of elements`() {
        assertEquals(0, stack.count())

        stack.push(ConstructorArg())
        stack.push(ConstructorArg())
        assertEquals(2, stack.count())

        stack.pop()
        assertEquals(1, stack.count())
    }

    private fun createArg() = ConstructorArg().apply {
        assignCloseable(mockk())
    }
}