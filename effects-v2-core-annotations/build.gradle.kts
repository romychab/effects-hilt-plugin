plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-core-annotations"
    description = "A library (core annotations artifact) for simplifying the implementation of one-off events"
}
