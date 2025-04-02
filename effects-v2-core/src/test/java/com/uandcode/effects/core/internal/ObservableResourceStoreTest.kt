package com.uandcode.effects.core.internal

import com.uandcode.effects.core.internal.ObservableResourceStore.ResourceObserver
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ObservableResourceStoreTest {

    private lateinit var store: ObservableResourceStoreImpl<String>

    @MockK(relaxed = true)
    private lateinit var observer: ResourceObserver<String>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        store = ObservableResourceStoreImpl()
    }

    @Test
    fun `attachResource should notify observers when a new resource is added`() {
        store.addObserver(observer)

        store.attachResource("Resource1")

        verify(exactly = 1) {
            observer.onResourceAttached("Resource1")
        }
        assertTrue(store.currentAttachedResources.contains("Resource1"))
    }

    @Test
    fun `detachResource should notify observers when a resource is removed`() {
        store.addObserver(observer)
        store.attachResource("Resource1")

        store.detachResource("Resource1")

        verify(exactly = 1) {
            observer.onResourceDetached("Resource1")
        }
        assertFalse(store.currentAttachedResources.contains("Resource1"))
    }

    @Test
    fun `addObserver should notify the new observer of all current resources`() {
        store.attachResource("Resource1")
        store.attachResource("Resource2")

        store.addObserver(observer)

        verifyOrder {
            observer.onResourceAttached("Resource2")
            observer.onResourceAttached("Resource1")
        }
    }

    @Test
    fun `removeObserver should notify the observer of its removal`() {
        store.addObserver(observer)

        store.removeObserver(observer)

        verify(exactly = 1) { observer.onObserverRemoved() }
    }

    @Test
    fun `removeAllObservers should notify all observers of their removal`() {
        val observer1 = mockk<ResourceObserver<String>>(relaxed = true)
        val observer2 = mockk<ResourceObserver<String>>(relaxed = true)
        store.addObserver(observer1)
        store.addObserver(observer2)

        store.removeAllObservers()

        verify(exactly = 1) {
            observer1.onObserverRemoved()
            observer2.onObserverRemoved()
        }
    }

    @Test
    fun `attachResource should not notify observers if the resource already exists`() {
        store.addObserver(observer)
        store.attachResource("Resource1")

        store.attachResource("Resource1")

        verify(exactly = 1) {
            observer.onResourceAttached("Resource1")
        }
    }

    @Test
    fun `removeObserver should do nothing if the observer does not exist`() {
        store.removeObserver(observer)

        verify(exactly = 0) { observer.onObserverRemoved() }
    }

    @Test
    fun `attachObserver should not call removed observer`() {
        every { observer.onResourceAttached(any()) } answers {
            store.removeObserver(observer)
        }
        store.attachResource("Resource1")
        store.attachResource("Resource2")

        store.addObserver(observer)

        verify(exactly = 1) {
            observer.onResourceAttached("Resource2")
        }
        verify(exactly = 0) {
            observer.onResourceAttached("Resource1")
        }
    }

}