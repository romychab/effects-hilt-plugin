import com.elveum.effects.core.CommandExecutor
import javax.inject.Inject
import kotlin.Int
import kotlin.Number
import kotlin.String

public class __TestInterfaceMediator @Inject constructor(
  private val commandExecutor: CommandExecutor<TestInterface>,
) : TestInterface {
  public override suspend fun coroutineEvent(arg1: String, arg2: Int): Number {
     return commandExecutor.executeCoroutine {
        it.coroutineEvent(arg1, arg2)
     }
  }

  public fun __internalCleanUp() {
    commandExecutor.cleanUp()
  }
}