import com.elveum.effects.annotations.CustomEffect


class TopLevelClass {

    interface Interface

}

@CustomEffect
class TestClass : TopLevelClass.Interface {

}