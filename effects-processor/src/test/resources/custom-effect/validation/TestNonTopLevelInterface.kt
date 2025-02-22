import com.elveum.effects.annotations.HiltEffect


class TopLevelClass {

    interface Interface

}

@HiltEffect
class TestClass : TopLevelClass.Interface {

}