import com.elveum.effects.core.v2.CommandExecutor
import java.util.concurrent.Callable
import kotlin.String
import kotlinx.coroutines.flow.Flow

public class TestInterfaceMediator(
    private val commandExecutor: CommandExecutor<TestClass>,
) : TestInterface {

    public override fun <K, T : Callable<K>> flowEvent(arg1: String, arg2: T): Flow<T> {
        return commandExecutor.executeFlow {
            it.flowEvent(arg1, arg2)
        }
    }

}
