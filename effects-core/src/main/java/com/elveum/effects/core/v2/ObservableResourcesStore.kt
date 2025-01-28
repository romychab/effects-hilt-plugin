package com.elveum.effects.core.v2

public interface ObservableResourcesStore<Resource> {

    public val currentAttachedResources: Iterable<Resource>
    public fun attachResource(resource: Resource)
    public fun detachResource(resource: Resource)
    public fun addObserver(observer: ResourceObserver<Resource>)
    public fun removeObserver(observer: ResourceObserver<Resource>)
    public fun removeAllObservers()

    public interface ResourceObserver<Resource> {
        public fun onResourceAttached(resource: Resource)
        public fun onResourceDetached(resource: Resource)
        public fun onObserverRemoved()
    }

}
