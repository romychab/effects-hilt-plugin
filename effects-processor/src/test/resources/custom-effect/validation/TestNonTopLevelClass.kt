import com.elveum.effects.annotations.HiltEffect

interface Interface

class TopLevelClass {

    @HiltEffect
    class NestedClass : Interface {

    }
}