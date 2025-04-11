plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
    alias(libs.plugins.ksp)
}

publishConfig {
    artifactId = "effects2-core-essentials"
    description = "Effects Core Library - Main engine which makes effects work."
}

dependencies {
    api(projects.effectsCore.kspcontractApi)
    compileOnly(projects.effectsCore.kspcontract)
    testImplementation(projects.effectsCore.kspcontract)
    testImplementation(projects.effectsCore.runtime)
    testImplementation(projects.effectsCore.annotations)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.flowtest)
}
