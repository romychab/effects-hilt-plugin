import com.elveum.effects.core.CommandExecutor
import java.util.concurrent.Callable
import javax.inject.Inject
import kotlin.String

public class __TestInterfaceMediator @Inject constructor(
  private val commandExecutor: CommandExecutor<TestInterface>,
) : TestInterface {
  public override suspend fun <K, T : Callable<K>> coroutineEvent(arg1: String, arg2: T): T {
     return commandExecutor.executeCoroutine {
        it.coroutineEvent(arg1, arg2)
     }
  }

  public fun __internalCleanUp() {
    commandExecutor.cleanUp()
  }
}