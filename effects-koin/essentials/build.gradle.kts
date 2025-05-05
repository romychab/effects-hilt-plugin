plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
    alias(libs.plugins.ksp)
}

publishConfig {
    artifactId = "effects2-koin"
    description = "Effects Koin Plugin for simplifying the implementation of one-off events."
}

dependencies {
    api(projects.effectsCore.essentials)
    api(projects.effectsCore.runtime)
    api(projects.effectsKoin.annotations)
    compileOnly(projects.effectsKoin.kspcontract)
    testImplementation(projects.effectsKoin.kspcontract)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.viewmodel)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
