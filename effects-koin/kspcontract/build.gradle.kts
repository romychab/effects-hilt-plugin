plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    artifactId = "effects2-koin-kspcontract"
    description = "Effects Koin Plugin - Internal KSP contract for accessing auto-generated code"
}

dependencies {
    implementation(projects.effectsCore.essentials)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
}