package com.uandcode.effects.core.testing.mocks

public interface SimpleEffect1 {
    public fun run1(input: String)
}

public interface SimpleEffect2 {
    public fun run2(input: String)

}

public interface SimpleEffect3 {
    public fun run3(input: String)
}

public class SimpleEffect1Impl : SimpleEffect1 {
    override fun run1(input: String): Unit = Unit
}

public class SimpleEffect2Impl : SimpleEffect2 {
    override fun run2(input: String): Unit = Unit

}

public class SimpleEffect3Impl : SimpleEffect3 {
    override fun run3(input: String): Unit = Unit
}

