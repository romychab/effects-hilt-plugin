plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.elveum"
    artifactId = "effects-annotations"
    description = "Hilt plugin (annotations library) for simplifying the implementation of one-off events"
}
