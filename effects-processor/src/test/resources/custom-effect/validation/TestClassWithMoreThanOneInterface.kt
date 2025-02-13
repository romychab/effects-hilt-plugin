import com.elveum.effects.annotations.CustomEffect

interface Interface1
interface Interface2

@CustomEffect
class TestClassWithMoreThanOneInterface : Interface1, Interface2
