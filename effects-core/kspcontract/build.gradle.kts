plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-stub"
    description = "Effects Core Library - Internal KSP contract for accessing auto-generated code"
}

dependencies {
    implementation(projects.effectsCore.kspcontractApi)
}
