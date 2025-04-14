plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
    alias(libs.plugins.compose)
}

publishConfig {
    artifactId = "effects2-koin-compose"
    description = "Effects Koin Plugin - Jetpack Compose extension for simplifying the implementation of one-off events"
}

dependencies {
    api(projects.effectsKoin.essentials)
    implementation(projects.effectsCore.compose)
    compileOnly(projects.effectsKoin.kspcontract)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.compose)

    implementation(libs.lifecycle.runtime.compose)
    api(libs.immutable.collections)
}
