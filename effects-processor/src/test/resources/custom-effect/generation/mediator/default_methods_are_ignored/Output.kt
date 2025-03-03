import com.elveum.effects.core.CommandExecutor
import kotlin.Int
import kotlin.String

public class __TestInterfaceMediator(
    private val commandExecutor: CommandExecutor<TestInterface>,
) : TestInterface {

    public override fun oneTimeEvent(arg1: String, arg2: Int) {
        commandExecutor.execute {
            it.oneTimeEvent(arg1, arg2)
        }
    }

}