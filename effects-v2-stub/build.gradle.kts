plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-stub"
    description = "A library (internal stub) for simplifying the implementation of one-off events"
}

dependencies {
    implementation(projects.effectsV2CoreApi)
}
