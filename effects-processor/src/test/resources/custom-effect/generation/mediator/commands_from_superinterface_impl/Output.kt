import com.elveum.effects.core.v2.CommandExecutor
import kotlin.Int
import kotlin.String

public class TestInterfaceMediator(
    private val commandExecutor: CommandExecutor<TestClass>,
) : TestInterface {

    public override fun oneTimeEvent(arg1: String, arg2: Int) {
        commandExecutor.execute {
            it.oneTimeEvent(arg1, arg2)
        }
    }

    public override fun abstractSuperMethod(arg: String) {
        commandExecutor.execute {
            it.abstractSuperMethod(arg)
        }
    }

}