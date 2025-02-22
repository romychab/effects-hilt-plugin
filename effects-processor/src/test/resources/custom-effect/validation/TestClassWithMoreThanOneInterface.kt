import com.elveum.effects.annotations.HiltEffect

interface Interface1
interface Interface2

@HiltEffect
class TestClassWithMoreThanOneInterface : Interface1, Interface2
