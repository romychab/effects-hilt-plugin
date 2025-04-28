package implementation_package

import com.uandcode.effects.hilt.annotations.HiltEffect
import interface1_package.CompiledEffect1
import interface2_package.CompiledEffect2

// pre-compiled effect for testing KSP code generation which uses
// metadata classes as input sources (a.k.a. testing the library in
// multi-module projects)
@HiltEffect
class CompiledEffectImpl : CompiledEffect1, CompiledEffect2 {
    override fun runEffect1(input1: String) = Unit
    override fun runEffect2(input2: Int) = Unit
}
