package com.uandcode.effects.core.internal

/**
 * For internal usage.
 *
 * This interface is a store of resources that can be observed by
 * [ResourceObserver] instances. Resources are compared using equals/hashCode,
 * meaning the same resource cannot be added twice. The same rule applies
 * to observers.
 *
 * @param Resource The type of resource being managed.
 */
internal interface ObservableResourceStore<Resource> {

    /**
     * The currently attached resources maintained in reverse order of addition
     */
    val currentAttachedResources: Iterable<Resource>

    /**
     * Add a new resource to the store.
     *
     * If the resource is already attached, no action is taken.
     *
     * @param resource The resource to be attached.
     */
    fun attachResource(resource: Resource)

    /**
     * Remove a resource from the store.
     *
     * If the resource is not attached, no action is taken.
     *
     * @param resource The resource to be detached.
     */
    fun detachResource(resource: Resource)

    /**
     * Adds an observer to monitor resource changes.
     *
     * If the observer is already added, no action is taken.
     * Observers added via this method are immediately notified about currently
     * added resources in reverse order of their addition.
     *
     * @param observer The observer to be added.
     */
    fun addObserver(observer: ResourceObserver<Resource>)

    /**
     * Removes an observer from monitoring resource changes.
     *
     * If the observer is not present, no action is taken.
     *
     * @param observer The observer to be removed.
     */
    fun removeObserver(observer: ResourceObserver<Resource>)

    /**
     * Removes all observers from the store.
     */
    fun removeAllObservers()

    /**
     * An observer interface for monitoring resource attachments and detachments.
     *
     * @param Resource The type of resource being observed.
     */
    interface ResourceObserver<Resource> {

        /**
         * Called when a resource is attached.
         *
         * @param resource The resource that was attached.
         */
        fun onResourceAttached(resource: Resource)

        /**
         * Called when a resource is detached.
         *
         * @param resource The resource that was detached.
         */
        fun onResourceDetached(resource: Resource)

        /**
         * Called when the observer is removed.
         */
        fun onObserverRemoved()

    }

}
