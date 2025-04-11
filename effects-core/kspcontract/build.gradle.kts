plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    artifactId = "effects2-core-kspcontract"
    description = "Effects Core Library - Internal KSP contract for accessing auto-generated code"
}

dependencies {
    implementation(projects.effectsCore.kspcontractApi)
}
