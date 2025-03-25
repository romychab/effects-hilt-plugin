import com.elveum.effects.core.CommandExecutor
import javax.inject.Inject
import kotlin.Int
import kotlin.String

public class __TestInterfaceMediator @Inject constructor(
  private val commandExecutor: CommandExecutor<TestInterface>,
) : TestInterface {
  public override fun oneTimeEvent(arg1: String, arg2: Int) {
     commandExecutor.execute {
        it.oneTimeEvent(arg1, arg2)
     }
  }

  public fun __internalCleanUp() {
    commandExecutor.cleanUp()
  }
}