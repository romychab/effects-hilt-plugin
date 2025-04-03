package com.uandcode.effects.core.runtime.proxy;

import com.uandcode.effects.core.CommandExecutor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

class ExecutionContext<Effect> {

    final Method method;
    final Object[] args;
    final CommandExecutor<Effect> commandExecutor;

    ExecutionContext(Method method, Object[] args, CommandExecutor<Effect> commandExecutor) {
        this.method = method;
        this.args = args;
        this.commandExecutor = commandExecutor;
    }

    void executeUnitCommand() {
        commandExecutor.execute(effect -> {
            executeOn(effect);
            return Unit.INSTANCE;
        });
    }

    Object executeCoroutine() {
        Continuation<Object> continuation = (Continuation<Object>) args[args.length - 1];
        return commandExecutor.executeCoroutine(
            (effect, nextContinuation) -> {
                Object[] modifiedArgs = Arrays.copyOf(args, args.length);
                modifiedArgs[modifiedArgs.length - 1] = nextContinuation;
                return executeOn(effect, modifiedArgs);
            },
            continuation
        );
    }

    Flow<?> executeFlow() {
        return commandExecutor.executeFlow(effect ->
            (Flow<?>) executeOn(effect)
        );
    }

    private Object executeOn(Effect effect) {
        return executeOn(effect, args);
    }

    private Object executeOn(Effect effect, Object[] args) {
        try {
            Method targetMethod = effect.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
            return targetMethod.invoke(effect, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Can't invoke method " + method.getName(), e);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        }
    }

}
