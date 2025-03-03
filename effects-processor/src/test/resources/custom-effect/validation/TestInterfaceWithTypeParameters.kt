import com.elveum.effects.annotations.HiltEffect

interface BaseInterface<T>

@HiltEffect
class TestInterfaceWithTypeParameters : BaseInterface<String>
