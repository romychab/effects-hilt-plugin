plugins {
    alias(libs.plugins.custom.library)
}

// Not published to Maven Central: this module only provides a compile-time stub of
// com.uandcode.effects.core.kspcontract.AnnotationBasedProxyEffectStore, which the KSP
// processor generates directly into the consumer's project. It is consumed as
// `compileOnly` inside this build only and never appears in any published POM.

dependencies {
    implementation(projects.effectsCore.kspcontractApi)
}
