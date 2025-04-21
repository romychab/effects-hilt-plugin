package com.uandcode.effects.core.runtime.proxy;

import com.uandcode.effects.core.CommandExecutor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

public class ProxyMethodInterceptor<Effect> implements InvocationHandler {

    private final String name;
    private final CommandExecutor<Effect> commandExecutor;
    private final List<Method> abstractMethods;

    public ProxyMethodInterceptor(
        String name,
        CommandExecutor<Effect> commandExecutor,
        List<Method> abstractMethods
    ) {
        this.name = name;
        this.commandExecutor = commandExecutor;
        this.abstractMethods = abstractMethods;
    }

    @Override
    public Object invoke(
        Object proxyObject,
        Method method,
        Object[] args
    ) {
        if (method.getDeclaringClass().equals(AutoCloseable.class)) {
            close();
        } else if (method.getDeclaringClass().equals(Object.class)) {
            return handleObjectMethod(proxyObject, method, args);
        } else if (!abstractMethods.contains(method)) {
            return handleDefaultMethod(proxyObject, method, args);
        } else {
            return handleEffectMethod(method, args);
        }
        return Unit.INSTANCE;
    }

    public void close() {
        commandExecutor.cleanUp();
    }

    private boolean isUnit(Method method) {
        return method.getReturnType() == Unit.class
                || method.getReturnType() == void.class;
    }

    private boolean isFlow(Method method) {
        return method.getReturnType() == Flow.class;
    }

    private boolean isCoroutine(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            return false;
        }
        return paramTypes[paramTypes.length - 1] == Continuation.class;
    }

    private Object handleEffectMethod(Method method, Object[] args) {
        ExecutionContext<Effect> executionContext = new ExecutionContext<>(method, args, commandExecutor);
        if (isCoroutine(method)) {
            return executionContext.executeCoroutine();
        } else if (isFlow(method)) {
            return executionContext.executeFlow();
        } else if (isUnit(method)) {
            executionContext.executeUnitCommand();
            return Unit.INSTANCE;
        } else {
            throw new IllegalArgumentException(
                    "Invalid method definition in the effect interface.\n" +
                            "1. Make sure all methods with a return type have a 'suspend' modifier.\n" +
                            "2. Make sure methods that return Flow<T> don't have a 'suspend' modifier."
            );
        }
    }

    private Object handleObjectMethod(Object proxyObject, Method method, Object[] args) {
        switch (method.getName()) {
            case "toString":
                String interceptorString = toString();
                String interceptorHashCode = interceptorString.substring(
                    interceptorString.indexOf('@')
                );
                return name + interceptorHashCode;
            case "hashCode":
                return hashCode();
            case "equals":
                return proxyObject == args[0];
            default:
                // native methods (like 'notify') are not delivered to proxy
                return Unit.INSTANCE;
        }
    }

    private Object handleDefaultMethod(Object proxyObject, Method method, Object[] args) {
        Method defaultMethod = findDefaultMethod(method);
        if (defaultMethod != null) {
            Object[] defaultArgs = new Object[args.length + 1];
            // the first arg must be a proxyObject itself
            defaultArgs[0] = proxyObject;
            // all other args are copied:
            System.arraycopy(args, 0, defaultArgs, 1, args.length);
            try {
                // invoke as static method:
                return defaultMethod.invoke(null, defaultArgs);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            throw new IllegalStateException("Can't find default method '"
                    + method.getName()
                    + "' for execution on proxy object.");
        }
    }

    private Method findDefaultMethod(Method calledMethod) {
        // Kotlin generates nested class '$DefaultImpls' with
        // static Java methods for Kotlin default methods;
        // we need to find that static method within $DefaultImpls:
        String defaultClassName = name + "$DefaultImpls";
        try {
            Class<?> defaultImplClass = Class.forName(defaultClassName);
            Method[] defaultMethods = defaultImplClass.getDeclaredMethods();
            for (Method defaultMethod : defaultMethods) {
                // match by name at first:
                if (defaultMethod.getName().equals(calledMethod.getName())) {
                    // then match by params
                    Class<?>[] allParameterTypes = defaultMethod.getParameterTypes();
                    // the first param is proxy object itself, so need to skip it
                    Class<?>[] defaultParameterTypes = Arrays.copyOfRange(allParameterTypes, 1, allParameterTypes.length);
                    if (Arrays.equals(defaultParameterTypes, calledMethod.getParameterTypes())) {
                        return defaultMethod;
                    }
                }
            }
        } catch (Exception ignored) { }
        return null;
    }
}
