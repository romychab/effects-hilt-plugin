plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-core-api"
    description = "A library (Core API artifact) for simplifying the implementation of one-off events"
}

dependencies {
    api(libs.coroutines.core)
}
