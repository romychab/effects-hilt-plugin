import com.elveum.effects.core.v2.CommandExecutor
import kotlin.Int
import kotlin.Number
import kotlin.String

public class __TestInterfaceMediator(
    private val commandExecutor: CommandExecutor<TestInterface>,
) : TestInterface {
    public override suspend fun coroutineEvent(arg1: String, arg2: Int): Number {
        return commandExecutor.executeCoroutine {
            it.coroutineEvent(arg1, arg2)
        }
    }
}
