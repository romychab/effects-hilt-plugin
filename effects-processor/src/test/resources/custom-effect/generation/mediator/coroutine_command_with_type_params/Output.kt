import com.elveum.effects.core.v2.CommandExecutor
import java.util.concurrent.Callable
import kotlin.String

public class __TestInterfaceMediator(
    private val commandExecutor: CommandExecutor<TestInterface>,
) : TestInterface {

    public override suspend fun <K, T : Callable<K>> coroutineEvent(arg1: String, arg2: T): T {
        return commandExecutor.executeCoroutine {
            it.coroutineEvent(arg1, arg2)
        }
    }

}