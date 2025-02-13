import com.elveum.effects.annotations.CustomEffect

interface Interface

class TopLevelClass {

    @CustomEffect
    class NestedClass : Interface {

    }
}